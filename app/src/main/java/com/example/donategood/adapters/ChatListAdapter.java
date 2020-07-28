package com.example.donategood.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donategood.R;
import com.example.donategood.helperClasses.LoadPost;
import com.parse.ParseUser;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    public static final String TAG = "UserAdapter";

    private Context context;
    private List<ParseUser> users;

    public ChatListAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ParseUser> list) {
        users.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvUsername;
        ImageView ivProfileImage;

        LoadPost loadPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvUserUsername);
            ivProfileImage = itemView.findViewById(R.id.ivUserProfileImage);

            loadPost = new LoadPost();

            itemView.setOnClickListener(this);
        }

        public void bind(ParseUser user) {
            loadPost.setUser(user, context, tvUsername, ivProfileImage);
        }

        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ParseUser user = users.get(position);
                Log.i(TAG, "user clicked: " + user.getUsername());

                //go to that user's chat

            }
        }
    }
}
