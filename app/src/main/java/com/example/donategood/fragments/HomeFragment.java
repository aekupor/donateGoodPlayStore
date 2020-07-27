package com.example.donategood.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.donategood.EndlessRecyclerViewScrollListener;
import com.example.donategood.R;
import com.example.donategood.adapters.OfferingAdapter;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Offering;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";

    private RecyclerView rvOfferings;
    private OfferingAdapter adapter;
    private List<Offering> allOfferings;
    private List<Offering> listAllOfferings;
    private List<Offering> listFollowingOfferings;

    private Query query;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ProgressBar pb;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvOfferings = view.findViewById(R.id.rvOfferings);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        pb = (ProgressBar) view.findViewById(R.id.pbHomeLoading);

        query = new Query();
        allOfferings = new ArrayList<>();
        listAllOfferings = new ArrayList<>();
        listFollowingOfferings = new ArrayList<>();
        adapter = new OfferingAdapter(getContext(), allOfferings);

        rvOfferings.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvOfferings.setLayoutManager(linearLayoutManager);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               queryPosts(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "onLoadMore: " + page);
                // Triggered only when new data needs to be appended to the list
                queryPosts(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvOfferings.addOnScrollListener(scrollListener);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.homeTabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.i(TAG, "tab selected at position: " + position);
                if (position == 0) {
                    queryPosts(0);
                } else {
                    queryPostsFollowing();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        queryPosts(0);
    }

    private void queryPostsFollowing() {
        if (!listFollowingOfferings.isEmpty()) {
            //if already queried for following posts once
            allOfferings.clear();
            adapter.clear();
            allOfferings.addAll(listFollowingOfferings);
            swipeContainer.setRefreshing(false);
            adapter.notifyDataSetChanged();
            return;
        }

        pb.setVisibility(ProgressBar.VISIBLE);
        ParseUser.getCurrentUser().getRelation("following").getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    return;
                }
                for (Offering offering : listAllOfferings) {
                    for (ParseObject object : objects) {
                        ParseUser followingUser = (ParseUser) object;
                        if (offering.getUser().getObjectId().equals(followingUser.getObjectId())) {
                            listFollowingOfferings.add(offering);
                        }
                    }
                }
                allOfferings.clear();
                adapter.clear();
                allOfferings.addAll(listFollowingOfferings);
                swipeContainer.setRefreshing(false);
                adapter.notifyDataSetChanged();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    protected void queryPosts(int page) {
        if (!listAllOfferings.isEmpty()) {
            //if already queried for all posts once
            allOfferings.clear();
            adapter.clear();
            allOfferings.addAll(listAllOfferings);
            swipeContainer.setRefreshing(false);
            adapter.notifyDataSetChanged();
            return;
        }

        pb.setVisibility(ProgressBar.VISIBLE);
        query.queryAllPostsByPage(page, new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting offerings", e);
                    return;
                }
                listAllOfferings = offerings;
                allOfferings.clear();
                adapter.clear();
                allOfferings.addAll(offerings);
                swipeContainer.setRefreshing(false);
                adapter.notifyDataSetChanged();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }
}