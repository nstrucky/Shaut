package com.ventoray.shaut;

import android.app.Application;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

public class ShautApplication extends Application {

    private Driver driver;
    private FirebaseJobDispatcher dispatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        driver = new GooglePlayDriver(getApplicationContext());
        dispatcher = new FirebaseJobDispatcher(driver);

    }

    public FirebaseJobDispatcher getFirebaseJobDispatcher() {
        return dispatcher;
    }
}
