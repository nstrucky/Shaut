package com.ventoray.shaut.client_data;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.ventoray.shaut.ShautApplication;

/**
 * Created by Nick on 2/17/2018.
 */

public class JobUtils {

    public static final String FRIEND_REQUEST_PULL_JOB_TAG = "friendRequestPullJobTag";
    private static boolean initializedFriendRequestPull;

    private static final int REQUEST_PULL_INTERVAL_SECONDS = 30;
    private static final int SYCN_FLEXTIME_SECONDS = 5;


    synchronized public static FirebaseJobDispatcher scheduleFriendRequestPull(@NonNull final Activity contxt) {
        if (initializedFriendRequestPull) return null;
        FirebaseJobDispatcher dispatcher =
                ((ShautApplication) contxt.getApplication()).getFirebaseJobDispatcher();

        Job constraintFriendRequestJob = dispatcher.newJobBuilder()
                .setService(FriendRequestPullService.class)
                .setTag(FRIEND_REQUEST_PULL_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(REQUEST_PULL_INTERVAL_SECONDS,
                        REQUEST_PULL_INTERVAL_SECONDS + SYCN_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(constraintFriendRequestJob);
        initializedFriendRequestPull = true;

        return dispatcher;
    }
}
