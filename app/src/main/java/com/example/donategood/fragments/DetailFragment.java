package com.example.donategood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.donategood.Query;
import com.example.donategood.R;

public class DetailFragment extends Fragment {

    public static final String TAG = "DetailFragment";

    private String offeringId;
    private Query query;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(String offeringId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString("offeringId", offeringId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        offeringId = getArguments().getString("offeringId", "");
        Log.i(TAG, "post id: " + offeringId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        query = new Query();
    }
}