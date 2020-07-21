package com.example.donategood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.donategood.R;
import com.parse.ParseFile;

public class NewCharityFragment extends Fragment {

    public static final String TAG = "NewCharityFragment";

    private EditText etCharityName;
    private EditText etCharityWebsite;
    private Button btnCharityPhoto;
    private Button btnSubmit;

    public NewCharityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_charity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etCharityName = view.findViewById(R.id.etCharityName);
        etCharityWebsite = view.findViewById(R.id.etCharityWebsite);
        btnCharityPhoto = view.findViewById(R.id.btnCharityUploadPhoto);
        btnSubmit = view.findViewById(R.id.btnNewCharitySubmit);

        btnCharityPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnCharityPhoto clicked");
                
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnSubmit clicked");
            }
        });
    }
}