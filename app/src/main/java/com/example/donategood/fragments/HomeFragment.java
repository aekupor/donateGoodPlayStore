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
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";

    private RecyclerView rvOfferings;
    private OfferingAdapter adapter;
    private List<Offering> allOfferings;
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

        queryPosts(0);
    }

    protected void queryPosts(int page) {
        pb.setVisibility(ProgressBar.VISIBLE);
        query.queryAllPostsByPage(page, new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting offerings", e);
                    return;
                }
                for (Offering offering : offerings) {
                    Log.i(TAG, "Offering: " + offering.getTitle());
                }
                allOfferings.addAll(offerings);
                swipeContainer.setRefreshing(false);
                adapter.notifyDataSetChanged();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }
}