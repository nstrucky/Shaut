package com.ventoray.shaut.firebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Nick on 2/5/2018.
 */

public class Write {

    /**
     * Allows the user to write an object to any path given the node string args
     * @param object - object to be saved to database
     * @param listener - listener called when setValue completes
     * @param pathNodes - String args for each node of path
     */
    public static void writeObject(Object object, OnCompleteListener listener, String... pathNodes) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        for (String node : pathNodes) {
            reference = reference.child(node);
        }
        reference.setValue(object).addOnCompleteListener(listener);
    }


}
