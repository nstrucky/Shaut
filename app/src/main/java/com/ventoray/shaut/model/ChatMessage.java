package com.ventoray.shaut.model;

import com.google.firebase.database.Exclude;
import com.ventoray.shaut.firebase.FirebaseContract;

import java.util.HashMap;
import java.util.Map;

import static com.ventoray.shaut.firebase.FirebaseContract.MessagesCollection.ChatMessagesCollection.ChatMessage.FIELD_MESSAGE_TEXT;
import static com.ventoray.shaut.firebase.FirebaseContract.MessagesCollection.ChatMessagesCollection.ChatMessage.FIELD_MESSAGE_TIME;
import static com.ventoray.shaut.firebase.FirebaseContract.MessagesCollection.ChatMessagesCollection.ChatMessage.FIELD_USER_KEY;
import static com.ventoray.shaut.firebase.FirebaseContract.MessagesCollection.ChatMessagesCollection.ChatMessage.FIELD_USER_NAME;

/**
 * Created by Nick on 2/4/2018.
 */

public class ChatMessage implements FirebaseContract.FirebaseMapObject {

    /**
     *  These are the key names for the variables to be stored in Firebase
     */
    private String userName;
    private String userKey;
    private String messageText;
    private long messageTime;

    public ChatMessage() {}

    public ChatMessage(String userName, String userKey, String messageText, long messageTime) {
        this.userName = userName;
        this.userKey = userKey;
        this.messageText = messageText;
        this.messageTime = messageTime;
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

    /**
     * Method used for updating children in firebase using mDatabase.updateChildren(childUpdates)
     * @return
     */
    @Override
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FIELD_USER_NAME, userName);
        result.put(FIELD_USER_KEY, userKey);
        result.put(FIELD_MESSAGE_TEXT, messageText);
        result.put(FIELD_MESSAGE_TIME, messageTime);

        return result;
    }
}
