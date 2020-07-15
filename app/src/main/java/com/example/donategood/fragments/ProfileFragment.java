package com.example.donategood.fragments;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.donategood.adapters.OfferingAdapter;
import com.example.donategood.helperClasses.Camera;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.LoginActivity;
import com.example.donategood.R;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public static final String KEY_BOUGHT = "bought";
    public static final String KEY_SELLING = "selling";
    public static final String KEY_SOLD = "sold";

    private LoadPost loadPost;
    private static Camera camera;

    private Button btnLogout;
    private Button btnTakePhoto;
    private Button btnUploadPhoto;
    private TextView tvName;
    private ImageView ivProfileImage;
    private TextView tvMoneyRaised;
    private TextView tvYouBoughtTitle;
    private TextView tvYouSellingTitle;
    private TextView tvYouSoldTitle;
    private ProgressBar pb;

    private RecyclerView rvBoughtItems;
    private OfferingAdapter adapter;
    private List<Offering> boughtOfferings;
    private Query query;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogout = view.findViewById(R.id.btnLogout);
        tvName = view.findViewById(R.id.tvProfileProfileName);
        ivProfileImage = view.findViewById(R.id.ivProfileProfileImage);
        btnTakePhoto = view.findViewById(R.id.btnProfileTakePhoto);
        btnUploadPhoto = view.findViewById(R.id.btnProfileUploadPhoto);
        tvMoneyRaised = view.findViewById(R.id.tvProfileMoneyRaised);
        rvBoughtItems = view.findViewById(R.id.rvBoughtItems);
        tvYouBoughtTitle = view.findViewById(R.id.tvYouBoughtTitle);
        tvYouSellingTitle = view.findViewById(R.id.tvYouSellingTitle);
        tvYouSoldTitle = view.findViewById(R.id.tvYouSoldTitle);
        pb = (ProgressBar) view.findViewById(R.id.pbProfileLoading);

        query = new Query();
        boughtOfferings = new ArrayList<>();
        adapter = new OfferingAdapter(getContext(), boughtOfferings);

        rvBoughtItems.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvBoughtItems.setLayoutManager(linearLayoutManager);

        loadPost = new LoadPost();
        camera = new Camera();

        loadPost.setUser(ParseUser.getCurrentUser(), getContext(), tvName, ivProfileImage);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.launchCamera(getContext(), true);
            }
        });

        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.pickPhoto(getContext(), true);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "logout user");
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        tvYouBoughtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_BOUGHT);
            }
        });

        tvYouSellingTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_SELLING);
            }
        });

        tvYouSoldTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryPosts(KEY_SOLD);
            }
        });

        queryPosts("bought");
        query.queryMoneyRaised(ParseUser.getCurrentUser(), tvMoneyRaised);
    }

    protected void queryPosts(String queryType) {
        pb.setVisibility(ProgressBar.VISIBLE);
        FindCallback<Offering> callback = new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting offerings", e);
                    return;
                }
                Log.i(TAG, "Got this number of offerings: " + offerings.size());
                adapter.clear();
                boughtOfferings.clear();
                boughtOfferings.addAll(offerings);
                adapter.notifyDataSetChanged();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        };

        if (queryType.equals(KEY_BOUGHT)) {
            query.queryBoughtPostsByUser(ParseUser.getCurrentUser(), callback);
        } else if (queryType.equals(KEY_SELLING)) {
            query.querySellingPostsByUser(ParseUser.getCurrentUser(), false, callback);
        } else if (queryType.equals(KEY_SOLD)) {
            query.querySellingPostsByUser(ParseUser.getCurrentUser(), true, callback);
        }
    }

    public static Camera getCamera() {
        return camera;
    }
}