package com.example.donategood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.donategood.fragments.ComposeFragment;
import com.example.donategood.fragments.HomeFragment;
import com.example.donategood.fragments.ProfileFragment;
import com.example.donategood.fragments.SearchFragment;
import com.example.donategood.helperClasses.Camera;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static final Integer CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 10;
    public static final Integer UPLOAD_PHOTO_CODE = 20;

    private BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_profile:
                        Log.i(TAG, "profile button clicked");
                        fragment = new ProfileFragment();
                        break;
                    case R.id.action_compose:
                        Log.i(TAG, "compose button clicked");
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_search:
                        Log.i(TAG, "search button clicked");
                        fragment = new SearchFragment();
                        break;
                    default:
                        //home button clicked
                        Log.i(TAG, "home button clicked");
                        fragment = new HomeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        //set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");

        Camera camera = ProfileFragment.getCamera();
        File photoFile = camera.getPhotoFile();

        Bitmap image = null;
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                image = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            } else { // Result was a failure
                Toast.makeText(getApplicationContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if ((data != null) && requestCode == UPLOAD_PHOTO_CODE) {
            Uri photoUri = data.getData();
            image = camera.loadFromUri(photoUri, getApplicationContext());
            photoFile = camera.createFile(getApplicationContext(), image);
        }

        ImageView ivPhotoToUpload = (ImageView) findViewById(R.id.ivProfileProfileImage);
        ivPhotoToUpload.setImageBitmap(image);
        ParseFile file = new ParseFile(photoFile);
        ParseUser.getCurrentUser().put("profileImage", file);
        ParseUser.getCurrentUser().saveInBackground();
    }
}