package com.example.donategood.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.donategood.adapters.OfferingAdapter;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.R;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class OtherUserProfileFragment extends Fragment {

    public static final String TAG = "OtherUserProfileFragment";

    private LoadPost loadPost;
    private Query query;

    private TextView tvName;
    private ImageView ivProfileImage;
    private RecyclerView rvSellingItems;
    private OfferingAdapter adapter;
    private List<Offering> sellingOfferings;

    private String userName;
    private ParseUser user;

    public OtherUserProfileFragment() {
        // Required empty public constructor
    }

    public static OtherUserProfileFragment newInstance(String userName) {
        OtherUserProfileFragment fragment = new OtherUserProfileFragment();
        Bundle args = new Bundle();
        args.putString("userName", userName);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = getArguments().getString("userName", "");
        Log.i(TAG, "userName: " + userName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tvOtherProfileProfileName);
        ivProfileImage = view.findViewById(R.id.ivOtherProfileProfileImage);
        rvSellingItems = view.findViewById(R.id.rvOtherUserSelling);

        sellingOfferings = new ArrayList<>();
        adapter = new OfferingAdapter(getContext(), sellingOfferings);

        rvSellingItems.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvSellingItems.setLayoutManager(linearLayoutManager);

        loadPost = new LoadPost();
        query = new Query();

        query.queryUserByName(userName, new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting user profile", e);
                    return;
                }
                user = objects.get(0);
                loadPost.setUser(user, getContext(), tvName, ivProfileImage);

                querySellingPosts();
            }
        });
    }

    protected void querySellingPosts() {
        query.querySellingPostsByUser(user, new FindCallback<Offering>() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting offerings", e);
                    return;
                }
                for (Offering offering : offerings) {
                    Log.i(TAG, "Offering: " + offering.getTitle());
                }
                sellingOfferings.addAll(offerings);
                adapter.notifyDataSetChanged();
            }
        });
    }
}