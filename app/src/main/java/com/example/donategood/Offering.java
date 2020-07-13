package com.example.donategood;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Offering")
public class Offering extends ParseObject {

    public static final String KEY_TITLE = "title";
    public static final String KEY_PRICE = "price";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public Integer getPrice() {
        return getInt(KEY_PRICE);
    }

    public void setPrice(Integer price) {
        put(KEY_PRICE, price);
    }
}