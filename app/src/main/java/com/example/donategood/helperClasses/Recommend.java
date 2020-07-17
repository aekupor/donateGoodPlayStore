package com.example.donategood.helperClasses;

import android.util.Log;

import com.example.donategood.models.Charity;
import com.example.donategood.models.Comment;
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

    final Integer[] points = {0};

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

    public Integer getPointValue(Offering mainOffering, Offering offering) {
        Integer pointValue = 0;
        checkRating(offering);
        pointValue += checkPrice(mainOffering.getPrice(), offering.getPrice());
        pointValue += checkCharity(mainOffering.getCharity(), offering.getCharity());
        pointValue += checkTags(mainOffering.getTags(), offering.getTags());
        pointValue += checkSellingUser(mainOffering.getUser(), offering.getUser());
        pointValue += points[0];
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
        Charity fetchedMainCharity = null;
        Charity fetchedOtherCharity = null;
        try {
            fetchedMainCharity = mainCharity.fetchIfNeeded();
            fetchedOtherCharity = otherCharity.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (mainCharity.getObjectId().equals(otherCharity.getObjectId())) {
            return 2;
        } else if (fetchedMainCharity.getGrouping().equals(fetchedOtherCharity.getGrouping())) {
            return 1;
        }
        return 0;
    }

    private Integer checkTags(ArrayList<String> mainTags, ArrayList<String> otherTags) {
        Integer points = 0;
        for (String tag : mainTags) {
            if (otherTags.contains(tag)) {
                points++;
            }
        }
        return points;
    }

    private Integer checkSellingUser(ParseUser mainUser, ParseUser otherUser) {
        if (mainUser.getObjectId().equals(otherUser.getObjectId())) {
            return 2;
        }
        return 0;
    }
    
    private void checkRating(Offering offering) {
        Query query = new Query();
        final Integer[] totalRating = {0};
        final Integer[] totalComments = {0};
        query.queryComments(offering, new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting user comments", e);
                    return;
                }
                for (Comment comment : objects) {
                    totalRating[0] += comment.getRating();
                    totalComments[0]++;
                }
                if (totalComments[0] != 0) {
                    points[0] = totalRating[0] / totalComments[0];
                } else {
                    points[0] = 0;
                }
            }
        });
    }
}
