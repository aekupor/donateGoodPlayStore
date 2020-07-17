package com.example.donategood.fragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.donategood.adapters.OfferingAdapter;
import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.R;
import com.example.donategood.models.Charity;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class CharityFragment extends Fragment {

    public static final String TAG = "CharityFragment";

    private Query query;
    private LoadPost loadPost;
    private RecyclerView rvOfferings;
    private SmallOfferingAdapter adapter;
    private List<Offering> allOfferings;

    private Charity charity;
    private String charityName;
    private TextView tvTitle;
    private ImageView ivProfileImage;
    private TextView tvMoneyRaised;
    private ProgressBar pb;
    private TextView tvCharitySellingTitle;
    private TextView tvCharitySoldTitle;
    private Button btnWebsite;

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

        tvTitle = view.findViewById(R.id.tvCharityCharityTitle);
        ivProfileImage = view.findViewById(R.id.ivCharityChairtyImage);
        tvMoneyRaised = view.findViewById(R.id.tvCharityMoneyRaised);
        rvOfferings = view.findViewById(R.id.rvCharitySellingOfferings);
        pb = (ProgressBar) view.findViewById(R.id.pbCharityLoading);
        tvCharitySellingTitle = view.findViewById(R.id.tvCharitySellingTitle);
        tvCharitySoldTitle = view.findViewById(R.id.tvCharitySoldTitle);
        btnWebsite = view.findViewById(R.id.btnCharityWebsite);

        query = new Query();
        allOfferings = new ArrayList<>();
        adapter = new SmallOfferingAdapter(getContext(), allOfferings);

        rvOfferings.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvOfferings.setLayoutManager(linearLayoutManager);

        tvCharitySoldTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvCharitySoldTitle.setTypeface(null, Typeface.BOLD);
                tvCharitySellingTitle.setTypeface(null, Typeface.NORMAL);
                queryPosts(true);
            }
        });

        tvCharitySellingTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvCharitySellingTitle.setTypeface(null, Typeface.BOLD);
                tvCharitySoldTitle.setTypeface(null, Typeface.NORMAL);
                queryPosts(false);
            }
        });

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to webview fragment
                final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                Fragment fragment = WebViewFragment.newInstance(charity.getWebsite());
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        loadPost = new LoadPost();
        query = new Query();
        query.queryCharityByName(charityName, new FindCallback<Charity>() {
            @Override
            public void done(List<Charity> objects, ParseException e) {
                pb.setVisibility(ProgressBar.VISIBLE);
                if (e != null) {
                    Log.e(TAG, "Error finding charity", e);
                    return;
                }
                charity = objects.get(0);
                Log.i(TAG, "Successfully got charity with title: " + charity.getTitle());

                loadPost.setCharityWithCharity(charity, getContext(), tvTitle, ivProfileImage);
                query.queryCharityMoneyRaised(charity, tvMoneyRaised);
                pb.setVisibility(ProgressBar.INVISIBLE);
                queryPosts(false);
            }
        });
    }

    private void queryPosts(Boolean bought) {
        pb.setVisibility(ProgressBar.VISIBLE);
        query.queryPostsByCharity(charity, bought, new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting offerings", e);
                    return;
                }
                Log.i(TAG, "Successfully received this number of offerings: " + offerings.size());
                allOfferings.clear();
                allOfferings.addAll(offerings);
                adapter.notifyDataSetChanged();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }
}