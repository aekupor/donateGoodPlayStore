package com.example.donategood.helperClasses;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donategood.R;
import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.models.Offering;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class ParentProfile {

    public static final String TAG = "ParentProfile";
    public static final String KEY_BOUGHT = "bought";
    public static final String KEY_SELLING = "selling";
    public static final String KEY_SOLD = "sold";

    public LoadPost loadPost;
    public Query query;
    public ParseUser user;

    public TextView tvName;
    public ImageView ivProfileImage;
    public TextView tvMoneyRaised;
    public RecyclerView rvOfferings;
    public SmallOfferingAdapter adapter;
    public List<Offering> selectedOfferings;
    public TextView tvBoughtTitle;
    public TextView tvSoldTitle;
    public TextView tvSellingTitle;
    public ProgressBar pb;
    public RatingBar ratingBar;

    public void initializeVariables(View view, Context context) {
        tvName = view.findViewById(R.id.tvOtherProfileProfileName);
        ivProfileImage = view.findViewById(R.id.ivOtherProfileProfileImage);
        tvMoneyRaised = view.findViewById(R.id.tvOtherUserMoneyRaised);
        rvOfferings = view.findViewById(R.id.rvOtherUserSelling);
        tvBoughtTitle = view.findViewById(R.id.tvOtherBoughtTitle);
        tvSellingTitle = view.findViewById(R.id.tvOtherSellingTitle);
        tvSoldTitle = view.findViewById(R.id.tvOtherSoldTitle);
        pb = (ProgressBar) view.findViewById(R.id.pbOtherProfileLoading);
        ratingBar = (RatingBar) view.findViewById(R.id.rbOtherUserProfile);

        selectedOfferings = new ArrayList<>();
        adapter = new SmallOfferingAdapter(context, selectedOfferings);

        rvOfferings.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rvOfferings.setLayoutManager(linearLayoutManager);

        loadPost = new LoadPost();
        query = new Query();

        tvBoughtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_BOUGHT);
            }
        });

        tvSellingTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_SELLING);
            }
        });

        tvSoldTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_SOLD);
            }
        });
    }

    public void queryPosts(String queryType) {
        pb.setVisibility(ProgressBar.VISIBLE);
        query.setBold(queryType, tvSoldTitle, tvSellingTitle, tvBoughtTitle);
        query.queryPosts(user, queryType, adapter, selectedOfferings, pb);
    }

    public void queryInfo(Context context) {
        loadPost.setUser(user, context, tvName, ivProfileImage);

        queryPosts(KEY_BOUGHT);
        query.queryMoneyRaised(user, tvMoneyRaised);
        query.queryUserRating(user, ratingBar);
    }

    public void setUser(ParseUser parseUser) {
        user = parseUser;
    }
}