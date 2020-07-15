package com.example.donategood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.donategood.R;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.models.Offering;

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

        private LoadPost loadPost;

        public SmallViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvUser = itemView.findViewById(R.id.tvUser);
            ivOfferingPhoto = itemView.findViewById(R.id.ivOfferingPhoto);

            loadPost = new LoadPost();

            itemView.setOnClickListener(this);
        }

        public void bind(Offering offering) {
            loadPost.setTitlePriceUser(offering, tvTitle, tvPrice, tvUser);
            loadPost.setPostImage(offering.getImage(), context, ivOfferingPhoto);
        }
    }
}
