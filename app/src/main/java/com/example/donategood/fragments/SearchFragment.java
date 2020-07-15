package com.example.donategood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.donategood.R;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

    private EditText etSearchText;
    private Button btnSearch;
    private RecyclerView rvOfferings;

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
    }
}