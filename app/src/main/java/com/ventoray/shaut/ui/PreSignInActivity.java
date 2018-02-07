package com.ventoray.shaut.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ventoray.shaut.R;
import com.ventoray.shaut.firebase.AuthHelper;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.firebase.Util;
import com.ventoray.shaut.firebase.Write;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.util.FileHelper;

public class PreSignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;
    public static final String USER_SIGNED_IN_ALREADY_KEY =
            "com.ventoray.shaut.ui.USER_SIGNED_IN_ALREADY_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_sign_in);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            AuthHelper.signIn(this, RC_SIGN_IN);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(USER_SIGNED_IN_ALREADY_KEY, true);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Util.checkNodeExists(nodeExistsCheckCallback, FirebaseContract.UsersNode.NAME, uid);
            } else {
                Toast.makeText(this, "No dice", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Util.NodeExistsCheckCallback nodeExistsCheckCallback = new Util.NodeExistsCheckCallback() {
        @Override
        public void onUserNodeChecked(boolean exists) {
            if (exists) {
                goToMainActivity();
            } else {
                createNewUserNode();
            }
        }
    };


    /**
     * This method creates a new user profile by get the display name, email address, and user ID
     * from the given Auth method.  It also sets a default profile summary of "Hello World!".
     *
     * A User object is then also cached in a user file, name specified in the FileHelper class.
     */
    private void createNewUserNode() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userName = firebaseUser.getDisplayName();
        String userEmail = firebaseUser.getEmail();
        String uid = firebaseUser.getUid();

        User newUser = new User();
        newUser.setUserKey(uid);
        newUser.setProfileSummary(getString(R.string.hello_world));
        newUser.setUserName(userName);
        newUser.setUserEmailAddress(userEmail);

        FileHelper.writeObjectToFile(this, newUser, FileHelper.USER_OBJECT_FILE);

        Write.writeObject(newUser, onCompleteWriteUserObject,
                FirebaseContract.UsersNode.NAME,
                firebaseUser.getUid(),
                FirebaseContract.UsersNode.User.USER_OBJECT);
    }


    OnCompleteListener onCompleteWriteUserObject = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                goToMainActivity();
            } else {
                Toast.makeText(PreSignInActivity.this,
                        R.string.something_wrong, Toast.LENGTH_SHORT).show();
            }

        }
    };

    private void goToMainActivity() {
        Intent intent = new Intent(PreSignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
