package com.ming.androblog.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    public static final String REF_USER = "users";
    public static final String COL_USER_ID = "userId";
    public static final String COL_USER_IMAGE = "imageUrl";
    public static final String COL_USER_NAME = "userName";
    private String userId;
    private String userName;
    private String imageUrl;

    public User() {
    }

    public User(String userId, String userName, String imageUrl) {
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("imageUrl", imageUrl);
        data.put("userId", userId);
        data.put("userName", userName);
        return data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
