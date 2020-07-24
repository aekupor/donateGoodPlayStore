package com.example.donategood.helperClasses;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donategood.R;
import com.example.donategood.models.Charity;
import com.example.donategood.models.Comment;
import com.example.donategood.models.Notification;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Query {

    public static final String KEY_BOUGHT = "bought";
    public static final String KEY_SELLING = "selling";
    public static final String KEY_SOLD = "sold";
    private List<Offering> savedBoughtPostsForUser;

    //query all available posts with a page limit
    public void queryAllPostsByPage(Integer page, FindCallback<Offering> callback) {
        Integer displayLimit = 20;
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.setLimit(displayLimit);
        query.setSkip(page * displayLimit);
        query.whereEqualTo("isBought", false);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    //query all posts that are being sold (aka haven't been bought)
    public void queryAllAvailablePosts(FindCallback<Offering> callback) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereEqualTo("isBought", false);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    //query all posts, available and not available
    public void queryAllPosts(FindCallback<Offering> callback) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    //query list of all charities
    public void queryAllCharities(FindCallback<Charity> callback) {
        ParseQuery<Charity> query = ParseQuery.getQuery(Charity.class);
        query.findInBackground(callback);
    }

    //find specific charity from its name
    public void findCharity(String charityName, FindCallback<Charity> callback) {
        ParseQuery<Charity> query = ParseQuery.getQuery(Charity.class);
        query.whereEqualTo("title", charityName);
        query.findInBackground(callback);
    }

    //find all comments relating to an offering
    public void queryComments(Offering offering, FindCallback<Comment> callback) {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.whereEqualTo("forPost", offering);
        query.findInBackground(callback);
    }

    //find a specific user by their name
    public void findUser(String userName, FindCallback<ParseUser> callback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", userName);
        query.findInBackground(callback);
    }

    //find a specific offering but its id
    public void findOffering(String postId, FindCallback<Offering> callback) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.setLimit(1);
        query.whereEqualTo("objectId", postId);
        query.findInBackground(callback);
    }

    //finds posts that a user is selling
    public void queryPostsUserIsSelling(Boolean bought, final ParentProfile parentProfile) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereEqualTo("isBought", bought);
        query.whereEqualTo("user", parentProfile.user);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                if (e != null) {
                    return;
                }
                updateAdapter(parentProfile, objects);
            }
        });
    }

    //update adapter with list of offerings
    public void updateAdapter(ParentProfile parentProfile, List<Offering> objects) {
        parentProfile.adapter.clear();
        parentProfile.selectedOfferings.clear();
        parentProfile.selectedOfferings.addAll(objects);
        parentProfile.adapter.notifyDataSetChanged();
        parentProfile.pb.setVisibility(ProgressBar.INVISIBLE);
    }

    //determine and set user rating
    public void setUserRating(ParseUser user, final RatingBar ratingBar) {
        final Integer[] totalRating = {0};
        final Integer[] numPosts = {0};

        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereEqualTo("user", user);
        query.include("rating");
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                if (e != null) {
                    return;
                }
                for (Offering offering : objects) {
                    if (offering.getRating() != 0) {
                        totalRating[0] += offering.getRating();
                        numPosts[0]++;
                    }
                }
                if (numPosts[0] == 0) {
                    ratingBar.setNumStars(0);
                } else {
                    ratingBar.setNumStars(totalRating[0] / numPosts[0]);
                }
            }
        });
    }

    //find a charity's sold and sellings
    public void setCharityPosts(Boolean selling, final ParentProfile parentProfile) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereEqualTo("isBought", selling);
        query.whereEqualTo("charity", parentProfile.charity);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                if (e != null) {
                    return;
                }
                updateAdapter(parentProfile, objects);
            }
        });
    }

    //determines amount of money raised for a charity
    public void findCharityMoneyRaised(final Charity charity, final TextView tvMoney) {
        final Integer[] moneyRaised = {0};
        queryAllPosts(new FindCallback<Offering>() {
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

    //search for posts based on parameters
    public void searchForOffering(String searchText, FindCallback<Offering> callback, Integer minPrice, Integer maxPrice, Integer minRating) {
        ParseQuery<Offering> query = ParseQuery.getQuery(Offering.class);
        query.whereContains("title", searchText);
        query.whereEqualTo("isBought", false);
        query.whereGreaterThanOrEqualTo("price", minPrice);
        query.whereLessThanOrEqualTo("price", maxPrice);
        query.whereGreaterThanOrEqualTo("rating", minRating);
        query.addDescendingOrder(Offering.KEY_CREATED_AT);
        query.findInBackground(callback);
    }

    //find posts that a specific user has bought
    public void queryPostsUserBought(final ParentProfile parentProfile) {
        if (savedBoughtPostsForUser != null) {
            updateAdapter(parentProfile, savedBoughtPostsForUser);
        }
        queryAllPosts(new FindCallback<Offering>() {
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
                            if (user.getObjectId().equals(parentProfile.user.getObjectId())) {
                                newOfferings.add(offering);
                            }
                        }
                    }
                }
                savedBoughtPostsForUser = offerings;
                updateAdapter(parentProfile, newOfferings);
            }
        });
    }

    //calls methods needed based on queryType
    public void queryPosts(String queryType, ParentProfile parentProfile) {
        if (queryType.equals(KEY_BOUGHT)) {
            queryPostsUserBought(parentProfile);
        } else if (queryType.equals(KEY_SELLING)) {
            queryPostsUserIsSelling(false, parentProfile);
        } else if (queryType.equals(KEY_SOLD)) {
           queryPostsUserIsSelling(true, parentProfile);
        }
    }

    //find money raised for a specified user
    public void queryMoneyRaised(final ParseUser currentUser, final TextView tvMoneyRaised, final ImageView ivLevelIcon, final Context context, final ImageView ivCharityIcon) {
        final Integer[] moneyRaised = {0};
        final Integer[] moneySold = {0};

        final HashMap<Charity, Integer> moneyRaisedByCharity = new HashMap<>();

        queryAllPosts(new FindCallback<Offering>() {
            @Override
            public void done(List<Offering> objects, ParseException e) {
                for (Offering offering : objects) {
                    ArrayList<Object> boughtUsers = offering.getBoughtByArray();
                    if (boughtUsers != null && !boughtUsers.isEmpty()) {
                        for (Object object : boughtUsers) {
                            ParseUser user = (ParseUser) object;
                            if (user.getObjectId().equals(currentUser.getObjectId())) {
                                moneyRaised[0] += offering.getPrice();
                                Charity charity = offering.getCharity();
                                if (moneyRaisedByCharity.containsKey(charity)) {
                                    moneyRaisedByCharity.put(charity, moneyRaisedByCharity.get(charity) + offering.getPrice());
                                } else {
                                    moneyRaisedByCharity.put(charity, offering.getPrice());
                                }
                            }
                        }
                        if (offering.getUser().getObjectId().equals(currentUser.getObjectId())) {
                            moneySold[0] += offering.getPrice() * boughtUsers.size();
                            Charity charity = offering.getCharity();
                            if (moneyRaisedByCharity.containsKey(charity)) {
                                moneyRaisedByCharity.put(charity, moneyRaisedByCharity.get(charity) + offering.getPrice() * boughtUsers.size());
                            } else {
                                moneyRaisedByCharity.put(charity, offering.getPrice() * boughtUsers.size());
                            }
                        }
                    }
                }
                Integer totalMoney = moneyRaised[0] + moneySold[0];
                tvMoneyRaised.setText("$" + totalMoney.toString());

                int iconImage = -1;

                if (totalMoney < 100) {
                    iconImage = R.drawable.level_one;
                } else if (totalMoney < 200){
                    iconImage = R.drawable.level_two;
                } else if (totalMoney < 300){
                    iconImage = R.drawable.level_three;
                } else if (totalMoney < 400){
                    iconImage = R.drawable.level_four;
                } else if (totalMoney < 500){
                    iconImage = R.drawable.level_five;
                } else {
                    iconImage = R.drawable.crown;
                }

                if (totalMoney < 25) {
                    ivLevelIcon.setVisibility(View.INVISIBLE);
                } else {
                    Glide.with(context)
                            .load(iconImage)
                            .circleCrop()
                            .into(ivLevelIcon);
                }

                HashMap<Charity, Integer> sortedMap = sortMapByPoints(moneyRaisedByCharity);
                Set<Charity> charities = sortedMap.keySet();
                for (Charity charity : charities) {
                    Charity fetchedCharity = null;
                    try {
                        fetchedCharity = charity.fetchIfNeeded();
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    Glide.with(context)
                            .load(fetchedCharity.getImage().getUrl())
                            .into(ivCharityIcon);
                    return;
                }
            }
        });
    }

    public HashMap<Charity, Integer> sortMapByPoints(Map<Charity, Integer> pointValues) {
        // Create a list from elements of HashMap
        List<Map.Entry<Charity, Integer> > list = new LinkedList<Map.Entry<Charity, Integer> >(pointValues.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Charity, Integer>>() {
            public int compare(Map.Entry<Charity, Integer> o1, Map.Entry<Charity, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Charity, Integer> temp = new LinkedHashMap<Charity, Integer>();
        for (Map.Entry<Charity, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    //finds all notifications where the seller hasn't yet acted
    public void queryNotificationsForSeller(FindCallback<Notification> callback) {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        query.whereEqualTo("userActed", false);
        query.include("byUser");
        query.include("forOffering");
        query.findInBackground(callback);
    }

    //finds all notifications for the specified buying user
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
