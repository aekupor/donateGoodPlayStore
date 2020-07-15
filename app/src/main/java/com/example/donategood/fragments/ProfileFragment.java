package com.example.donategood.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donategood.adapters.OfferingAdapter;
import com.example.donategood.helperClasses.Camera;
import com.example.donategood.helperClasses.LoadPost;
import com.example.donategood.LoginActivity;
import com.example.donategood.R;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public static final Integer CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 10;
    public static final Integer UPLOAD_PHOTO_CODE = 20;

    private LoadPost loadPost;
    private static Camera camera;
    private File photoFile;
    public String photoFileName = "photo.jpg";

    private Button btnLogout;
    private Button btnTakePhoto;
    private Button btnUploadPhoto;
    private TextView tvName;
    private ImageView ivProfileImage;

    private RecyclerView rvBoughtItems;
    private OfferingAdapter adapter;
    private List<Offering> boughtOfferings;
    private Query query;

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
        rvBoughtItems = view.findViewById(R.id.rvBoughtItems);

        query = new Query();
        boughtOfferings = new ArrayList<>();
        adapter = new OfferingAdapter(getContext(), boughtOfferings);

        rvBoughtItems.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvBoughtItems.setLayoutManager(linearLayoutManager);

        loadPost = new LoadPost();
        camera = new Camera();

        loadPost.setUser(ParseUser.getCurrentUser(), getContext(), tvName, ivProfileImage);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.launchCamera(getContext(), true);
            }
        });

        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.pickPhoto(getContext(), true);
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

        queryBoughtPosts();
    }

    protected void queryBoughtPosts() {
        query.queryBoughtPostsByUser(ParseUser.getCurrentUser(), new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting offerings", e);
                    return;
                }
                for (Offering offering : offerings) {
                    Log.i(TAG, "Offering: " + offering.getTitle());
                }
                boughtOfferings.addAll(offerings);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public static Camera getCamera() {
        return camera;
    }
}