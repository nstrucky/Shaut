package com.ventoray.shaut.client_data;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.model.FriendRequest;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.util.FileHelper;

import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_CITY_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_CITY_NAME;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_POTENTIAL_FRIEND_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_IMAGE_URL;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_PROFILE_CONTENT;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_USER_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_USER_NAME;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.CONTENT_URI;
import static com.ventoray.shaut.widget.Utils.notifyAppWidget;

/**
 * Created by Nick on 2/17/2018.
 */

public class DataTasks {

    public static final String LOG_TAG = "DataTask";

    public static final String ACTION_PULL_FRIEND_REQUESTS =
            "com.ventoray.shaut.client_data.DataTasks.ACTION_PULL_FRIEND_REQEUSTS";

    public static void executeTask(Context context, String task) {
        if (task.equals(ACTION_PULL_FRIEND_REQUESTS)) {
            int deleted =
                    context.getContentResolver().delete(CONTENT_URI, null, null);
            Log.d(LOG_TAG, "Task: " + task);
            Log.d(LOG_TAG, "Deleted in task: " + deleted);
            getFriendRequests(context);
        } else {
            Log.d(LOG_TAG, "Could not match task " + task);
        }
    }


    private static void getFriendRequests(final Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User userObject =
                (User) FileHelper.readObjectFromFile(context, FileHelper.USER_OBJECT_FILE);
        if (userObject == null) {
            Log.d(LOG_TAG, "No user object!");
            return;
        }
        Query friendRequestQuery = db.collection(FirebaseContract.UsersCollection.NAME)
                .document(userObject.getUserKey())
                .collection(FirebaseContract.UsersCollection.User.StrangersRequestCollection.NAME);

        friendRequestQuery.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots != null && documentSnapshots.size() > 0) {
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                FriendRequest request =
                                        documentSnapshot.toObject(FriendRequest.class);

                                saveRequestToClient(context, request);
                            }
                            notifyAppWidget(context);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(LOG_TAG, "Error downloading requests: " + e.getLocalizedMessage());
            }
        });
    }

    private static void saveRequestToClient(Context context, FriendRequest friendRequest) {

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_REQUESTER_USER_KEY, friendRequest.getRequesterUserKey());
        cv.put(COLUMN_REQUESTER_USER_NAME, friendRequest.getRequesterUserName());
        cv.put(COLUMN_REQUESTER_IMAGE_URL, friendRequest.getRequesterImageUrl());
        cv.put(COLUMN_REQUESTER_PROFILE_CONTENT, friendRequest.getRequesterProfileContent());
        cv.put(COLUMN_POTENTIAL_FRIEND_KEY, friendRequest.getPotentialFriendKey());
        cv.put(COLUMN_CITY_KEY, friendRequest.getCityKey());
        cv.put(COLUMN_CITY_NAME, friendRequest.getCityName());

        context.getContentResolver()
                .insert(CONTENT_URI, cv);
    }




}
