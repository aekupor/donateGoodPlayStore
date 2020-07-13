package com.example.donategood;

import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseQuery;

public class Query {

    public void queryAllPosts(FindCallback<Offering> callback) {
        Integer displayLimit = 20;
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.setLimit(displayLimit);
        query.whereEqualTo("isBought", false);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }
}
