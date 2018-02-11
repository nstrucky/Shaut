package com.ventoray.shaut.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ventoray.shaut.model.FriendRequest;
import com.ventoray.shaut.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 2/5/2018.
 */

public class Write {


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

        db.collection(FirebaseContract.UsersCollection.NAME)
                .document(potentialFriendKey)
                .collection(FirebaseContract.UsersCollection.User.StrangersRequestCollection.NAME)
                .document(userObject.getUserKey())
                .set(friendRequest).addOnSuccessListener(successListener);
    }

    /**
     * Allows the user to write an object to any path given the node string args
     * @param object - object to be saved to database
     * @param listener - listener called when setValue completes
     * @param pathNodes - String args for each node of path
     */
    public static void writeObject(Object object,
                                   @NonNull OnCompleteListener listener, String... pathNodes) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        for (String node : pathNodes) {
            reference = reference.child(node);
        }
        reference.setValue(object).addOnCompleteListener(listener);
    }


    /**TODO - 2/7/2018 - When a user is added to a city, they should be removed from the old one
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

}
