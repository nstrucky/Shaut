package com.ventoray.shaut.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.ventoray.shaut.firebase.FirebaseContract;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.ventoray.shaut.firebase.FirebaseContract.ShautsCollection.Shauts.FIELD_CITY_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.ShautsCollection.Shauts.FIELD_DOWN_VOTE;
import static com.ventoray.shaut.firebase.FirebaseContract.ShautsCollection.Shauts.FIELD_MESSAGE_TEXT;
import static com.ventoray.shaut.firebase.FirebaseContract.ShautsCollection.Shauts.FIELD_MESSAGE_TIME;
import static com.ventoray.shaut.firebase.FirebaseContract.ShautsCollection.Shauts.FIELD_PROFILE_IMAGE_URL;
import static com.ventoray.shaut.firebase.FirebaseContract.ShautsCollection.Shauts.FIELD_UP_VOTE;
import static com.ventoray.shaut.firebase.FirebaseContract.ShautsCollection.Shauts.FIELD_USER_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.ShautsCollection.Shauts.FIELD_USER_NAME;

/**
 * Created by Nick on 2/4/2018.
 */

public class Shaut implements FirebaseContract.FirebaseMapObject, Parcelable {

    private String userName;
    private String userKey;
    private String cityKey; //placeId (places api)
    private String profileImageUrl;
    private String messageText;
    private long messageTime;
    private int upVote;
    private int downVote;

    public Shaut() {}

    public Shaut(String userName, String userKey, String cityKey, String profileImageUrl, String messageText, long messageTime, int upVote, int downVote) {
        this.userName = userName;
        this.userKey = userKey;
        this.cityKey = cityKey;
        this.profileImageUrl = profileImageUrl;
        this.messageText = messageText;
        this.messageTime = messageTime;
        this.upVote = upVote;
        this.downVote = downVote;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getCityKey() {
        return cityKey;
    }

    public void setCityKey(String cityKey) {
        this.cityKey = cityKey;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public int getUpVote() {
        return upVote;
    }

    public void setUpVote(int upVote) {
        this.upVote = upVote;
    }

    public int getDownVote() {
        return downVote;
    }

    public void setDownVote(int downVote) {
        this.downVote = downVote;
    }

    /**
     * Method used for updating children in firebase using mDatabase.updateChildren(childUpdates)
     * @return
     */
    @Override
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FIELD_USER_NAME, userName);
        result.put(FIELD_USER_KEY, userKey);
        result.put(FIELD_CITY_KEY, cityKey);
        result.put(FIELD_PROFILE_IMAGE_URL, profileImageUrl);
        result.put(FIELD_MESSAGE_TEXT, messageText);
        result.put(FIELD_MESSAGE_TIME, messageTime);
        result.put(FIELD_UP_VOTE, upVote);
        result.put(FIELD_DOWN_VOTE, downVote);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userName);
        dest.writeString(this.userKey);
        dest.writeString(this.cityKey);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.messageText);
        dest.writeLong(this.messageTime);
        dest.writeInt(this.upVote);
        dest.writeInt(this.downVote);
    }

    protected Shaut(Parcel in) {
        this.userName = in.readString();
        this.userKey = in.readString();
        this.cityKey = in.readString();
        this.profileImageUrl = in.readString();
        this.messageText = in.readString();
        this.messageTime = in.readLong();
        this.upVote = in.readInt();
        this.downVote = in.readInt();
    }

    public static final Parcelable.Creator<Shaut> CREATOR = new Parcelable.Creator<Shaut>() {
        @Override
        public Shaut createFromParcel(Parcel source) {
            return new Shaut(source);
        }

        @Override
        public Shaut[] newArray(int size) {
            return new Shaut[size];
        }
    };
}
