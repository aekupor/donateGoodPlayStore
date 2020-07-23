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
import com.parse.ParseUser;

import java.util.List;


public class OtherUserProfileFragment extends Fragment {

    public static final String TAG = "OtherUserProfileFragment";
    public static final String KEY_BOUGHT = "bought";
    public static final String KEY_SELLING = "selling";
    public static final String KEY_SOLD = "sold";

    private ParentProfile parentProfile;
    private Button btnChat;

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
        parentProfile.initializeVariables(view, getContext());

        btnChat = view.findViewById(R.id.btnChat);

        parentProfile.query.queryUserByName(userName, new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting user profile", e);
                    return;
                }
                user = objects.get(0);
                parentProfile.setUser(user);

                parentProfile.queryInfo(getContext());
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
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
    }


}