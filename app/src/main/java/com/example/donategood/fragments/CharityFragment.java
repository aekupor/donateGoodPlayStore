package com.example.donategood.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.donategood.R;
import com.example.donategood.helperClasses.ParentProfile;
import com.example.donategood.models.Charity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class CharityFragment extends Fragment {

    public static final String TAG = "CharityFragment";

    private Charity charity;
    private String charityName;
    private Button btnWebsite;
    private ParentProfile parentProfile;
    private Boolean following;
    private ImageView ivFollow;

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

        parentProfile = new ParentProfile();
        parentProfile.initializeVariables(view, getContext(), parentProfile.KEY_CHARITY);

        ivFollow = view.findViewById(R.id.ivFollowCharity);
        btnWebsite = view.findViewById(R.id.btnCharityWebsite);
        following = false;

        btnWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to webview fragment
                final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                Fragment fragment = WebViewFragment.newInstance(charity.getWebsite());
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
        });

        parentProfile.pb.setVisibility(ProgressBar.VISIBLE);
        parentProfile.query.findCharity(charityName, new FindCallback<Charity>() {
            @Override
            public void done(List<Charity> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error finding charity", e);
                    return;
                }
                charity = objects.get(0);
                Log.i(TAG, "Successfully got charity with title: " + charity.getTitle());

                parentProfile.setCharity(charity);
                parentProfile.queryCharityInfo(getContext());
                checkIfFollowing();
            }
        });

        ivFollow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                Log.i(TAG, "follow clicked");
                if (following) {
                    ParseUser.getCurrentUser().getRelation("followingCharity").remove(charity);
                    ParseUser.getCurrentUser().saveInBackground();
                    ivFollow.setImageResource(R.drawable.ic_baseline_person_add_24);
                    Toast.makeText(getContext(), "Unfollowed", Toast.LENGTH_SHORT).show();
                } else {
                    ParseUser.getCurrentUser().getRelation("followingCharity").add(charity);
                    ParseUser.getCurrentUser().saveInBackground();
                    ivFollow.setImageResource(R.drawable.ic_baseline_person_add_disabled_24);
                    Toast.makeText(getContext(), "Following", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //check is current user is already following this user
    public void checkIfFollowing() {
        parentProfile.pb.setVisibility(ProgressBar.VISIBLE);
        ParseUser.getCurrentUser().getRelation("followingCharity").getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    return;
                }
                for (ParseObject followingObject : objects) {
                    Charity followingCharity = (Charity) followingObject;
                    if (followingCharity.getObjectId().equals(charity.getObjectId())) {
                        following = true;
                        ivFollow.setImageResource(R.drawable.ic_baseline_person_add_disabled_24);
                        parentProfile.pb.setVisibility(ProgressBar.INVISIBLE);
                        return;
                    }
                }
                parentProfile.pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }
}