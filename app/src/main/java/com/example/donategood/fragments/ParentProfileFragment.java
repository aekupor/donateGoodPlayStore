package com.example.donategood.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ParentProfileFragment extends Fragment {

    public static final String TAG = "ParentProfileFragment";

    private String userName;

    public ParentProfileFragment() {
        // Required empty public constructor
    }

    public static ParentProfileFragment newInstance(String userName) {
        ParentProfileFragment fragment = new ParentProfileFragment();
        Bundle args = new Bundle();
        args.putString("userName", userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = getArguments().getString("userName", "");
        Log.i(TAG, "userName: " + userName);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    protected void queryPosts(String queryType) {

    }
}