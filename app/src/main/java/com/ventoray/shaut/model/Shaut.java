package com.ventoray.shaut.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 2/4/2018.
 */

public class Shaut {

    /**
     *  These are the key names for the variables to be stored in Firebase
     */
    public static final String USER_NAME = "userName";
    public static final String USER_KEY = "userKey";
    public static final String PROFILE_IMAGE_URL = "profileImageUrl";
    public static final String MESSAGE_TEXT = "messageText";
    public static final String MESSAGE_TIME = "messageTime";
    public static final String UP_VOTE = "upVote";
    public static final String DOWN_VOTE = "downVote";

    private String userName;
    private String userKey;
    private String profileImageUrl;
    private String messageText;
    private long messageTime;
    private int upVote;
    private int downVote;

    public Shaut() {}

    public Shaut(String userName, String userKey, String profileImageUrl, String messageText, long messageTime, int upVote, int downVote) {
        this.userName = userName;
        this.userKey = userKey;
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
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(USER_NAME, userName);
        result.put(USER_KEY, userKey);
        result.put(PROFILE_IMAGE_URL, profileImageUrl);
        result.put(MESSAGE_TEXT, messageText);
        result.put(MESSAGE_TIME, messageTime);
        result.put(UP_VOTE, upVote);
        result.put(DOWN_VOTE, downVote);

        return result;
    }

}
