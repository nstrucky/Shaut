package com.ventoray.shaut.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 2/4/2018.
 */

public class User {

    /**
     *  These are the key names for the variables to be stored in Firebase
     */
    public static final String USER_KEY = "userKey";
    public static final String USER_NAME = "userName";
    public static final String CITY_KEY = "cityKey";
    public static final String CITY_NAME = "cityName";
    public static final String PROFILE_SUMMARY = "profileSummary";
    public static final String PROFILE_IMAGE_URL = "profileImageUrl";


    private String userKey;
    private String userName;
    private String cityKey;
    private String cityName;
    private String profileSummary;
    private String profileImageUrl;

    public User() {}


    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getCityKey() {
        return cityKey;
    }

    public void setCityKey(String cityKey) {
        this.cityKey = cityKey;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileSummary() {
        return profileSummary;
    }

    public void setProfileSummary(String profileSummary) {
        this.profileSummary = profileSummary;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * Method used for updating children in firebase using mDatabase.updateChildren(childUpdates)
     * @return
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(USER_KEY, userKey);
        result.put(USER_NAME, userName);
        result.put(CITY_KEY, cityKey);
        result.put(CITY_NAME, cityName);
        result.put(PROFILE_SUMMARY, profileSummary);
        result.put(PROFILE_IMAGE_URL, profileImageUrl);

        return result;
    }
}
