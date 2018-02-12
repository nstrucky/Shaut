package com.ventoray.shaut.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_CHATROOM_ID;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_FRIEND_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_FRIEND_NAME;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_LAST_MESSAGE;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_TIMESTAMP;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_USER_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.ChatroomsCollection.ChatMetaData.FIELD_USER_NAME;

/**
 * Created by Nick on 2/11/2018.
 */

public class ChatMetaData implements Parcelable {

    private String chatroomId;
    private long timeStamp;
    private String userKey;
    private String userName;
    private String friendKey;
    private String friendName;
    private String lastMessage;

    public ChatMetaData() {}

    public ChatMetaData(String chatroomId, long timeStamp, String userKey, String userName,
                        String friendKey, String friendName, String lastMessage) {
        this.chatroomId = chatroomId;
        this.timeStamp = timeStamp;
        this.userKey = userKey;
        this.userName = userName;
        this.friendKey = friendKey;
        this.friendName = friendName;
        this.lastMessage = lastMessage;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
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
        result.put(FIELD_CHATROOM_ID, chatroomId);
        result.put(FIELD_TIMESTAMP, timeStamp);
        result.put(FIELD_USER_KEY, userKey);
        result.put(FIELD_USER_NAME, userName);
        result.put(FIELD_FRIEND_KEY, friendKey);
        result.put(FIELD_FRIEND_NAME, friendName);
        result.put(FIELD_LAST_MESSAGE, lastMessage);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.chatroomId);
        dest.writeLong(this.timeStamp);
        dest.writeString(this.userKey);
        dest.writeString(this.userName);
        dest.writeString(this.friendKey);
        dest.writeString(this.friendName);
        dest.writeString(this.lastMessage);
    }

    protected ChatMetaData(Parcel in) {
        this.chatroomId = in.readString();
        this.timeStamp = in.readLong();
        this.userKey = in.readString();
        this.userName = in.readString();
        this.friendKey = in.readString();
        this.friendName = in.readString();
        this.lastMessage = in.readString();
    }

    public static final Creator<ChatMetaData> CREATOR = new Creator<ChatMetaData>() {
        @Override
        public ChatMetaData createFromParcel(Parcel source) {
            return new ChatMetaData(source);
        }

        @Override
        public ChatMetaData[] newArray(int size) {
            return new ChatMetaData[size];
        }
    };
}
