package com.example.donategood.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donategood.R;
import com.example.donategood.adapters.ChatListAdapter;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    public static final String TAG = "ChatListFragment";
    
    private ChatListAdapter adapter;
    private RecyclerView rvChatPreview;
    private ArrayList<String> userIdList;
    private Query query;

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

        query = new Query();
        rvChatPreview = view.findViewById(R.id.rvChatPreview);
        users = new ArrayList<>();
        adapter = new ChatListAdapter(getContext(), users);

        rvChatPreview.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvChatPreview.setLayoutManager(linearLayoutManager);
        
        findChatUsers();
    }

    private void findChatUsers() {
        query.queryAllChats(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error getting messages");
                    return;
                }
                for (Message message : objects) {
                    String[] userIds = message.getRoomId().split(" ");
                    if (userIds[0].equals(ParseUser.getCurrentUser().getObjectId())) {
                        userIdList.add(userIds[1]);
                    } else if (userIds[1].equals(ParseUser.getCurrentUser().getObjectId())) {
                        userIdList.add(userIds[0]);
                    }
                }
            }
        });
    }
}
