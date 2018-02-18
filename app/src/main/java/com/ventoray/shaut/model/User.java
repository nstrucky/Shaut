package com.ventoray.shaut.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.ventoray.shaut.firebase.FirebaseContract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.FIELD_CITY_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.FIELD_CITY_NAME;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.FIELD_MOVED_TO_CITY_DATE;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.FIELD_PROFILE_IMAGE_URL;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.FIELD_PROFILE_SUMMARY;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.FIELD_USER_EMAIL_ADDRESS;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.FIELD_USER_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.FIELD_USER_NAME;

/**
 * Created by Nick on 2/4/2018.
 */

public class User implements FirebaseContract.FirebaseMapObject, Serializable, Parcelable {

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

        result.put(FIELD_USER_KEY, userKey);
        result.put(FIELD_USER_NAME, userName);
        result.put(FIELD_USER_EMAIL_ADDRESS, userEmailAddress);
        result.put(FIELD_CITY_KEY, cityKey);
        result.put(FIELD_CITY_NAME, cityName);
        result.put(FIELD_PROFILE_SUMMARY, profileSummary);
        result.put(FIELD_PROFILE_IMAGE_URL, profileImageUrl);
        result.put(FIELD_MOVED_TO_CITY_DATE, movedToCityDate);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userKey);
        dest.writeString(this.userName);
        dest.writeString(this.userEmailAddress);
        dest.writeString(this.cityKey);
        dest.writeString(this.cityName);
        dest.writeString(this.profileSummary);
        dest.writeString(this.profileImageUrl);
        dest.writeLong(this.movedToCityDate);
    }

    protected User(Parcel in) {
        this.userKey = in.readString();
        this.userName = in.readString();
        this.userEmailAddress = in.readString();
        this.cityKey = in.readString();
        this.cityName = in.readString();
        this.profileSummary = in.readString();
        this.profileImageUrl = in.readString();
        this.movedToCityDate = in.readLong();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
