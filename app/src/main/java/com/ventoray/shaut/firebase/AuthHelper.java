package com.ventoray.shaut.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.ventoray.shaut.R;
import com.ventoray.shaut.ui.PreSignInActivity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Nick on 2/5/2018.
 */

public class AuthHelper {

    public static final String LOG_TAG = "AuthHelper";


    /**
     * Signs in user and returns result in onActivityResult
     * @param activity
     * @param requestCode
     */
    public static void signIn(AppCompatActivity activity, int requestCode) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
        );

        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo_ventoray_512x512)
                        .build(),
                requestCode
        );
    }



    public static void signOut(final AppCompatActivity appCompatActivity) {
        FirebaseAuth.getInstance().signOut();

        AuthUI.getInstance()
                .signOut(appCompatActivity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(appCompatActivity, PreSignInActivity.class);
                        appCompatActivity.startActivity(intent);
                        appCompatActivity.finish();
                    }
                });
    }
}
