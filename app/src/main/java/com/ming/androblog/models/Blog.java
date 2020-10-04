package com.ming.androblog.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Blog implements Parcelable {
    public static final String REF_BLOG = "blogs";
    public static final String KEY_USER_ID = "userId";
    private String blogId;
    private String imageUrl;
    private String detail;
    private String userId;
    private String username;
    private String dateCreated;

    @Override
    public String toString() {
        return "Blog{" +
                "blogId='" + blogId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", detail='" + detail + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                '}';
    }

    public Blog() {
    }

    public Blog(String blogId, String imageUrl, String detail, String username, String userId, String dateCreated) {
        this.blogId = blogId;
        this.imageUrl = imageUrl;
        this.detail = detail;
        this.username = username;
        this.userId = userId;
        this.dateCreated = dateCreated;
    }

    protected Blog(Parcel in) {
        blogId = in.readString();
        imageUrl = in.readString();
        detail = in.readString();
        userId = in.readString();
        username = in.readString();
        dateCreated = in.readString();
    }

    public static final Creator<Blog> CREATOR = new Creator<Blog>() {
        @Override
        public Blog createFromParcel(Parcel in) {
            return new Blog(in);
        }

        @Override
        public Blog[] newArray(int size) {
            return new Blog[size];
        }
    };

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("blogId", blogId);
        data.put("imageUrl", imageUrl);
        data.put("username", username);
        data.put("userId", userId);
        data.put("detail", detail);
        data.put("dateCreated", dateCreated);
        return data;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(blogId);
        parcel.writeString(imageUrl);
        parcel.writeString(detail);
        parcel.writeString(userId);
        parcel.writeString(username);
        parcel.writeString(dateCreated);
    }
}
