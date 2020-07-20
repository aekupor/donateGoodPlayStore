package com.example.donategood.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.donategood.helperClasses.Camera;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.R;
import com.example.donategood.models.Charity;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {

    public static final String TAG = "ComposeFragment";

    private Query query;
    private static Camera camera;

    private EditText etTitle;
    private EditText etPrice;
    private Button btnTakePhoto;
    private Button btnUploadPhoto;
    private ImageView ivPhoto;
    private EditText etTags;
    private Button btnSubmit;
    private ProgressBar pb;
    private EditText etQuantity;
    private Button btnTakeMultiple;

    private String title;
    private String price;
    private ArrayList<String> tags;
    private String charity;
    private String quantity;
    private Spinner spinner;

    public ComposeFragment() {
        //required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTitle = view.findViewById(R.id.etTitle);
        etPrice = view.findViewById(R.id.etPrice);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto);
        ivPhoto = view.findViewById(R.id.ivComposePhoto);
        etTags = view.findViewById(R.id.etTags);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        pb = (ProgressBar) view.findViewById(R.id.pbLoading);
        etQuantity = view.findViewById(R.id.etQuantity);
        btnTakeMultiple = view.findViewById(R.id.btnTakeMultiple);

        query = new Query();
        camera = new Camera();

        spinner = (Spinner) view.findViewById(R.id.spinnerCharity);
        setUpSpinner(view);

        btnTakeMultiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnTakeMultiple onClick");
                
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnTakePhoto onClick");
                camera.launchCamera(getContext(), false);
            }
        });

        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnUploadPhoto onClick");
                camera.pickPhoto(getContext(), false);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnSubmit onClick");
                title = etTitle.getText().toString();
                price = etPrice.getText().toString();
                tags = editTags(etTags.getText().toString());
                quantity = etQuantity.getText().toString();

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

    private ArrayList<String> editTags(String tags) {
        String[] tagArray = tags.split(", ");
        ArrayList<String> tagList = new ArrayList<>();
        tagList.addAll(Arrays.asList(tagArray));
        Log.i(TAG, "TAGS: " + tagList.toString());
        return tagList;
    }

    private void savePost() {
        pb.setVisibility(ProgressBar.VISIBLE);
        query.queryCharityByName(charity, new FindCallback<Charity>() {
            @Override
            public void done(List<Charity> charities, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Successfully got charity");

                final Offering offering = new Offering();
                offering.setTitle(title);
                offering.setImage(new ParseFile(camera.getPhotoFile()));
                offering.setPrice(Integer.valueOf(price));
                offering.setCharity(charities.get(0));
                offering.setTags(tags);
                offering.setQuantityLeft(Integer.valueOf(quantity));
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
                        etTags.setText("");
                        ivPhoto.setImageResource(0);
                        pb.setVisibility(ProgressBar.INVISIBLE);

                        //go to detail fragment
                        final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                        Fragment fragment = DetailFragment.newInstance(offering.getObjectId());
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                    }
                });
            }
        });
    }

    private void setUpSpinner(View view) {
        final List<String> charitiesNames = new ArrayList<>();
        query.queryAllCharities(new FindCallback<Charity>() {
            @Override
            public void done(List<Charity> charities, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error getting charities", e);
                }
                Log.i(TAG, "Successfully got charities");
                //populate charitiesNames with all names of charities in database
                for (Charity charity : charities) {
                    charitiesNames.add(charity.getTitle());
                }

                // Create an ArrayAdapter for spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, charitiesNames);

                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                        charity = (String) adapterView.getItemAtPosition(pos);
                        Log.i(TAG, "onItemSelected with charity: " + charity);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        Log.i(TAG, "onNothingSelected");
                    }
                });
            }
        });
    }

    public static Camera getCamera() {
        return camera;
    }
}