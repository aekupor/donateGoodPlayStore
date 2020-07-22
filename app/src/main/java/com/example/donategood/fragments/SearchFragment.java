package com.example.donategood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.donategood.R;
import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.helperClasses.Query;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";

    private EditText etSearchText;
    private Spinner spPrice;
    private Button btnSearch;

    private RecyclerView rvOfferings;
    private SmallOfferingAdapter adapter;
    private List<Offering> allOfferings;
    private Query query;

    private Integer minPrice;
    private Integer maxPrice;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearchText = view.findViewById(R.id.etSearchBar);
        btnSearch = view.findViewById(R.id.btnSearch);
        rvOfferings = view.findViewById(R.id.rvSearchOfferings);
        spPrice = (Spinner) view.findViewById(R.id.spinnerPriceSearch);

        setUpSpinner();

        query = new Query();
        allOfferings = new ArrayList<>();
        adapter = new SmallOfferingAdapter(getContext(), allOfferings);

        rvOfferings.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvOfferings.setLayoutManager(linearLayoutManager);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = etSearchText.getText().toString();
                if (searchText.isEmpty()) {
                    Toast.makeText(getContext(), "Search cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    querySearch(searchText);
                }
            }
        });
    }

    private void querySearch(String searchText) {
        Log.i(TAG, "Search for: " + searchText);
        query.search(searchText, new FindCallback<Offering>() {
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
                allOfferings.addAll(offerings);
                adapter.notifyDataSetChanged();
            }
        }, minPrice, maxPrice);
    }

    private void setUpSpinner() {
        final List<String> priceRanges = new ArrayList<>();
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

                if (price != "50+") {
                    String[] priceArray = price.split(" - ");
                    minPrice = Integer.parseInt(priceArray[0]);
                    maxPrice = Integer.parseInt(priceArray[1]);
                } else {
                    minPrice = 50;
                    maxPrice = Integer.MAX_VALUE;
                }
                Log.i(TAG, "price min: " + minPrice.toString() + " max: " + maxPrice.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(TAG, "onNothingSelected");
            }

        });
    }
}