package com.example.donategood.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donategood.R;
import com.example.donategood.adapters.ChatListAdapter;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatListFragment extends Fragment {

    public static final String TAG = "ChatListFragment";

    private Query query;
    private FloatingActionButton btnNewChat;
    private TextView tvChatWithTitle;

    private ChatListAdapter adapterChatList;
    private RecyclerView rvChat;
    private ArrayList<String> chatUserList;

    private ChatListAdapter adapterNoChatList;
    private ArrayList<String> noChatYetUserList;
    private RecyclerView rvNoChat;

    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.i(TAG, "onViewCreated chat list fragment");

        rvChat = view.findViewById(R.id.rvChatPreview);
        btnNewChat = view.findViewById(R.id.fabNewChat);
        tvChatWithTitle = view.findViewById(R.id.tvViewAllUsersTitle);

        query = new Query();
        chatUserList = new ArrayList<>();
        adapterChatList = new ChatListAdapter(getContext(), chatUserList);

        rvChat.setAdapter(adapterChatList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvChat.setLayoutManager(linearLayoutManager);

        noChatYetUserList = new ArrayList<>();
        adapterNoChatList = new ChatListAdapter(getContext(), noChatYetUserList);
        rvNoChat = view.findViewById(R.id.rvUsers);

        rvNoChat.setAdapter(adapterNoChatList);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        rvNoChat.setLayoutManager(linearLayoutManager2);

        rvNoChat.setVisibility(View.INVISIBLE);
        tvChatWithTitle.setVisibility(View.INVISIBLE);

        btnNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnNewChat clicked");
                rvNoChat.setVisibility(View.VISIBLE);
                tvChatWithTitle.setVisibility(View.VISIBLE);
            }
        });
        
        findChatUsers();
    }

    //find users that the current user has chatted with previously
    private void findChatUsers() {
        query.queryAllChats(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error getting messages");
                    return;
                }
                for (Message message : objects) {
                    List<String> userIds = new ArrayList<String>(Arrays.asList(message.getRoomId().split(" ")));
                    //add other user's id to userIdList
                    if (userIds.get(0).equals(ParseUser.getCurrentUser().getObjectId())) {
                        if (!chatUserList.contains(userIds.get(1))) {
                            chatUserList.add(userIds.get(1));
                        }
                    } else if (userIds.get(1).equals(ParseUser.getCurrentUser().getObjectId())) {
                        if (!chatUserList.contains(userIds.get(0))) {
                            chatUserList.add(userIds.get(0));
                        }
                    }
                }
                adapterChatList.notifyDataSetChanged();

                //find all users on app
                query.findAllUsers(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "error getting all users");
                        }
                        for (ParseUser user : objects) {
                            if (!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId()) && !chatUserList.contains(user.getObjectId())) {
                                //only add user to noChatYetUserList if user is not the current user and is not already in chatUserList
                                noChatYetUserList.add(user.getObjectId());
                            }
                        }
                        adapterNoChatList.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
