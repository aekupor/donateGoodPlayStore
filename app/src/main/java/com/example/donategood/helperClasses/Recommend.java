package com.example.donategood.helperClasses;

import android.util.Log;

import com.example.donategood.models.Charity;
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

    public void getRecommendedOfferings(final Offering mainOffering) {
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

                    if (offering.equals(mainOffering)) {
                        //if offering is the same, do not include as recommended offering
                        continue;
                    }

                    Integer pointValue = getPointValue(mainOffering, offering);
                    pointValues.put(offering, pointValue);
                }
            }
        });
    }

    private Integer getPointValue(Offering mainOffering, Offering offering) {
        Integer pointValue = 0;
        pointValue += checkPrice(mainOffering.getPrice(), offering.getPrice());
        pointValue += checkCharity(mainOffering.getCharity(), offering.getCharity());
        return pointValue;
    }

    private Integer checkPrice(Integer mainPrice, Integer otherPrice) {
        Integer priceDifference = Math.abs(mainPrice - otherPrice);
        if (priceDifference <= 5) {
            return 2;
        } else if (priceDifference <= 20) {
            return 1;
        }
        return 0;
    }

    private Integer checkCharity(Charity mainCharity, Charity otherCharity) {
        if (mainCharity.equals(otherCharity)) {
            return 2;
        }
        //TODO: if same type of charity (i.e. environmental, BLM, etc.), add 1 point
        return 0;
    }
}
