package com.example.donategood.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.donategood.ChatActivity;
import com.example.donategood.R;
import com.example.donategood.helperClasses.ParentProfile;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class OtherUserProfileFragment extends Fragment {

    public static final String TAG = "OtherUserProfileFragment";

    private ParentProfile parentProfile;
    private Button btnChat;
    private Button btnAnalytics;
    private ParseUser user;

    public OtherUserProfileFragment() {
        // Required empty public constructor
    }

    public static OtherUserProfileFragment newInstance(Bundle bundle) {
        OtherUserProfileFragment fragment = new OtherUserProfileFragment();
        if (bundle != null) {
            Bundle args = new Bundle();
            ParseUser user = bundle.getParcelable("user");
            Log.i(TAG, "got user: " + user.getUsername());
            args.putParcelable("user", user);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getArguments().getParcelable("user");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_other_user_profile_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_user_profile, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_analytics_other_profile:
                Log.i(TAG, "action_analytics_other_profile clicked");

                return true;
            case R.id.action_chat_other_user:
                Log.i(TAG, "action_chat_other_user clicked");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        //if user clicked on is signed in user, go to ProfileFragment
        if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
            Fragment fragment = new ProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            return;
        }

        //set variables and info via parentProfile methods
        parentProfile = new ParentProfile();
        parentProfile.initializeVariables(view, getContext(), parentProfile.KEY_OTHER_USER);
        parentProfile.setUser(user);
        parentProfile.queryInfo(getContext(), view);
        parentProfile.queryPosts(ParentProfile.KEY_BOUGHT);

        //only other user's have a chat button and analytics button
        btnChat = view.findViewById(R.id.btnChat);
        btnAnalytics = view.findViewById(R.id.btnAnalytics);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnChat clicked");
                //open up chat
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("user", Parcels.wrap(user));
                startActivity(intent);
            }
        });

        btnAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnAnalytics clicked");
                parentProfile.openAnalyticsDialog(OtherUserProfileFragment.this, getFragmentManager());
            }
        });
    }
}