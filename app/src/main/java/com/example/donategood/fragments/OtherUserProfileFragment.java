package com.example.donategood.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //if user clicked on is signed in user
        if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
            Fragment fragment = new ProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            return;
        }

        parentProfile = new ParentProfile();
        parentProfile.initializeVariables(view, getContext(), parentProfile.KEY_OTHER_USER);

        btnChat = view.findViewById(R.id.btnChat);

        parentProfile.setUser(user);
        parentProfile.queryInfo(getContext());
        parentProfile.queryPosts(ParentProfile.KEY_BOUGHT);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnChat clicked");
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("user", Parcels.wrap(user));
                startActivity(intent);

                /*
                if (user.get("fbMessenger") == null) {
                    Toast.makeText(getContext(), "User does not have FB messenger set up", Toast.LENGTH_SHORT).show();
                } else {
                    Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.me/" + user.get("fbMessenger").toString()));
                    startActivity(implicit);
                }
                 */
            }
        });
    }
}