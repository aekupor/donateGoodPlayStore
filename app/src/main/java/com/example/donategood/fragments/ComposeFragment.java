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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.donategood.Camera;
import com.example.donategood.Query;
import com.example.donategood.R;
import com.example.donategood.models.Offering;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {

    public static final String TAG = "ComposeFragment";
    public static final Integer CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 10;

    private Query query;
    private Camera camera;

    private EditText etTitle;
    private EditText etPrice;
    private Button btnTakePhoto;
    private ImageView ivPhoto;
    private Button btnSubmit;

    private String title;
    private String price;
    private File photoFile;
    public String photoFileName = "photo.jpg";

    public ComposeFragment() {
        //required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTitle = view.findViewById(R.id.etTitle);
        etPrice = view.findViewById(R.id.etPrice);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        ivPhoto = view.findViewById(R.id.ivComposePhoto);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        query = new Query();
        camera = new Camera();

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnTakePhoto onClick");
                launchCamera();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnSubmit onClick");
                title = etTitle.getText().toString();
                price = etPrice.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (price.isEmpty()) {
                    Toast.makeText(getContext(), "Price cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                savePost();
            }
        });
    }

    private void savePost() {
        Offering offering = new Offering();
        offering.setTitle(title);
        offering.setPrice(Integer.valueOf(price));
        offering.setUser(ParseUser.getCurrentUser());
        offering.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
                etTitle.setText("");
                etPrice.setText("");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                ivPhoto.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}