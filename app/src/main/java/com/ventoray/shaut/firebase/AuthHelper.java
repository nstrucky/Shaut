package com.ventoray.shaut.firebase;

import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.ventoray.shaut.R;

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
}
