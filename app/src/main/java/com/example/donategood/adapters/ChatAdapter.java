package com.example.donategood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.donategood.R;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Message;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Message> mMessages;
    private Context mContext;
    private String mUserId;
    private ParseUser savedUser;

    public ChatAdapter(Context context, String userId, List<Message> messages) {
        mMessages = messages;
        this.mUserId = userId;
        mContext = context;
    }

    public void clear() {
        mMessages.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_chat, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Message message = mMessages.get(position);

        boolean isMe = false;
        if (message.getUserId().equals(ParseUser.getCurrentUser().getObjectId())) {
            isMe = true;
        }

        if (isMe) {
            //if chat is from current user
            holder.imageMe.setVisibility(View.VISIBLE);
            holder.imageOther.setVisibility(View.INVISIBLE);
            holder.username.setVisibility(View.INVISIBLE);
            holder.body.setVisibility(View.INVISIBLE);
            holder.imageMe.setVisibility(View.VISIBLE);
            holder.usernameRight.setVisibility(View.VISIBLE);
            holder.bodyRight.setVisibility(View.VISIBLE);
            holder.bodyRight.setText(message.getBody());
            holder.loadPost.setUser(ParseUser.getCurrentUser(), mContext, holder.usernameRight, holder.imageMe);
        } else {
            //if chat is from other user
            holder.imageOther.setVisibility(View.VISIBLE);
            holder.imageMe.setVisibility(View.INVISIBLE);
            holder.usernameRight.setVisibility(View.INVISIBLE);
            holder.bodyRight.setVisibility(View.INVISIBLE);
            holder.imageOther.setVisibility(View.VISIBLE);
            holder.username.setVisibility(View.VISIBLE);
            holder.body.setVisibility(View.VISIBLE);
            holder.body.setText(message.getBody());

            if (savedUser != null && message.getUserId().equals(savedUser.getObjectId())) {
                holder.loadPost.setUser(savedUser, mContext, holder.username, holder.imageOther);
            } else {
                //have to query to find other user
                holder.query.findUserById(message.getUserId(), new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e != null) {
                            return;
                        }
                        savedUser = objects.get(0);
                        holder.loadPost.setUser(objects.get(0), mContext, holder.username, holder.imageOther);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOther;
        ImageView imageMe;
        TextView body;
        TextView username;
        TextView bodyRight;
        TextView usernameRight;
        LoadPost loadPost;
        Query query;

        public ViewHolder(View itemView) {
            super(itemView);

            loadPost = new LoadPost();
            query = new Query();

            imageOther = itemView.findViewById(R.id.ivProfileOther);
            imageMe = itemView.findViewById(R.id.ivProfileMe);
            body = itemView.findViewById(R.id.tvBody);
            username = itemView.findViewById(R.id.tvChatUser);
            bodyRight = itemView.findViewById(R.id.tvBodyRight);
            usernameRight = itemView.findViewById(R.id.tvChatUserRight);
        }
    }
}