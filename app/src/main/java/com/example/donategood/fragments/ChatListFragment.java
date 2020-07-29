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
import com.example.donategood.adapters.UserAdapter;
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
    
    private ChatListAdapter adapter;
    private RecyclerView rvChatPreview;
    private ArrayList<String> userIdList;
    private Query query;
    private FloatingActionButton btnNewChat;
    private TextView tvChatWithTitle;

    private UserAdapter userAdapter;
    private List<ParseUser> allUsers;
    private RecyclerView rvUsers;

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

        rvChatPreview = view.findViewById(R.id.rvChatPreview);
        btnNewChat = view.findViewById(R.id.fabNewChat);
        tvChatWithTitle = view.findViewById(R.id.tvViewAllUsersTitle);

        query = new Query();
        userIdList = new ArrayList<>();
        adapter = new ChatListAdapter(getContext(), userIdList);

        rvChatPreview.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvChatPreview.setLayoutManager(linearLayoutManager);

        allUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), allUsers);
        rvUsers = view.findViewById(R.id.rvUsers);

        rvUsers.setAdapter(userAdapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(linearLayoutManager2);

        //find all users on app
        query.findAllUsers(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error getting all users");
                }
                allUsers.clear();
                allUsers.addAll(objects);
                userAdapter.notifyDataSetChanged();
            }
        });

        rvUsers.setVisibility(View.INVISIBLE);
        tvChatWithTitle.setVisibility(View.INVISIBLE);

        btnNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnNewChat clicked");
                rvUsers.setVisibility(View.VISIBLE);
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
                    if (userIds.get(0).equals(ParseUser.getCurrentUser().getObjectId())) {
                        if (!userIdList.contains(userIds.get(1))) {
                            userIdList.add(userIds.get(1));
                        }
                    } else if (userIds.get(1).equals(ParseUser.getCurrentUser().getObjectId())) {
                        if (!userIdList.contains(userIds.get(0))) {
                            userIdList.add(userIds.get(0));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
