package com.example.donategood.helperClasses;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

import com.example.donategood.models.Charity;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class Query {

    public void queryAllPosts(Integer page, FindCallback<Offering> callback) {
        Integer displayLimit = 2;
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.setLimit(displayLimit);
        query.setSkip(page * displayLimit);
        query.whereEqualTo("isBought", false);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    public void queryAllCharities(FindCallback<Charity> callback) {
        ParseQuery<Charity> query = ParseQuery.getQuery(Charity.class);
        query.findInBackground(callback);
    }

    public void queryCharityByName(String charityName, FindCallback<Charity> callback) {
        ParseQuery<Charity> query = ParseQuery.getQuery(Charity.class);
        query.setLimit(1);
        query.whereEqualTo("title", charityName);
        query.findInBackground(callback);
    }

    public void queryUserByName(String userName, FindCallback<ParseUser> callback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setLimit(1);
        query.whereEqualTo("username", userName);
        query.findInBackground(callback);
    }

    public void queryOfferingById(String postId, FindCallback<Offering> callback) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.setLimit(1);
        query.whereEqualTo("objectId", postId);
        query.findInBackground(callback);
    }

    public void queryBoughtPostsByUser(ParseUser user, FindCallback<Offering> callback) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereEqualTo("isBought", true);
        query.whereEqualTo("boughtBy", user);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    public void querySellingPostsByUser(ParseUser user, FindCallback<Offering> callback) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereEqualTo("isBought", false);
        query.whereEqualTo("user", user);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    public void queryMoneyRaised(final ParseUser user, final TextView tvMoney) {
        final Integer[] moneyRaised = {0};
        queryMoneyBought(user, new FindCallback<Offering>() {
            @SuppressLint("LongLogTag")
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    return;
                }
                for (Offering offering : offerings) {
                    moneyRaised[0] += offering.getPrice();
                }

                queryMoneySold(user, new FindCallback<Offering>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void done(List<Offering> offerings, ParseException e) {
                        if (e != null) {
                            return;
                        }
                        for (Offering offering : offerings) {
                            moneyRaised[0] += offering.getPrice();
                        }
                        tvMoney.setText(moneyRaised[0].toString());
                    }
                });
            }
        });
    }

    public void queryMoneyBought(ParseUser user, FindCallback<Offering> callback) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereEqualTo("isBought", true);
        query.whereEqualTo("boughtBy", user);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    public void queryMoneySold(ParseUser user, FindCallback<Offering> callback) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereEqualTo("isBought", true);
        query.whereEqualTo("user", user);
        query.findInBackground(callback);
    }
}
