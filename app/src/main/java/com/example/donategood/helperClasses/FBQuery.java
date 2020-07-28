package com.example.donategood.helperClasses;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class FBQuery {

    public static final String TAG = "FBQuery";

    //get name of signed in user
    public void getName(AccessToken accessToken, GraphRequest.GraphJSONObjectCallback graphJSONObjectCallback) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                graphJSONObjectCallback);

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    //get profile image of signed in user
    public void getProfileImage(AccessToken accessToken, Long userId, GraphRequest.Callback callback) {
        GraphRequest photoRequest = GraphRequest.newGraphPathRequest(
                accessToken,
                "/" + userId +"/picture?redirect=false",
                callback);

        photoRequest.executeAsync();
    }

    //check if user is logged in; if they are, load their profile image and name
    public void checkFBLogin(final ParentProfile parentProfile, final Context context) {
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            //user is logged in with facebook
            Log.i(TAG, "user is logged in with FB");
            final Long[] userId = new Long[1];
            final FBQuery fbQuery = new FBQuery();

            //get user name from FB
            fbQuery.getName(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        final String name = object.getString("name");
                        Log.i(TAG, "got graph response: " + name);
                        userId[0] = object.getLong("id");

                        //get user profile picture from FB
                        fbQuery.getProfileImage(accessToken, userId[0], new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                try {
                                    JSONObject data = response.getJSONObject().getJSONObject("data");
                                    String url = data.getString("url");
                                    Log.i(TAG, "got image url: " + url);

                                    parentProfile.loadPost.setUserFromFB(name, url, context, parentProfile.tvName, parentProfile.ivProfileImage);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            //user is not logged in with FB
            parentProfile.loadPost.setUser(ParseUser.getCurrentUser(), context, parentProfile.tvName, parentProfile.ivProfileImage);
        }
    }
}
