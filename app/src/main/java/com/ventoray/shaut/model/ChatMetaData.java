package com.ventoray.shaut.model;

import java.util.HashMap;
import java.util.Map;

import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_FRIEND_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_FRIEND_NAME;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_LAST_MESSAGE;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_TIMESTAMP;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_USER_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_USER_NAME;

/**
 * Created by Nick on 2/11/2018.
 */

public class ChatMetaData {

    private long timeStamp;
    private String userKey;
    private String userName;
    private String friendKey;
    private String friendName;
    private String lastMessage;

    public ChatMetaData() {}

    public ChatMetaData(long timeStamp, String userKey, String userName, String friendKey, String friendName, String lastMessage) {
        this.timeStamp = timeStamp;
        this.userKey = userKey;
        this.userName = userName;
        this.friendKey = friendKey;
        this.friendName = friendName;
        this.lastMessage = lastMessage;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFriendKey() {
        return friendKey;
    }

    public void setFriendKey(String friendKey) {
        this.friendKey = friendKey;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put(FIELD_TIMESTAMP, timeStamp);
        result.put(FIELD_USER_KEY, userKey);
        result.put(FIELD_USER_NAME, userName);
        result.put(FIELD_FRIEND_KEY, friendKey);
        result.put(FIELD_FRIEND_NAME, friendName);
        result.put(FIELD_LAST_MESSAGE, lastMessage);

        return result;
    }
}
