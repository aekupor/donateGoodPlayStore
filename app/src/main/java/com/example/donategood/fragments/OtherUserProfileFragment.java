package com.example.donategood.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class OtherUserProfileFragment extends Fragment {

    public static final String TAG = "OtherUserProfileFragment";

    private ParentProfile parentProfile;
    private Button btnChat;
    private ImageView ivFollow;
    private Boolean following;

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

        parentProfile = new ParentProfile();
        parentProfile.initializeVariables(view, getContext(), parentProfile.KEY_OTHER_USER);

        btnChat = view.findViewById(R.id.btnChat);
        ivFollow = view.findViewById(R.id.ivFollow);
        following = false;

        parentProfile.query.findUser(userName, new FindCallback<ParseUser>() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting user profile", e);
                    return;
                }
                user = objects.get(0);
                parentProfile.setUser(user);

                parentProfile.queryInfo(getContext());
                parentProfile.queryPosts(ParentProfile.KEY_BOUGHT);

                checkIfFollowing();
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnChat clicked");
                if (user.get("fbMessenger") == null) {
                    Toast.makeText(getContext(), "User does not have FB messenger set up", Toast.LENGTH_SHORT).show();
                } else {
                    Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.me/" + user.get("fbMessenger").toString()));
                    startActivity(implicit);
                }
            }
        });

        ivFollow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                Log.i(TAG, "follow clicked");
            }
        });
    }

    //check is current user is already following this user
    public void checkIfFollowing() {
        parentProfile.pb.setVisibility(ProgressBar.VISIBLE);
        ParseUser.getCurrentUser().getRelation("following").getQuery().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    return;
                }
                for (ParseObject followingObject : objects) {
                    ParseUser followingUser = (ParseUser) followingObject;
                    if (followingUser.getObjectId().equals(user.getObjectId())) {
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