package com.example.donategood.fragments;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.donategood.adapters.OfferingAdapter;
import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.R;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class OtherUserProfileFragment extends Fragment {

    public static final String TAG = "OtherUserProfileFragment";
    public static final String KEY_BOUGHT = "bought";
    public static final String KEY_SELLING = "selling";
    public static final String KEY_SOLD = "sold";

    private LoadPost loadPost;
    private Query query;

    private TextView tvName;
    private ImageView ivProfileImage;
    private TextView tvMoneyRaised;
    private RecyclerView rvOfferings;
    private SmallOfferingAdapter adapter;
    private List<Offering> selectedOfferings;
    private TextView tvBoughtTitle;
    private TextView tvSoldTitle;
    private TextView tvSellingTitle;
    private ProgressBar pb;

    private String userName;
    private ParseUser user;

    public OtherUserProfileFragment() {
        // Required empty public constructor
    }

    public static OtherUserProfileFragment newInstance(String userName) {
        OtherUserProfileFragment fragment = new OtherUserProfileFragment();
        Bundle args = new Bundle();
        args.putString("userName", userName);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = getArguments().getString("userName", "");
        Log.i(TAG, "userName: " + userName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //if user clicked on is signed in user
        if (userName.equals(ParseUser.getCurrentUser().getUsername())) {
            final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
            Fragment fragment = new ProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            return;
        }

        tvName = view.findViewById(R.id.tvOtherProfileProfileName);
        ivProfileImage = view.findViewById(R.id.ivOtherProfileProfileImage);
        tvMoneyRaised = view.findViewById(R.id.tvOtherUserMoneyRaised);
        rvOfferings = view.findViewById(R.id.rvOtherUserSelling);
        tvBoughtTitle = view.findViewById(R.id.tvOtherBoughtTitle);
        tvSellingTitle = view.findViewById(R.id.tvOtherSellingTitle);
        tvSoldTitle = view.findViewById(R.id.tvOtherSoldTitle);
        pb = (ProgressBar) view.findViewById(R.id.pbOtherProfileLoading);

        selectedOfferings = new ArrayList<>();
        adapter = new SmallOfferingAdapter(getContext(), selectedOfferings);

        rvOfferings.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvOfferings.setLayoutManager(linearLayoutManager);

        loadPost = new LoadPost();
        query = new Query();

        tvBoughtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSoldTitle.setTypeface(null, Typeface.NORMAL);
                tvSellingTitle.setTypeface(null, Typeface.NORMAL);
                tvBoughtTitle.setTypeface(null, Typeface.BOLD);
                queryPosts(KEY_BOUGHT);
            }
        });

        tvSellingTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSoldTitle.setTypeface(null, Typeface.NORMAL);
                tvSellingTitle.setTypeface(null, Typeface.BOLD);
                tvBoughtTitle.setTypeface(null, Typeface.NORMAL);
                queryPosts(KEY_SELLING);
            }
        });

        tvSoldTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSoldTitle.setTypeface(null, Typeface.BOLD);
                tvSellingTitle.setTypeface(null, Typeface.NORMAL);
                tvBoughtTitle.setTypeface(null, Typeface.NORMAL);
                queryPosts(KEY_SOLD);
            }
        });

        query.queryUserByName(userName, new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting user profile", e);
                    return;
                }
                user = objects.get(0);
                loadPost.setUser(user, getContext(), tvName, ivProfileImage);

                queryPosts(KEY_BOUGHT);
                tvSoldTitle.setTypeface(null, Typeface.NORMAL);
                tvSellingTitle.setTypeface(null, Typeface.NORMAL);
                tvBoughtTitle.setTypeface(null, Typeface.BOLD);

                query.queryMoneyRaised(user, tvMoneyRaised);
            }
        });
    }

    protected void queryPosts(String queryType) {
        pb.setVisibility(ProgressBar.VISIBLE);
        query.queryPosts(user, queryType, new FindCallback<Offering>() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting offerings", e);
                    return;
                }
                Log.i(TAG, "Got this number of offerings: " + offerings.size());
                adapter.clear();
                selectedOfferings.clear();
                selectedOfferings.addAll(offerings);
                adapter.notifyDataSetChanged();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }
}