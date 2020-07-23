package com.example.donategood.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.donategood.LoginActivity;
import com.example.donategood.R;
import com.example.donategood.helperClasses.Camera;
import com.example.donategood.helperClasses.FBQuery;
import com.example.donategood.helperClasses.ParentProfile;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment implements ChangeNameFragment.ChangeNameDialogListener {

    public static final String TAG = "ProfileFragment";

    private static Camera camera;
    private Boolean fbEdit;
    private ParentProfile parentProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                Log.i(TAG, "action_logout clicked");
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                return true;
            case R.id.action_take_photo:
                Log.i(TAG, "action_take_photo clicked");
                camera.launchCamera(getContext(), true);
                return true;
            case R.id.action_upload_photo:
                Log.i(TAG, "action_upload_photo clicked");
                camera.pickPhoto(getContext(), true, false);
                return true;
            case R.id.action_messenger_name:
                Log.i(TAG, "action_messenger_name clicked");
                fbEdit = true;
                showEditDialog();
                return true;
            case R.id.action_venmo_name:
                Log.i(TAG, "action_venmo_name clicked");
                fbEdit = false;
                showEditDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        parentProfile = new ParentProfile();
        parentProfile.initializeVariables(view, getContext(), true);
        parentProfile.setUser(ParseUser.getCurrentUser());
        parentProfile.queryInfo(getContext());
        parentProfile.queryPosts(parentProfile.KEY_BOUGHT);

        camera = new Camera();

        checkFBLogin();
    }

    public static Camera getCamera() {
        return camera;
    }

    public void checkFBLogin() {
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

                                    parentProfile.loadPost.setUserFromFB(name, url, getContext(), parentProfile.tvName, parentProfile.ivProfileImage);
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
            parentProfile.loadPost.setUser(ParseUser.getCurrentUser(), getContext(), parentProfile.tvName, parentProfile.ivProfileImage);
        }
    }

    private void showEditDialog() {
        FragmentManager fm = getFragmentManager();
        ChangeNameFragment changeNameFragment = (ChangeNameFragment) ChangeNameFragment.newInstance(fbEdit);
        // SETS the target fragment for use later when sending results
        changeNameFragment.setTargetFragment(ProfileFragment.this, 300);
        changeNameFragment.show(fm, "fragment_change_name");
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        Log.i(TAG, "change name to: " + inputText);

        if (fbEdit) {
            ParseUser.getCurrentUser().put("fbMessenger", inputText);
        } else {
            ParseUser.getCurrentUser().put("venmoName", inputText);
        }
        ParseUser.getCurrentUser().saveInBackground();
        Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }
}