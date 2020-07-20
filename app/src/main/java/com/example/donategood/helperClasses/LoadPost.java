package com.example.donategood.helperClasses;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donategood.R;
import com.example.donategood.models.Charity;
import com.example.donategood.models.Offering;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;

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

    public void setCharityWithCharity(Charity charity, Context context, TextView tvCharity, ImageView ivCharityProfile) {
            tvCharity.setText(charity.getTitle());
            ivCharityProfile.setImageDrawable(null);
            ParseFile charityImage = charity.getImage();
            if (charityImage != null) {
                Glide.with(context)
                        .load(charityImage.getUrl())
                        .into(ivCharityProfile);
            }
    }

    public void setPostImage(ParseFile image, Context context, ImageView ivOfferingPhoto) {
        ivOfferingPhoto.setImageDrawable(null);
        if (image != null) {
            Glide.with(context)
                    .load(image.getUrl())
                    .into(ivOfferingPhoto);
        }
    }

    public void setTags(ArrayList<String> tags, TextView tvTagList) {
        if (tags != null) {
            String tagList = "";
            for (String tag : tags) {
                if (tagList == "") {
                    tagList = tag;
                } else {
                    tagList += ", " + tag;
                }
            }
            tvTagList.setText(tagList);
        }
    }

    public void setTitlePriceUser(Offering offering, TextView tvTitle, TextView tvPrice, TextView tvUser) {
        tvTitle.setText(offering.getTitle());
        tvPrice.setText("$" + Integer.toString(offering.getPrice()));
        try {
            tvUser.setText(offering.getUser().fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setUser(ParseUser user, Context context, TextView tvTitle, ImageView ivPhoto) {
        try {
            tvTitle.setText(user.fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Serializable profileImage;

        if (user.getParseFile("profileImage") != null) {
            profileImage = user.getParseFile("profileImage").getUrl();
        } else {
            profileImage = R.drawable.ic_baseline_person_outline_24;
        }

        Glide.with(context)
                .load(profileImage)
                .circleCrop()
                .into(ivPhoto);
    }

    public void setUserFromFB(String name, String url, Context context, TextView tvTitle, ImageView ivPhoto) {
        tvTitle.setText(name);
        Glide.with(context)
                .load(url)
                .circleCrop()
                .into(ivPhoto);
    }
}
