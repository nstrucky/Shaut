package com.ventoray.shaut.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.ventoray.shaut.R;
import com.ventoray.shaut.ui.MainActivity;
import com.ventoray.shaut.ui.PreSignInActivity;
import com.ventoray.shaut.ui.ProfileEditorActivity;


public class NetworkStateReceiver extends BroadcastReceiver {
    Snackbar snackbar = null;

    @Override
    public void onReceive(Context context, Intent intent) {


        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        Log.d("app", "Network connectivity change");
        if (intent.getExtras() != null) {
            if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                if (snackbar != null) {
                    snackbar.dismiss();
                }

                if (context instanceof ProfileEditorActivity) {
                    ProfileEditorActivity productActivity = (ProfileEditorActivity) context;
                    productActivity.findViewById(R.id.button_camera).setEnabled(true);
                }

                Log.i("app", "Network " + networkInfo.getTypeName() + " connected");
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                handleActivityResponse(context);


                Log.d("app", "There's no network connectivity");
            }
        }
    }

    private void handleActivityResponse(Context context) {

        if (context instanceof MainActivity) {
            doMainActivityThings(context);

        } else if (context instanceof PreSignInActivity) {
            doPreSignInActivityThings(context);
        }

        if (context instanceof ProfileEditorActivity) {
            doProfileEditorActivityThings(context);
        }

    }

    private void doProfileEditorActivityThings(Context context) {
        ProfileEditorActivity productActivity = (ProfileEditorActivity) context;
        productActivity.findViewById(R.id.button_camera).setEnabled(false);
    }

    private void doMainActivityThings(Context context) {
        snackbar = Snackbar.make(
                ((MainActivity) context).findViewById(R.id.container_app_bar),
                R.string.connectivity_lost,
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.show();
    }

    private void doPreSignInActivityThings(Context context) {
            snackbar = Snackbar.make(
                    ((PreSignInActivity) context).findViewById(R.id.container_presignin),
                    R.string.connectivity_lost,
                    Snackbar.LENGTH_INDEFINITE
            );
            snackbar.show();
    }

}