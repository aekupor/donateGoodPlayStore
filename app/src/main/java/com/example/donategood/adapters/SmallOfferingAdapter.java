package com.example.donategood.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.donategood.R;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SmallOfferingAdapter extends OfferingAdapter {

    public SmallOfferingAdapter(Context context, List<Offering> offerings) {
        super(context, offerings);
    }

    @NonNull
    @Override
    public SmallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_small_offering, parent, false);
        return new SmallViewHolder(view);
    }

    class SmallViewHolder extends ViewHolder {

        private TextView tvTitle;
        private TextView tvPrice;
        private TextView tvUser;
        private ImageView ivOfferingPhoto;
        private TextView tvBoughtBy;

        private LoadPost loadPost;

        public SmallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvUser = itemView.findViewById(R.id.tvUser);
            ivOfferingPhoto = itemView.findViewById(R.id.ivOfferingPhoto);
            tvBoughtBy = itemView.findViewById(R.id.tvBoughtBy);

            loadPost = new LoadPost();

            itemView.setOnClickListener(this);
        }

        public void bind(Offering offering) {
            loadPost.setTitlePriceUser(offering, tvTitle, tvPrice, tvUser);
            loadPost.setPostImage(offering.getImage(), context, ivOfferingPhoto);

            if (context == null) {
                return;
            }

            String boughtList = "Bought by: ";
            ArrayList<Object> boughtUsers = offering.getBoughtByArray();

            if (boughtUsers != null && !boughtUsers.isEmpty()) {
                for (Object object : boughtUsers) {
                    ParseUser user = (ParseUser) object;
                    try {
                        user = user.fetchIfNeeded();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    boughtList = boughtList + user.getUsername() + ", ";
                }

                tvBoughtBy.setText(boughtList);
            } else {
                tvBoughtBy.setText("");
            }
        }
    }
}
