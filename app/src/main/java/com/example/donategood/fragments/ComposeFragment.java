package com.example.donategood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.donategood.Query;
import com.example.donategood.R;

public class ComposeFragment extends Fragment {

    public static final String TAG = "ComposeFragment";

    private Query query;
    private EditText etTitle;
    private EditText etPrice;
    private Button btnSubmit;

    public ComposeFragment() {
        //required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTitle = view.findViewById(R.id.etTitle);
        etPrice = view.findViewById(R.id.etPrice);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        query = new Query();
    }
}