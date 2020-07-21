package com.example.donategood.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Notification")
public class Notification extends ParseObject {

    public static final String KEY_USER = "byUser";
    public static final String KEY_OFFERING = "forOffering";
    public static final String KEY_APPROVED = "approved";
    public static final String KEY_SELLING_USER = "sellingUser";

    public ParseUser getKeyUser() {
        return getParseUser(KEY_USER);
    }

    public void setKeyUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Offering getKeyOffering() {
        return (Offering) get(KEY_OFFERING);
    }

    public void setKeyOffering(Offering offering) {
        put(KEY_OFFERING, offering);
        put(KEY_SELLING_USER, offering.getUser());
    }

    public Boolean getKeyApproved() {
        return getBoolean(KEY_APPROVED);
    }

    public void setKeyApproved(Boolean approved) {
        put(KEY_APPROVED, approved);
    }
}
