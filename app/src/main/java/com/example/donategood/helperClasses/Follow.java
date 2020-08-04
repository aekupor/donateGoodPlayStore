package com.example.donategood.helperClasses;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.donategood.R;
import com.example.donategood.models.Charity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class Follow {

    public static final String TAG = "Follow";

    //initialize variables relating to follow
    //ProfileFragment does not have follow since user is current signed in use
    public void initializeFollow(View view, final Context context, final ParentProfile parentProfile) {
        parentProfile.following = false;
        parentProfile.ivFollow = view.findViewById(R.id.ivFollow);
        checkIfFollowing(parentProfile);
        parentProfile.ivFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "follow clicked");
                if (parentProfile.following) {
                    //if user is already following, onClick unfollows
                    if (parentProfile.profileType == parentProfile.KEY_CHARITY) {
                        ParseUser.getCurrentUser().getRelation("followingCharity").remove(parentProfile.charity);
                    } else {
                        ParseUser.getCurrentUser().getRelation("following").remove(parentProfile.user);
                    }
                    ParseUser.getCurrentUser().saveInBackground();
                    parentProfile.ivFollow.setImageResource(R.drawable.ic_baseline_person_add_24);
                    Toast.makeText(context, "Unfollowed!", Toast.LENGTH_SHORT).show();
                    parentProfile.following = false;
                } else {
                    //if user is not already following, onClick follows
                    if (parentProfile.profileType == parentProfile.KEY_CHARITY) {
                        ParseUser.getCurrentUser().getRelation("followingCharity").add(parentProfile.charity);
                    } else {
                        ParseUser.getCurrentUser().getRelation("following").add(parentProfile.user);
                    }
                    ParseUser.getCurrentUser().saveInBackground();
                    parentProfile.ivFollow.setImageResource(R.drawable.ic_baseline_person_add_disabled_24);
                    Toast.makeText(context, "Following", Toast.LENGTH_SHORT).show();
                    parentProfile.following = true;
                }
            }
        });
    }

    //check is current user is already following this user/charity
    public void checkIfFollowing(final ParentProfile parentProfile) {
        parentProfile.pb.setVisibility(ProgressBar.VISIBLE);
        if (parentProfile.profileType == parentProfile.KEY_CHARITY) {
            //check if following charity
            ParseUser.getCurrentUser().getRelation("followingCharity").getQuery().findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e != null) {
                        return;
                    }
                    for (ParseObject followingObject : objects) {
                        Charity followingCharity = (Charity) followingObject;
                        if (followingCharity != null && parentProfile.charity != null) {
                            if (followingCharity.getObjectId().equals(parentProfile.charity.getObjectId())) {
                                setIsFollowing(parentProfile);
                                return;
                            }
                        }
                    }
                }
            });
        } else {
            //check if following user
            ParseUser.getCurrentUser().getRelation("following").getQuery().findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e != null) {
                        return;
                    }
                    for (ParseObject followingObject : objects) {
                        ParseUser followingUser = (ParseUser) followingObject;
                        if (followingUser.getObjectId().equals(parentProfile.user.getObjectId())) {
                            setIsFollowing(parentProfile);
                            return;
                        }
                    }
                }
            });
        }
    }

    //if user is following that charity/user, set appropiate variables
    public void setIsFollowing(final ParentProfile parentProfile) {
        parentProfile.following = true;
        parentProfile.ivFollow.setImageResource(R.drawable.ic_baseline_person_add_disabled_24);
    }
}
