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
import com.parse.ParseUser;

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

        FBQuery fbQuery = new FBQuery();
        fbQuery.checkFBLogin(parentProfile, getContext());
    }

    public static Camera getCamera() {
        return camera;
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