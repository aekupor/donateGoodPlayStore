package com.example.donategood.fragments;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.donategood.R;
import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

    private EditText etSearchText;
    private Button btnSearch;

    private RecyclerView rvOfferings;
    private SmallOfferingAdapter adapter;
    private List<Offering> allOfferings;
    private Query query;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearchText = view.findViewById(R.id.etSearchBar);
        btnSearch = view.findViewById(R.id.btnSearch);
        rvOfferings = view.findViewById(R.id.rvSearchOfferings);

        query = new Query();
        allOfferings = new ArrayList<>();
        adapter = new SmallOfferingAdapter(getContext(), allOfferings);

        rvOfferings.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvOfferings.setLayoutManager(linearLayoutManager);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = etSearchText.getText().toString();
                if (searchText.isEmpty()) {
                    Toast.makeText(getContext(), "Search cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    querySearch(searchText);
                }
            }
        });
    }

    private void querySearch(String searchText) {
        Log.i(TAG, "Search for: " + searchText);
        query.search(searchText, new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting offerings", e);
                    return;
                }
                if (offerings.size() == 0) {
                    Log.i(TAG, "No posts found");
                    Toast.makeText(getContext(), "No posts were found", Toast.LENGTH_SHORT).show();
                    return;
                }
                allOfferings.addAll(offerings);
                adapter.notifyDataSetChanged();
            }
        });
    }
}