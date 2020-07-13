package com.example.donategood.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Charity")
public class Charity extends ParseObject {

    public static final String KEY_TITLE = "title";
    public static final String KEY_WEBSITE = "website";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getWebsite() {
        return getString(KEY_WEBSITE);
    }

    public void setWebsite(String website) {
        put(KEY_WEBSITE, website);
    }
}