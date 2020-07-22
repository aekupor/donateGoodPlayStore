package com.example.donategood.helperClasses;

import android.graphics.Typeface;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.donategood.adapters.SmallOfferingAdapter;
import com.example.donategood.models.Charity;
import com.example.donategood.models.Comment;
import com.example.donategood.models.Notification;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class Query {

    public static final String KEY_BOUGHT = "bought";
    public static final String KEY_SELLING = "selling";
    public static final String KEY_SOLD = "sold";

    public void queryAllPosts(Integer page, FindCallback<Offering> callback) {
        Integer displayLimit = 20;
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.setLimit(displayLimit);
        query.setSkip(page * displayLimit);
        query.whereEqualTo("isBought", false);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    public void queryAllPostsWithoutPage(FindCallback<Offering> callback) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereEqualTo("isBought", false);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    public void queryAllPostsBoughtAndNot(FindCallback<Offering> callback) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
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

    public void queryComments(Offering offering, FindCallback<Comment> callback) {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.whereEqualTo("forPost", offering);
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

    public void querySellingPostsByUser(ParseUser user, Boolean bought, final SmallOfferingAdapter adapter, final List<Offering> offeringsList, final ProgressBar pb) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereEqualTo("isBought", bought);
        query.whereEqualTo("user", user);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                if (e != null) {
                    return;
                }
                adapter.clear();
                offeringsList.clear();
                offeringsList.addAll(objects);
                adapter.notifyDataSetChanged();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    public void queryPostsByCharity(Charity charity, Boolean bought, FindCallback<Offering> callback) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereEqualTo("isBought", bought);
        query.whereEqualTo("charity", charity);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    public void queryCharityMoneyRaised(final Charity charity, final TextView tvMoney) {
        final Integer[] moneyRaised = {0};
        queryAllPostsBoughtAndNot(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                for (Offering offering : objects) {
                    if (offering.getCharity().getObjectId().equals(charity.getObjectId())) {
                        ArrayList<Object> boughtUsers = offering.getBoughtByArray();
                        if (boughtUsers != null && !boughtUsers.isEmpty()) {
                            moneyRaised[0] += boughtUsers.size() * offering.getPrice();
                        }
                    }
                }
                tvMoney.setText("$" + moneyRaised[0].toString());
            }
        });
    }

    public void search(String searchText, FindCallback<Offering> callback, Integer minPrice, Integer maxPrice, Integer minRating) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereContains("title", searchText);
        query.whereEqualTo("isBought", false);
        query.whereGreaterThanOrEqualTo("price", minPrice);
        query.whereLessThanOrEqualTo("price", maxPrice);
        query.whereGreaterThanOrEqualTo("rating", minRating);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    public void queryBoughtPostsByUser(final ParseUser currentUser, final SmallOfferingAdapter adapter, final List<Offering> selectedOfferings, final ProgressBar pb) {
        queryAllPostsBoughtAndNot(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> offerings, ParseException e) {
                if (e != null) {
                    return;
                }

                List<Offering> newOfferings = new ArrayList<>();
                for (Offering offering : offerings) {
                    ArrayList<Object> boughtUsers = offering.getBoughtByArray();
                    if (boughtUsers != null && !boughtUsers.isEmpty()) {
                        for (Object object : boughtUsers) {
                            ParseUser user = (ParseUser) object;
                            if (user.getObjectId().equals(currentUser.getObjectId())) {
                                newOfferings.add(offering);
                            }
                        }
                    }
                }
                adapter.clear();
                selectedOfferings.clear();
                selectedOfferings.addAll(newOfferings);
                adapter.notifyDataSetChanged();
                pb.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }
    public void queryPosts(ParseUser user, String queryType, final SmallOfferingAdapter adapter, final List<Offering> selectedOfferings, final ProgressBar pb) {
        if (queryType.equals(KEY_BOUGHT)) {
            queryBoughtPostsByUser(user, adapter, selectedOfferings, pb);
        } else if (queryType.equals(KEY_SELLING)) {
            querySellingPostsByUser(user, false, adapter, selectedOfferings, pb);
        } else if (queryType.equals(KEY_SOLD)) {
            querySellingPostsByUser(user, true, adapter, selectedOfferings, pb);
        }
    }

    public void setBold(String queryType, TextView tvSold, TextView tvSelling, TextView tvBought) {
        tvSold.setTypeface(null, Typeface.NORMAL);
        tvSelling.setTypeface(null, Typeface.NORMAL);
        tvBought.setTypeface(null, Typeface.NORMAL);
        if (queryType.equals(KEY_BOUGHT)) {
            tvBought.setTypeface(null, Typeface.BOLD);
        } else if (queryType.equals(KEY_SELLING)) {
            tvSelling.setTypeface(null, Typeface.BOLD);
        } else {
            tvSold.setTypeface(null, Typeface.BOLD);
        }
    }

    public void queryMoneyRaised(final ParseUser currentUser, final TextView tvMoneyRaised) {
        final Integer[] moneyRaised = {0};
        final Integer[] moneySold = {0};

        queryAllPostsBoughtAndNot(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                for (Offering offering : objects) {
                    ArrayList<Object> boughtUsers = offering.getBoughtByArray();
                    if (boughtUsers != null && !boughtUsers.isEmpty()) {
                        for (Object object : boughtUsers) {
                            ParseUser user = (ParseUser) object;
                            if (user.getObjectId().equals(currentUser.getObjectId())) {
                                moneyRaised[0] += offering.getPrice();
                            }
                        }
                        if (offering.getUser().getObjectId().equals(currentUser.getObjectId())) {
                            moneySold[0] += offering.getPrice() * boughtUsers.size();
                        }
                    }
                }
                Integer totalMoney = moneyRaised[0] + moneySold[0];
                tvMoneyRaised.setText("$" + totalMoney.toString());
            }
        });
    }

    public void queryNotificationsForSeller(FindCallback<Notification> callback) {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        query.whereEqualTo("userActed", false);
        query.include("byUser");
        query.include("forOffering");
        query.findInBackground(callback);
    }

    public void queryNotificationsForBuyer(ParseUser user, FindCallback<Notification> callback) {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        query.whereEqualTo("byUser", user);
        query.include("byUser");
        query.include("forUser");
        query.include("forOffering");
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }
}
