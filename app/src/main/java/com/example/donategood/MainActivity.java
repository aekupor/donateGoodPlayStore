package com.example.donategood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.ClipData;
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
import com.example.donategood.fragments.DetailFragment;
import com.example.donategood.fragments.HomeFragment;
import com.example.donategood.fragments.ProfileFragment;
import com.example.donategood.fragments.SearchFragment;
import com.example.donategood.helperClasses.Camera;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static final Integer CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 10;
    public static final Integer UPLOAD_PHOTO_CODE = 20;
    public static final Integer CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_PROFILE = 30;
    public static final Integer UPLOAD_PHOTO_CODE_PROFILE = 40;
    public static final Integer PICK_MULTIPLE_PHOTO_CODE = 50;

    private BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    private static ArrayList<ParseFile> parseFileList;

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

    public static ArrayList<ParseFile> getParseFileList() {
        return parseFileList;
    }

    //called when camera is closed
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");
        if (isPhoto(requestCode)) {
            if (requestCode == PICK_MULTIPLE_PHOTO_CODE) {

                Camera camera = ComposeFragment.getCamera();
                Context mainContext = camera.getContext();

                ArrayList<File> photoFileArray = new ArrayList<>();

                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    parseFileList = new ArrayList<>();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        Bitmap bitmap = camera.loadFromUri(uri, mainContext);
                        File photoFile = camera.createFile(mainContext, bitmap);
                        photoFileArray.add(photoFile);
                        ParseFile file = new ParseFile(photoFile);
                        parseFileList.add(file);
                        Log.i(TAG, "got photo number " + i);
                    }
                    camera.setPhotoFileArray(photoFileArray);
                }
            } else {
                ImageView ivPhotoToUpload;
                Camera camera;
                if (isProfile(requestCode)) {
                    ivPhotoToUpload = (ImageView) findViewById(R.id.ivProfileProfileImage);
                    camera = ProfileFragment.getCamera();
                } else {
                    ivPhotoToUpload = (ImageView) findViewById(R.id.ivComposePhoto);
                    camera = ComposeFragment.getCamera();
                }

                File photoFile = camera.getPhotoFile();
                Context mainContext = camera.getContext();
                Bitmap image = null;

                if (isTakePhoto(resultCode, requestCode)) {
                    image = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                } else if (isUploadPhoto(data, requestCode)) {
                    Uri photoUri = data.getData();
                    image = camera.loadFromUri(photoUri, mainContext);
                    photoFile = camera.createFile(mainContext, image);
                    camera.setPhotoFile(photoFile);
                } else {
                    Toast.makeText(getApplicationContext(), "Issue with picture!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ivPhotoToUpload.setImageBitmap(image);

                if (isProfile(requestCode)) {
                    ParseFile file = new ParseFile(photoFile);
                    ParseUser.getCurrentUser().put("profileImage", file);
                    ParseUser.getCurrentUser().saveInBackground();
                }
            }
        }
    }

    private Boolean isPhoto(int requestCode) {
        if (requestCode == UPLOAD_PHOTO_CODE_PROFILE || requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_PROFILE || requestCode == UPLOAD_PHOTO_CODE || requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == PICK_MULTIPLE_PHOTO_CODE) {
            return true;
        }
        return false;
    }

    private Boolean isTakePhoto(int resultCode, int requestCode) {
        if (resultCode == RESULT_OK && (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_PROFILE)) {
            return true;
        }
        return false;
    }

    private Boolean isUploadPhoto(Intent data, int requestCode) {
        if ((data != null) && (requestCode == UPLOAD_PHOTO_CODE || requestCode == UPLOAD_PHOTO_CODE_PROFILE)) {
            return true;
        }
        return false;
    }

    private Boolean isProfile(int requestCode) {
        if (requestCode == UPLOAD_PHOTO_CODE_PROFILE || requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_PROFILE) {
            return true;
        }
        return false;
    }
}