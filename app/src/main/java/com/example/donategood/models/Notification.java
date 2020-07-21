package com.example.donategood.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Notification")
public class Notification extends ParseObject {

    public static final String KEY_USER = "byUser";
    public static final String KEY_OFFERING = "forOffering";
    public static final String KEY_APPROVED = "approved";

}
