package com.ventoray.shaut.model;

import com.google.firebase.database.Exclude;
import com.ventoray.shaut.firebase.FirebaseContract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 2/4/2018.
 */

public class User implements FirebaseContract.FirebaseMapObject, Serializable {

    /**
     *  These are the key names for the variables to be stored in Firebase
     */
    public static final String USER_KEY = "userKey";
    public static final String USER_NAME = "userName";
    public static final String USER_EMAIL_ADDRESS = "userEmailAddress";
    public static final String CITY_KEY = "cityKey";
    public static final String CITY_NAME = "cityName";
    public static final String PROFILE_SUMMARY = "profileSummary";
    public static final String PROFILE_IMAGE_URL = "profileImageUrl";
    public static final String MOVED_TO_CITY_DATE = "movedToCityDate";


    private String userKey;
    private String userName;
    private String userEmailAddress;
    private String cityKey;
    private String cityName;
    private String profileSummary;
    private String profileImageUrl;
    private long movedToCityDate;

    public User() {}

    public User(String userKey, String userName, String cityKey, String cityName, String profileSummary, String profileImageUrl, long movedToCityDate) {
        this.userKey = userKey;
        this.userName = userName;
        this.cityKey = cityKey; //placeId (places api)
        this.cityName = cityName;
        this.profileSummary = profileSummary;
        this.profileImageUrl = profileImageUrl;
        this.movedToCityDate = movedToCityDate;
    }

    public long getMovedToCityDate() {
        return movedToCityDate;
    }

    public void setMovedToCityDate(long movedToCityDate) {
        this.movedToCityDate = movedToCityDate;
    }

    public String getUserEmailAddress() {
        return userEmailAddress;
    }

    public void setUserEmailAddress(String userEmailAddress) {
        this.userEmailAddress = userEmailAddress;
    }

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
    @Override
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put(USER_KEY, userKey);
        result.put(USER_NAME, userName);
        result.put(USER_EMAIL_ADDRESS, userEmailAddress);
        result.put(CITY_KEY, cityKey);
        result.put(CITY_NAME, cityName);
        result.put(PROFILE_SUMMARY, profileSummary);
        result.put(PROFILE_IMAGE_URL, profileImageUrl);
        result.put(MOVED_TO_CITY_DATE, movedToCityDate);

        return result;
    }
}
