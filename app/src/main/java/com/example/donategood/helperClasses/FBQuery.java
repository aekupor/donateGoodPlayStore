package com.example.donategood.helperClasses;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;

public class FBQuery {

    public void getName(AccessToken accessToken, GraphRequest.GraphJSONObjectCallback graphJSONObjectCallback) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                graphJSONObjectCallback);

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void getProfileImage(AccessToken accessToken, Long userId, GraphRequest.Callback callback) {
        GraphRequest photoRequest = GraphRequest.newGraphPathRequest(
                accessToken,
                "/" + userId +"/picture?redirect=false",
                callback);

        photoRequest.executeAsync();
    }
}
