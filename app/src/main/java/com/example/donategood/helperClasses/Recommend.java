package com.example.donategood.helperClasses;

import android.util.Log;

import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.models.Charity;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Recommend {

    public static final String TAG = "Recommend";

    //sorts map with the largest number of points first
    public HashMap<Offering, Integer> sortMapByPoints(Map<Offering, Integer> pointValues) {
        // Create a list from elements of HashMap
        List<Map.Entry<Offering, Integer> > list = new LinkedList<Map.Entry<Offering, Integer> >(pointValues.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Offering, Integer> >() {
            public int compare(Map.Entry<Offering, Integer> o1, Map.Entry<Offering, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Offering, Integer> temp = new LinkedHashMap<Offering, Integer>();
        for (Map.Entry<Offering, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    //returns the number of points for the corresponding offering
    public Integer getPointValue(Offering mainOffering, Offering offering) {
        Integer pointValue = 0;
        pointValue += checkPrice(mainOffering.getPrice(), offering.getPrice());
        pointValue += checkCharity(mainOffering.getCharity(), offering.getCharity());
        pointValue += checkTags(mainOffering.getTags(), offering.getTags());
        pointValue += checkSellingUser(mainOffering.getUser(), offering.getUser());
        pointValue += checkRating(offering);
        return pointValue;
    }

    private Integer checkPrice(Integer mainPrice, Integer otherPrice) {
        Integer priceDifference = Math.abs(mainPrice - otherPrice);
        if (priceDifference <= 5) {
            //if price difference between is less than $5
            return 2;
        } else if (priceDifference <= 20) {
            //if price difference between is less than $20
            return 1;
        }
        return 0;
    }

    private Integer checkCharity(Charity mainCharity, Charity otherCharity) {
        Charity fetchedMainCharity = null;
        Charity fetchedOtherCharity = null;
        try {
            fetchedMainCharity = mainCharity.fetchIfNeeded();
            fetchedOtherCharity = otherCharity.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (mainCharity.getObjectId().equals(otherCharity.getObjectId())) {
            //if same charity
            return 2;
        } else if (fetchedMainCharity.getGrouping().equals(fetchedOtherCharity.getGrouping())) {
            //if charity just in same grouping
            return 1;
        }
        return 0;
    }

    private Integer checkTags(ArrayList<String> mainTags, ArrayList<String> otherTags) {
        Integer points = 0;
        for (String tag : mainTags) {
            if (otherTags.contains(tag)) {
                //add one point per same tag
                points++;
            }
        }
        return points;
    }

    private Integer checkSellingUser(ParseUser mainUser, final ParseUser otherUser) {
        if (mainUser.getObjectId().equals(otherUser.getObjectId())) {
            //if selling user is the same
            return 2;
        }
        return 0;
    }
    
    private Integer checkRating(Offering offering) {
        return offering.getRating();
    }

    //query recommended posts based on current offering
    public void queryRecommendedPosts(Query query, final Offering offering, final SmallOfferingAdapter adapter, final List<Offering> reccomendedOfferings) {
        final Recommend recommend = new Recommend();
        final Map<Offering, Integer>[] pointValues = new Map[]{new HashMap<>()};

        //find all available posts
        query.queryAllAvailablePosts(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting offerings", e);
                    return;
                }
                for (Offering otherOffering : offerings) {
                    if (otherOffering.getObjectId().equals(offering.getObjectId())) {
                        //if offering is the same, do not include as recommended offering
                        continue;
                    }

                    //determine point value for each offering
                    Integer pointValue = recommend.getPointValue(offering, otherOffering);
                    pointValues[0].put(otherOffering, pointValue);
                }

                //sort map to have most recommended offerings show up at the top
                final Map<Offering, Integer>[] sortedPointValues = new Map[]{new HashMap<>()};
                sortedPointValues[0] = recommend.sortMapByPoints(pointValues[0]);
                Log.i(TAG, "sorted point values list: " + sortedPointValues[0].toString());

                //update adapter with recommended offerings
                adapter.clear();
                reccomendedOfferings.clear();
                reccomendedOfferings.addAll(sortedPointValues[0].keySet());
                adapter.notifyDataSetChanged();
            }
        });
    }
}
