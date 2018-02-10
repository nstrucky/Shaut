package com.ventoray.shaut.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Nick on 2/5/2018.
 */

public class Util {

    public static final String LOG_TAG = "Util";

    /**
     * Callback used in checkNodeExists method returns boolean.
     */
    public interface NodeExistsCheckCallback {
        void onUserNodeChecked(boolean exists);
    }


    public static void checkFireStoreDocumentExists() {



    }


    /**
     * Checks if path in firebase database exists and returns boolean to callback
     * @param callback - callback which returns boolean result
     * @param pathNodes - String argument for every node in the path to be verified
     */
    public static void checkNodeExists(final NodeExistsCheckCallback callback,
                                       String... pathNodes) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        for (String node : pathNodes) {
            reference = reference.child(node);
        }

        Log.d(LOG_TAG, reference.toString());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                callback.onUserNodeChecked(dataSnapshot.exists());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(LOG_TAG, databaseError.getMessage());
            }
        });
    }
}
