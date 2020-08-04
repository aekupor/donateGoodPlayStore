package com.example.donategood.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_ID_KEY = "userId";
    public static final String BODY_KEY = "body";
    public static final String ROOM_ID_KEY = "roomId";
    public static final String UNREAD_KEY = "unread";

    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public String getRoomId() {
        return getString(ROOM_ID_KEY);
    }

    public void setRoomID(String roomId) {
        put(ROOM_ID_KEY, roomId);
    }

    public Boolean getUnread() {
        return getBoolean(UNREAD_KEY);
    }

    public void setUnread(Boolean unread) {
        put(UNREAD_KEY, unread);
    }
}