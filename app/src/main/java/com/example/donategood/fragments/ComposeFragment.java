package com.example.donategood.fragments;

import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.donategood.MainActivity;
import com.example.donategood.R;
import com.example.donategood.helperClasses.Camera;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Charity;
import com.example.donategood.models.Offering;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private EditText etVenmo;
    private EditText etDescription;
    private TextInputLayout etVenmoBox;

    private String title;
    private String price;
    private ArrayList<String> tags;
    private String charity;
    private String quantity;
    private Spinner spinner;
    private Offering editOffering;
    private ArrayAdapter<String> spinnerAdapter;

    public ComposeFragment() {
        //required empty public constructor
    }

    public static ComposeFragment newInstance(Bundle bundle) {
        ComposeFragment fragment = new ComposeFragment();
        if (bundle != null) {
            Bundle args = new Bundle();
            Offering offering = bundle.getParcelable("offering");
            Log.i(TAG, "got offering: " + offering.getTitle());
            args.putParcelable("offering", offering);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            editOffering = getArguments().getParcelable("offering");
        }
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
        etVenmo = view.findViewById(R.id.etVenmoCompose);
        etVenmoBox = view.findViewById(R.id.etVenmoComposeBox);
        etDescription = view.findViewById(R.id.etDescription);

        etVenmoBox.setVisibility(View.INVISIBLE);
        etVenmo.setVisibility(View.INVISIBLE);

        query = new Query();
        camera = new Camera();

        spinner = (Spinner) view.findViewById(R.id.spinnerCharity);
        setUpSpinner();

        btnTakeMultiple.setVisibility(View.INVISIBLE);
        btnUploadPhoto.setVisibility(View.INVISIBLE);
        btnTakePhoto.setVisibility(View.INVISIBLE);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //when photo is clicked, show buttons to take photos
                btnTakeMultiple.setVisibility(View.VISIBLE);
                btnUploadPhoto.setVisibility(View.VISIBLE);
                btnTakePhoto.setVisibility(View.VISIBLE);
            }
        });

        btnTakeMultiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnTakeMultiple onClick");
                camera.pickMultiplePhotos(getContext());
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
                camera.pickPhoto(getContext(), false, false);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "btnSubmit onClick");

                if (ParseUser.getCurrentUser().get("venmoName") == null) {
                    Log.i(TAG, "user doesn't have venmo name");

                    //if user doesn't have a venmo username stored, make them enter one and save to Parse
                    etVenmo.setVisibility(View.VISIBLE);
                    etVenmoBox.setVisibility(View.VISIBLE);

                    if (etVenmo.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "You must add your venmo username", Toast.LENGTH_SHORT).show();
                    } else {
                        ParseUser.getCurrentUser().put("venmoName", etVenmo.getText().toString());
                        ParseUser.getCurrentUser().saveInBackground();
                        etVenmo.setVisibility(View.INVISIBLE);
                    }
                }

                title = etTitle.getText().toString();
                price = etPrice.getText().toString();
                tags = editTags(etTags.getText().toString());
                quantity = etQuantity.getText().toString();

                //make sure required field aren't empty
                if (title.isEmpty()) {
                    Toast.makeText(getContext(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (price.isEmpty() || quantity.isEmpty()) {
                    Toast.makeText(getContext(), "Price and quantity cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    savePost();
                }
            }
        });
    }

    //pre-fill fields of offering that is to be edited
    public void preFillFields() {
        etTitle.setText(editOffering.getTitle());
        etPrice.setText(Integer.toString(editOffering.getPrice()));
        etQuantity.setText(Integer.toString(editOffering.getQuantityLeft()));
        etDescription.setText(editOffering.getDescription());

        //set charity spinner value
        int spinnerPosition = spinnerAdapter.getPosition(editOffering.getCharity().getTitle());
        spinner.setSelection(spinnerPosition);

        //make tags prettier
        StringBuilder listWithCommas = new StringBuilder("");
        for (String tag : editOffering.getTags()){
            listWithCommas.append(tag).append(", ");
        }
        String strList = listWithCommas.toString();
        etTags.setText(strList);

        //load image
        Glide.with(getContext())
                .load(editOffering.getImage().getUrl())
                .into(ivPhoto);
    }

    //create ArrayList<String> of tags the user has entered
    private ArrayList<String> editTags(String tags) {
        String[] tagArray = tags.split(", ");
        ArrayList<String> tagList = new ArrayList<>();
        tagList.addAll(Arrays.asList(tagArray));
        Log.i(TAG, "TAGS: " + tagList.toString());
        return tagList;
    }

    private void savePost() {
        pb.setVisibility(ProgressBar.VISIBLE);
        //query to find the charity that the user selected from the spinner
        query.findCharity(charity, new FindCallback<Charity>() {
            @Override
            public void done(List<Charity> charities, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Successfully got charity");

                ArrayList<ParseFile> fileList = MainActivity.getParseFileList();

                final Offering offering;
                if (editOffering != null) {
                     offering = editOffering;
                } else {
                     offering = new Offering();
                }

                //set all elements of the offering
                if (editOffering == null) {
                    //only set images for a new offering
                    if (fileList != null) {
                        //offering has multiple images; save all images to backend
                        Log.i(TAG, "got array of size: " + fileList.size());

                        ArrayList<File> photoFileArray = camera.getPhotoFileArray();
                        ArrayList<ParseFile> photoParseFileArray = new ArrayList<>();

                        for (File photo : photoFileArray) {
                            photoParseFileArray.add(new ParseFile(photo));
                        }

                        offering.setImagesArray(photoParseFileArray);
                        offering.setHasMultipleImages(true);
                        offering.setImage(photoParseFileArray.get(0));
                    } else {
                        //offering has one image
                        offering.setImage(new ParseFile(camera.getPhotoFile()));
                        offering.setHasMultipleImages(false);
                    }
                }

                if (etDescription.getText() != null) {
                    //if user entered a description, save to backend
                    offering.setDescription(etDescription.getText().toString());
                }
                offering.setTitle(title);
                offering.setPrice(Integer.valueOf(price));
                offering.setCharity(charities.get(0));
                offering.setTags(tags);
                offering.setQuantityLeft(Integer.valueOf(quantity));
                offering.setUser(ParseUser.getCurrentUser());

                //save offering to backend
                offering.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while saving", e);
                            Toast.makeText(getContext(), "Error while saving!", Toast.LENGTH_SHORT).show();
                        }
                        Log.i(TAG, "Post save was successful!");
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

    //initialize the spinner
    private void setUpSpinner() {
        final List<String> charitiesNames = new ArrayList<>();
        final String KEY_NEW_CHARITY = "Create New Charity";

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
                //add option to create a new charity
                charitiesNames.add(KEY_NEW_CHARITY);

                // Create an ArrayAdapter for spinner
                spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, charitiesNames);

                // Specify the layout to use when the list of choices appears
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(spinnerAdapter);

                //must wait to pre-fill fields until spinnerAdapter is already intialized
                //if offering is already created and just needs to be edited
                if (editOffering != null) {
                    preFillFields();
                }

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                        charity = (String) adapterView.getItemAtPosition(pos);
                        if (charity.equals(KEY_NEW_CHARITY)) {
                            //if user clicks to create a new charity, go to new charity fragment
                            final FragmentManager fragmentManager = ((AppCompatActivity)getContext()).getSupportFragmentManager();
                            Fragment fragment = new NewCharityFragment();
                            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        }
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