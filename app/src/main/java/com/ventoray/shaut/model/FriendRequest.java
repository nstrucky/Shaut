package com.ventoray.shaut.model;

import java.util.HashMap;
import java.util.Map;

import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.StrangersRequestCollection.Request.FIELD_CITY_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.StrangersRequestCollection.Request.FIELD_CITY_NAME;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.StrangersRequestCollection.Request.FIELD_POTENTIAL_FRIEND_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.StrangersRequestCollection.Request.FIELD_REQUESTER_IMAGE_URL;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.StrangersRequestCollection.Request.FIELD_REQUESTER_PROFILE_CONTENT;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.StrangersRequestCollection.Request.FIELD_REQUESTER_USER_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.StrangersRequestCollection.Request.FIELD_REQUESTER_USER_NAME;

/**
 * Created by Nick on 2/10/2018.
 */

public class FriendRequest {


    private String requesterUserKey;
    private String requesterUserName;
    private String requesterImageUrl;
    private String requesterProfileContent;
    private String potentialFriendKey;
    private String cityKey;
    private String cityName;


    public FriendRequest() {}

    public FriendRequest(String requesterUserKey, String requesterUserName,
                         String requesterImageUrl, String requesterProfileContent,
                         String potentialFriendKey, String cityKey, String cityName) {
        this.requesterUserKey = requesterUserKey;
        this.requesterUserName = requesterUserName;
        this.potentialFriendKey = potentialFriendKey;
        this.requesterProfileContent = requesterProfileContent;
        this.cityKey = cityKey;
        this.cityName = cityName;
        this.requesterImageUrl = requesterImageUrl;
    }

    public String getRequesterUserKey() {
        return requesterUserKey;
    }

    public void setRequesterUserKey(String requesterUserKey) {
        this.requesterUserKey = requesterUserKey;
    }

    public String getRequesterUserName() {
        return requesterUserName;
    }

    public void setRequesterUserName(String requesterUserName) {
        this.requesterUserName = requesterUserName;
    }

    public String getRequesterImageUrl() {
        return requesterImageUrl;
    }

    public void setRequesterImageUrl(String requesterImageUrl) {
        this.requesterImageUrl = requesterImageUrl;
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

    public String getPotentialFriendKey() {
        return potentialFriendKey;
    }

    public void setPotentialFriendKey(String potentialFriendKey) {
        this.potentialFriendKey = potentialFriendKey;
    }

    public String getRequesterProfileContent() {
        return requesterProfileContent;
    }

    public void setRequesterProfileContent(String requesterProfileContent) {
        this.requesterProfileContent = requesterProfileContent;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put(FIELD_REQUESTER_USER_KEY, requesterUserKey);
        result.put(FIELD_REQUESTER_USER_NAME, requesterUserName);
        result.put(FIELD_REQUESTER_IMAGE_URL, requesterImageUrl);
        result.put(FIELD_REQUESTER_PROFILE_CONTENT, requesterProfileContent);
        result.put(FIELD_POTENTIAL_FRIEND_KEY, potentialFriendKey);
        result.put(FIELD_CITY_KEY, cityKey);
        result.put(FIELD_CITY_NAME, cityName);

        return result;
    }
}
