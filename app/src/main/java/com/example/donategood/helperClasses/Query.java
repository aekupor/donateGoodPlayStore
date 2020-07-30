package com.example.donategood.helperClasses;

import android.view.View;
import android.widget.RatingBar;

import com.example.donategood.models.Charity;
import com.example.donategood.models.Comment;
import com.example.donategood.models.Message;
import com.example.donategood.models.Notification;
import com.example.donategood.models.Offering;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Query {

    public static final String KEY_BOUGHT = "bought";
    public static final String KEY_SELLING = "selling";
    public static final String KEY_SOLD = "sold";
    private List<Offering> savedBoughtPostsForUser;
    private List<Offering> savedSellingPostsForUser;
    private List<Offering> savedSoldPostsForUser;
    private List<Offering> savedSellingPostsForCharity;
    private List<Offering> savedSoldPostsForCharity;
    public  HashMap<Charity, Integer> sortedMapMoneyRaisedByCharity;
    public HashMap<String, Integer> moneyRaisedForPersonByCharity;
    public HashMap<String, Integer> moneyRaisedForCharityByPerson;

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
        query.include("user");
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
        query.include("image");
        query.findInBackground(callback);
    }

    //find all comments relating to an offering
    public void queryComments(Offering offering, FindCallback<Comment> callback) {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.whereEqualTo("forPost", offering);
        query.findInBackground(callback);
    }

    //find all users
    public void findAllUsers(FindCallback<ParseUser> callback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(callback);
    }

    //find a specific user by their name
    public void findUser(String userName, FindCallback<ParseUser> callback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", userName);
        query.findInBackground(callback);
    }

    //find a specific user by their id
    public void findUserById(String id, FindCallback<ParseUser> callback) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", id);
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
    public void queryPostsUserIsSelling(final Boolean bought, final ParentProfile parentProfile) {
        if (!bought && savedSellingPostsForUser != null) {
            updateAdapter(parentProfile, savedSellingPostsForUser);
            return;
        } else if (bought && savedSoldPostsForUser != null) {
            updateAdapter(parentProfile, savedSoldPostsForUser);
            return;
        }
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
                if (bought) {
                    savedSoldPostsForUser = objects;
                } else {
                    savedSellingPostsForUser = objects;
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
        parentProfile.pb.setVisibility(View.INVISIBLE);
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
    public void setCharityPosts(final Boolean selling, final ParentProfile parentProfile) {
        parentProfile.pb.setVisibility(View.VISIBLE);
        if (selling && savedSellingPostsForCharity != null) {
            updateAdapter(parentProfile, savedSellingPostsForCharity);
            return;
        } else if (!selling && savedSoldPostsForCharity != null) {
            updateAdapter(parentProfile, savedSoldPostsForCharity);
            return;
        }
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
                if (!selling) {
                    savedSoldPostsForCharity = objects;
                } else {
                    savedSellingPostsForCharity = objects;
                }
                updateAdapter(parentProfile, objects);
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
            return;
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

    //query chats with the specified roomId
    public void queryAllChatsByRoomId(String roomId, FindCallback<Message> callback) {
        final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.whereEqualTo("roomId", roomId);
        query.orderByDescending("createdAt");
        query.findInBackground(callback);
    }

    //query all chats
    public void queryAllChats(FindCallback<Message> callback) {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.orderByDescending("createdAt");
        query.findInBackground(callback);
    }

    public HashMap<String, Integer> getMoneyRaisedForPersonByCharity() {
        return moneyRaisedForPersonByCharity;
    }

    public HashMap<String, Integer> getMoneyRaisedForCharityByPerson() {
        return moneyRaisedForCharityByPerson;
    }
}
