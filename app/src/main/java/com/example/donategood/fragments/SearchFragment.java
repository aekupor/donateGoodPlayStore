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
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donategood.R;
import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.adapters.UserAdapter;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Offering;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

    private EditText etSearchText;
    private Spinner spPrice;
    private RatingBar ratingBar;
    private Button btnSearch;
    private TextView tvPriceTitle;
    private TextView tvRatingTitle;

    private RecyclerView rvOfferings;
    private SmallOfferingAdapter adapter;
    private List<Offering> allOfferings;
    private Query query;

    private UserAdapter userAdapter;
    private List<ParseUser> allUsers;

    private Integer minPrice;
    private Integer maxPrice;
    private Integer minRating;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeVariables(view);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.searchTabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    Log.i(TAG, "search by offering clicked");
                    setVisbilityForOfferingSearch();
                } else if (position == 1) {
                    Log.i(TAG, "search by charity clicked");
                } else {
                    Log.i(TAG, "search by user clicked");
                    setVisibilityForUserSearch();
                    setSubmitButtonForUserSearch();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setSubmitButtonForUserSearch() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = etSearchText.getText().toString();
                if (searchText.isEmpty()) {
                    Toast.makeText(getContext(), "Search cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    querySearchForUser(searchText);
                }
            }
        });
    }

    private void setSubmitButtonForOfferingSearch() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minRating = Math.round(ratingBar.getRating());

                String searchText = etSearchText.getText().toString();
                if (searchText.isEmpty()) {
                    Toast.makeText(getContext(), "Search cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    querySearchForOffering(searchText);
                }
            }
        });
    }

    private void setVisibilityForUserSearch() {
        spPrice.setVisibility(View.INVISIBLE);
        ratingBar.setVisibility(View.INVISIBLE);
        tvRatingTitle.setVisibility(View.INVISIBLE);
        tvPriceTitle.setVisibility(View.INVISIBLE);
        etSearchText.setHint("user name");

        allUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), allUsers);

        rvOfferings.setAdapter(userAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvOfferings.setLayoutManager(linearLayoutManager);
    }

    private void setVisbilityForOfferingSearch() {
        allOfferings.clear();
        adapter.clear();
        spPrice.setVisibility(View.VISIBLE);
        ratingBar.setVisibility(View.VISIBLE);
        tvRatingTitle.setVisibility(View.VISIBLE);
        tvPriceTitle.setVisibility(View.VISIBLE);
        etSearchText.setHint("offering title");
    }

    private void initializeVariables(View view) {
        etSearchText = view.findViewById(R.id.etSearchBar);
        btnSearch = view.findViewById(R.id.btnSearch);
        rvOfferings = view.findViewById(R.id.rvSearchOfferings);
        spPrice = (Spinner) view.findViewById(R.id.spinnerPriceSearch);
        ratingBar = (RatingBar) view.findViewById(R.id.rbSearchRating);
        tvPriceTitle = view.findViewById(R.id.tvPriceTitle);
        tvRatingTitle = view.findViewById(R.id.tvRatingTitle);

        ratingBar.setEnabled(true);
        etSearchText.setHint("offering title");

        setUpPriceSpinner();

        query = new Query();
        allOfferings = new ArrayList<>();
        adapter = new SmallOfferingAdapter(getContext(), allOfferings);

        rvOfferings.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvOfferings.setLayoutManager(linearLayoutManager);

        setSubmitButtonForOfferingSearch();
    }

    private void querySearchForOffering(String searchText) {
        Log.i(TAG, "Search for: " + searchText);
        query.searchForOffering(searchText, new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting offerings", e);
                    return;
                }
                if (offerings.size() == 0) {
                    Log.i(TAG, "No posts found");
                    Toast.makeText(getContext(), "No posts were found", Toast.LENGTH_SHORT).show();
                    return;
                }
                allOfferings.clear();
                allOfferings.addAll(offerings);
                adapter.notifyDataSetChanged();
            }
        }, minPrice, maxPrice, minRating);
    }

    private void querySearchForUser(String searchText) {
        Log.i(TAG, "Search for: " + searchText);
        query.findUser(searchText, new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    return;
                }
                for (ParseUser user : objects) {
                    Log.i(TAG, "found user: " + user.getUsername());
                }
            }
        });
    }

    private void setUpPriceSpinner() {
        final List<String> priceRanges = new ArrayList<>();
        priceRanges.add("All prices");
        priceRanges.add("0 - 15");
        priceRanges.add("15 - 30");
        priceRanges.add("30 - 50");
        priceRanges.add("50+");

        // Create an ArrayAdapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, priceRanges);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spPrice.setAdapter(adapter);

        spPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String price = (String) adapterView.getItemAtPosition(pos);
                determinePriceRange(price);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(TAG, "onNothingSelected");
            }

        });
    }

    private void determinePriceRange(String price) {
        if (price.equals("All prices")) {
            minPrice = 0;
            maxPrice = Integer.MAX_VALUE;
        } else if (!price.equals("50+")) {
            String[] priceArray = price.split(" - ");
            minPrice = Integer.parseInt(priceArray[0]);
            maxPrice = Integer.parseInt(priceArray[1]);
        } else {
            minPrice = 50;
            maxPrice = Integer.MAX_VALUE;
        }
        Log.i(TAG, "price min: " + minPrice.toString() + " max: " + maxPrice.toString());
    }
}