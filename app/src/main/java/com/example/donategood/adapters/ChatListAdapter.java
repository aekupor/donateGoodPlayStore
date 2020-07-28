package com.example.donategood.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donategood.ChatActivity;
import com.example.donategood.R;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.helperClasses.Query;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    public static final String TAG = "UserAdapter";

    private Context context;
    private List<String> userIds;

    public ChatListAdapter(Context context, List<String> users) {
        this.context = context;
        this.userIds = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String userId = userIds.get(position);
        holder.bind(userId);
    }

    @Override
    public int getItemCount() {
        return userIds.size();
    }

    public void clear() {
        userIds.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<String> list) {
        userIds.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvUsername;
        ImageView ivProfileImage;

        LoadPost loadPost;
        Query query;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvOtherPersonChatName);
            ivProfileImage = itemView.findViewById(R.id.ivChatOtherPersonProfile);

            loadPost = new LoadPost();
            query = new Query();

            itemView.setOnClickListener(this);
        }

        public void bind(String userId) {
            query.findUserById(userId, new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "issue getting users");
                        return;
                    }
                    loadPost.setUser(objects.get(0), context, tvUsername, ivProfileImage);
                }
            });
        }

        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String userId = userIds.get(position);
                Log.i(TAG, "user clicked with id: " + userId);

                query.findUserById(userId, new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "issue getting users");
                            return;
                        }

                        //go to that user's chat
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("user", Parcels.wrap(objects.get(0)));
                        context.startActivity(intent);
                    }
                });
            }
        }
    }
}
