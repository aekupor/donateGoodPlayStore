package com.example.donategood.helperClasses;

import android.util.Log;

import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Recommend {

    public static final String TAG = "Recommend";

    private Map<Offering, Integer> pointValues;
    private Query query;

    public void getRecommendedOfferings(Offering offering) {
        pointValues = new HashMap<>();
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
                    //add point value of 1 as placeholder
                    pointValues.put(offering, 1);
                }
            }
        });
    }
}
