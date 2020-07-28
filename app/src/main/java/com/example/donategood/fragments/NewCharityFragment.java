package com.example.donategood.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.donategood.R;
import com.example.donategood.helperClasses.Camera;
import com.example.donategood.models.Charity;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

public class NewCharityFragment extends Fragment {

    public static final String TAG = "NewCharityFragment";

    private EditText etCharityName;
    private EditText etCharityWebsite;
    private EditText etCharityGrouping;
    private Button btnCharityPhoto;
    private Button btnSubmit;

    private static Camera camera;

    public NewCharityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_charity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etCharityName = view.findViewById(R.id.etCharityName);
        etCharityWebsite = view.findViewById(R.id.etCharityWebsite);
        etCharityGrouping = view.findViewById(R.id.etCharityGrouping);
        btnCharityPhoto = view.findViewById(R.id.btnCharityUploadPhoto);
        btnSubmit = view.findViewById(R.id.btnNewCharitySubmit);

        camera = new Camera();

        btnCharityPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnCharityPhoto clicked");
                camera.pickPhoto(getContext(), false, true);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnSubmit clicked");

                //create new Charity and save in background
                Charity charity = new Charity();
                charity.setTitle(etCharityName.getText().toString());
                charity.setWebsite(etCharityWebsite.getText().toString());
                charity.setGrouping(etCharityGrouping.getText().toString());
                charity.setImage((new ParseFile(camera.getPhotoFile())));
                charity.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while saving", e);
                            Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                        }
                        Log.i(TAG, "Charity save was successful!");

                        //go back to detail fragment
                        final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                        Fragment fragment = new ComposeFragment();
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                    }
                });
            }
        });
    }

    public static Camera getCamera() {
        return camera;
    }
}