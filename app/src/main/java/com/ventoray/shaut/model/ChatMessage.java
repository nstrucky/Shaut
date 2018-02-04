package com.ventoray.shaut.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 2/4/2018.
 */

public class ChatMessage {

    /**
     *  These are the key names for the variables to be stored in Firebase
     */
    public static final String USER_NAME = "userName";
    public static final String USER_KEY = "userKey";
    public static final String MESSAGE_TEXT = "messageText";
    public static final String MESSAGE_TIME = "messageTime";

    private String userName;
    private String userKey;
    private String messageText;
    private long messageTime;

    public ChatMessage() {}

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

    /**
     * Method used for updating children in firebase using mDatabase.updateChildren(childUpdates)
     * @return
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(USER_NAME, userName);
        result.put(USER_KEY, userKey);
        result.put(MESSAGE_TEXT, messageText);
        result.put(MESSAGE_TIME, messageTime);

        return result;
    }
}
