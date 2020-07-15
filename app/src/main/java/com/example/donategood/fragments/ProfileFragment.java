package com.example.donategood.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donategood.helperClasses.Camera;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.LoginActivity;
import com.example.donategood.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public static final Integer CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 10;
    public static final Integer UPLOAD_PHOTO_CODE = 20;

    private LoadPost loadPost;
    private Camera camera;
    private File photoFile;
    public String photoFileName = "photo.jpg";

    private Button btnLogout;
    private Button btnTakePhoto;
    private Button btnUploadPhoto;
    private TextView tvName;
    private ImageView ivProfileImage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogout = view.findViewById(R.id.btnLogout);
        tvName = view.findViewById(R.id.tvProfileProfileName);
        ivProfileImage = view.findViewById(R.id.ivProfileProfileImage);
        btnTakePhoto = view.findViewById(R.id.btnProfileTakePhoto);
        btnUploadPhoto = view.findViewById(R.id.btnProfileUploadPhoto);

        loadPost = new LoadPost();
        camera = new Camera();

        loadPost.setUser(ParseUser.getCurrentUser(), getContext(), tvName, ivProfileImage);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhoto();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "logout user");
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    protected void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = camera.getPhotoFileUri(photoFileName, getContext());

        // wrap File object into a content provider; required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.donateGood", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public void pickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, UPLOAD_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i(TAG, "onActivityResult");
        Bitmap image = null;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                image = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if ((data != null) && requestCode == UPLOAD_PHOTO_CODE) {
            Uri photoUri = data.getData();
            image = camera.loadFromUri(photoUri, getContext());
            photoFile = camera.createFile(getContext(), image);
        }
        ivProfileImage.setImageBitmap(image);
        ParseFile file = new ParseFile(photoFile);
        ParseUser.getCurrentUser().put("profileImage", file);
        ParseUser.getCurrentUser().saveInBackground();
    }
}