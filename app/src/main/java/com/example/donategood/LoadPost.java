package com.example.donategood;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donategood.models.Charity;
import com.example.donategood.models.Offering;
import com.parse.ParseException;
import com.parse.ParseFile;

public class LoadPost {

    public void setCharity(Offering offering, Context context, TextView tvCharity, ImageView ivCharityProfile) {
        if (offering.getCharity() != null) {
            try {
                Charity charity = offering.getCharity().fetchIfNeeded();
                tvCharity.setText(charity.getTitle());
                ivCharityProfile.setImageDrawable(null);
                ParseFile charityImage = offering.getCharity().getImage();
                if (charityImage != null) {
                    Glide.with(context)
                            .load(charityImage.getUrl())
                            .into(ivCharityProfile);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            tvCharity.setText("No charity currently");
        }
    }
}
