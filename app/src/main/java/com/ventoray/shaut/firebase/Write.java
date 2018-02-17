package com.ventoray.shaut.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.ventoray.shaut.model.ChatMessage;
import com.ventoray.shaut.model.ChatMetaData;
import com.ventoray.shaut.model.FriendRequest;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.widget.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_USER_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.CONTENT_URI;

/**
 * Created by Nick on 2/5/2018.
 */

public class Write {

    public static final String LOG_TAG = "Write";

    /**
     * Writes the friend request to the potential friend's strangers requests collection
     * @param friendRequest
     * @param userObject
     * @param db
     * @param successListener
     */
    public static void sendFriendRequest(FriendRequest friendRequest, User userObject,
                                         FirebaseFirestore db, OnSuccessListener successListener) {
        String potentialFriendKey = friendRequest.getPotentialFriendKey();

        friendRequest.setRequesterImageUrl(userObject.getProfileImageUrl());
        friendRequest.setRequesterUserKey(userObject.getUserKey());
        friendRequest.setRequesterUserName(userObject.getUserName());
        friendRequest.setRequesterProfileContent(userObject.getProfileSummary());

        db.collection(FirebaseContract.UsersCollection.NAME)
                .document(potentialFriendKey)
                .collection(FirebaseContract.UsersCollection.User.StrangersRequestCollection.NAME)
                .document(userObject.getUserKey())
                .set(friendRequest).addOnSuccessListener(successListener);
    }

    /** deprecated --moved to firestore
     * Allows the user to write an object to any path given the node string args
     * @param object - object to be saved to database
     * @param listener - listener called when setValue completes
     * @param pathNodes - String args for each node of path
     */
    @Deprecated
    public static void writeObject(Object object,
                                   @NonNull OnCompleteListener listener, String... pathNodes) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        for (String node : pathNodes) {
            reference = reference.child(node);
        }
        reference.setValue(object).addOnCompleteListener(listener);
    }


    /**
     * This method will update the Users Object with the new city name and ID (filter type cities
     * from the autocomplete api).  The Cities node for that city will also be updated with a
     * key of the user's id with value equal to true.
     *
     * Note that the user object passed to this method should contain all of the updated city
     * information.
     * @param userObject
     * @param listener
     */

    public static void updateUserCity(User userObject,
                                      String oldCityId,
                                      OnCompleteListener listener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        String userId = userObject.getUserKey();
        String newCityId = userObject.getCityKey();
        Map<String, Object> userValues = userObject.toMap();
        childUpdates.put(FirebaseContract.UsersCollection.NAME + "/" +
                userId + "/" +
                FirebaseContract.UsersCollection.User.NAME,
                userValues);
        childUpdates.put(FirebaseContract.CitiesNode.COLLECTION + "/" +
                        newCityId + "/" +
                        FirebaseContract.CitiesNode.City.USERS_NODE + "/" +
                        userId,
                true);

        databaseReference.updateChildren(childUpdates).addOnCompleteListener(listener);

        if (oldCityId != null && !oldCityId.isEmpty()) {
            databaseReference.child(FirebaseContract.CitiesNode.COLLECTION)
                    .child(oldCityId)
                    .child(FirebaseContract.CitiesNode.City.USERS_NODE)
                    .child(userId)
                    .removeValue();
        }
    }

    /**
     * Deletes any pending friend requests taking just the context and requester's user ID.
     * This will trigger deletion from the sqlite database only once the success listener from
     * firebase returns successful.
     * @param context
     * @param requesterId
     */
    public static void deleteFriendRequest(final Context context, final String requesterId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection(FirebaseContract.UsersCollection.NAME)
                .document(userId)
                .collection(FirebaseContract.UsersCollection.User.StrangersRequestCollection.NAME)
                .document(requesterId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        int deleted = context.getContentResolver().delete(CONTENT_URI,
                                COLUMN_REQUESTER_USER_KEY, new String[]{requesterId});
                        Log.d(LOG_TAG, "Deleted " + deleted + " records");

                        Utils.notifyAppWidget(context);
                    }
                });
    }

    /**
     * This method updates the users and new friend's chatmetadata collections with the same
     * metadata (reveresed - userName switched with friendName when saving to new friend's
     * collection).
     *
     * This method also saves an initial message from no one to the messages collection under
     * a generated chatroom ID.  This chatroom ID is used as the document name under each users
     * chatroom (metadata) collection.
     * @param friendRequest
     */
    public static void initializeFriendship(final FriendRequest friendRequest,
                                            final FirebaseFirestore db,
                                            final User userObject) {
        WriteBatch batch = db.batch();
        String chatroomId;
        String userKey = userObject.getUserKey();
        String userName = userObject.getUserName();
        String friendKey = friendRequest.getRequesterUserKey();
        String friendName = friendRequest.getRequesterUserName();
        long time = new Date().getTime();
        ChatMessage message = new ChatMessage("", "",
                "Welcome to Friendship!", time);

        //Create messages col/chatroomID doc/messages col/messages doc/message
        DocumentReference messagesRef = db
                .collection(FirebaseContract.MessagesCollection.NAME)
                .document();
        chatroomId = messagesRef.getId(); //create chatroom ID here and use it later

        messagesRef = messagesRef
                .collection(FirebaseContract.MessagesCollection.ChatMessagesCollection.NAME)
                .document();

        ChatMetaData userChatMetaData = new ChatMetaData(chatroomId, time, userKey, userName,
                friendKey, friendName, message.getMessageText());
        //Create users col/user doc/chatrooms col/chatmetadata doc/chatmetadata
        DocumentReference userChatMetaDataRef = db
                .collection(FirebaseContract.UsersCollection.NAME)
                .document(userKey)
                .collection(FirebaseContract.UsersCollection.User.ChatroomsCollection.NAME)
                .document(chatroomId);

        ChatMetaData friendChatMetaData = new ChatMetaData(chatroomId, time, friendKey, friendName,
                userKey, userName, message.getMessageText());
        //Create users col/user doc/chatrooms col/chatmetadata doc/chatmetadata
        DocumentReference friendChatMetaDataRef = db
                .collection(FirebaseContract.UsersCollection.NAME)
                .document(friendKey)
                .collection(FirebaseContract.UsersCollection.User.ChatroomsCollection.NAME)
                .document(chatroomId);

        //Set and commit batch update
        batch.set(messagesRef, message);
        batch.set(userChatMetaDataRef, userChatMetaData);
        batch.set(friendChatMetaDataRef, friendChatMetaData);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.d(LOG_TAG, task.getException().getMessage());
                } else {
                    db.collection(FirebaseContract.UsersCollection.NAME)
                            .document(userObject.getUserKey())
                            .collection(FirebaseContract.UsersCollection.User.StrangersRequestCollection.NAME)
                            .document(friendRequest.getRequesterUserKey())
                            .delete();
                }
            }
        });
    }


    public static void sendMessage(String message, FirebaseFirestore db, User userObject,
                                   ChatMetaData userChatMetaData) {
        WriteBatch batch = db.batch();
        ChatMetaData friendsChatMetaData;
        long time = new Date().getTime();

        String userName = userObject.getUserName();
        String userKey = userObject.getUserKey();
        String friendName = userChatMetaData.getFriendName();
        String friendKey = userChatMetaData.getFriendKey();
        String chatroomId = userChatMetaData.getChatroomId();

        ChatMessage chatMessage = new ChatMessage(userName, userKey, message, time);

        //update meta data
        userChatMetaData.setLastMessage("You: "+message);
        userChatMetaData.setTimeStamp(time);

        //create mirror chatmetadata for friend
        friendsChatMetaData = new ChatMetaData(chatroomId, time, friendKey, friendName, userKey,
                userName, message);

        //get messages col/chatroom doc/chatmessages col/chatmessage
        DocumentReference chagMessageRef = db
                .collection(FirebaseContract.MessagesCollection.NAME)
                .document(userChatMetaData.getChatroomId())
                .collection(FirebaseContract.MessagesCollection.ChatMessagesCollection.NAME)
                .document();

        //get users col/user doc/chatrooms col/chatmetadata doc/chatmetadata
        DocumentReference userChatMetaDataRef = db
                .collection(FirebaseContract.UsersCollection.NAME)
                .document(userKey)
                .collection(FirebaseContract.UsersCollection.User.ChatroomsCollection.NAME)
                .document(chatroomId);

        //get users col/user doc/chatrooms col/chatmetadata doc/chatmetadata
        DocumentReference friendChatMetaDataRef = db
                .collection(FirebaseContract.UsersCollection.NAME)
                .document(friendKey)
                .collection(FirebaseContract.UsersCollection.User.ChatroomsCollection.NAME)
                .document(chatroomId);


        //set all info and commit
        batch.set(chagMessageRef, chatMessage);
        batch.set(userChatMetaDataRef, userChatMetaData);
        batch.set(friendChatMetaDataRef, friendsChatMetaData);
        batch.commit();


    }




}
