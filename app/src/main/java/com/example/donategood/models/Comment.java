package com.example.donategood.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_FOR_POST = "forPost";
    public static final String KEY_BY_USER = "byUser";
    public static final String KEY_TEXT = "commentText";
    public static final String KEY_RATING = "rating";

    public String getForPost() {
        return getString(KEY_FOR_POST);
    }

    public ParseUser getByUser() {
        return getParseUser(KEY_BY_USER);
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public Integer getRating() {
        return getInt(KEY_RATING);
    }

    public void setForPost(Offering offering) {
        put(KEY_FOR_POST, offering);
    }

    public void setByUser(ParseUser user) {
        put(KEY_BY_USER, user);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public void setRating(Integer rating) {
        put(KEY_RATING, rating);
    }
}
