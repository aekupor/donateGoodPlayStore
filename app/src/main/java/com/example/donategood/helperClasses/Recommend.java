package com.example.donategood.helperClasses;

import android.util.Log;

import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recommend {

    public static final String TAG = "Recommend";

    private Map<Offering, Integer> pointValues;
    private Query query;

    private ArrayList<String> mainTags;

    public void getRecommendedOfferings(final Offering mainOffering) {
        pointValues = new HashMap<>();
        mainTags = mainOffering.getTags();
        query = new Query();
        query.queryAllPostsWithoutPage(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting offerings", e);
                    return;
                }
                for (Offering offering : offerings) {
                    Log.i(TAG, "Offering: " + offering.getTitle());

                    if (offering.equals(mainOffering)) {
                        //if offering is the same, do not include as recommended offering
                        continue;
                    }

                    //add point value of 1 as placeholder
                    pointValues.put(offering, 1);
                }
            }
        });
    }
}
