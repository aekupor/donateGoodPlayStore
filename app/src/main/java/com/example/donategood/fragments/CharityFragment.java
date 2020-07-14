package com.example.donategood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.donategood.Query;
import com.example.donategood.R;
import com.example.donategood.models.Charity;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public class CharityFragment extends Fragment {

    public static final String TAG = "CharityFragment";

    private String charityName;
    private Query query;
    private Charity charity;

    public CharityFragment() {
        // Required empty public constructor
    }

    public static CharityFragment newInstance (String charityName) {
        CharityFragment fragment = new CharityFragment();
        Bundle args = new Bundle();
        args.putString("charityName", charityName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        charityName = getArguments().getString("charityName", "");
        Log.i(TAG, "charity name: " + charityName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_charity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        query = new Query();
        query.queryCharityByName(charityName, new FindCallback<Charity>() {
            @Override
            public void done(List<Charity> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error finding charity", e);
                    return;
                }
                charity = objects.get(0);
                Log.i(TAG, "Successfully got charity with title: " + charity.getTitle());
            }
        });
    }
}