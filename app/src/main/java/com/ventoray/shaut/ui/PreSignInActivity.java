package com.ventoray.shaut.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ventoray.shaut.R;
import com.ventoray.shaut.firebase.AuthHelper;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.firebase.Util;
import com.ventoray.shaut.firebase.Write;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.util.FileHelper;

public class PreSignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;
    public static final String LOG_TAG = "PreSignInActivity";
    public static final String USER_SIGNED_IN_ALREADY_KEY =
            "com.ventoray.shaut.ui.USER_SIGNED_IN_ALREADY_KEY";

    private boolean onActivityResultCalled;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_sign_in);
        db = FirebaseFirestore.getInstance();

    }


    /**
     * The sign in logic is placed here because onResume seems to be called after
     * onActivityResult
     */
    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.d(LOG_TAG, "USER DEFINITELYE NULL");
            AuthHelper.signIn(this, RC_SIGN_IN);

        } else if (!onActivityResultCalled) {//this will be the case only if user already signed in
            String uid = user.getUid();
            Log.d(LOG_TAG, "USER NOT NULL : " + uid);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(USER_SIGNED_IN_ALREADY_KEY, true);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        onActivityResultCalled = true;
        Log.d(LOG_TAG, "onActivityResult!");
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Log.d(LOG_TAG, "User Id: " + uid);
                db.collection(FirebaseContract.UsersNode.NAME)
                        .document(uid)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                FileHelper.writeObjectToFile(PreSignInActivity.this,
                                        user, FileHelper.USER_OBJECT_FILE);
                                goToMainActivity();
                            } else {//document does not exist
                                createNewUserNode();
                            }
                        } else {
                            Log.e(LOG_TAG, task.getException().getMessage().toString());
                        }
                    }
                });

            } else {
                Toast.makeText(this, R.string.something_wrong, Toast.LENGTH_SHORT).show();
            }
        }
    }

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
        Log.d(LOG_TAG, "User email: " + userEmail);

        User newUser = new User();
        newUser.setUserKey(uid);
        newUser.setProfileSummary(getString(R.string.hello_world));
        newUser.setUserName(userName);
        newUser.setUserEmailAddress(userEmail);

        FileHelper.writeObjectToFile(this, newUser, FileHelper.USER_OBJECT_FILE);

        db.collection(FirebaseContract.UsersNode.NAME)
                .document(newUser.getUserKey())
                .set(newUser.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    goToMainActivity();
                } else { //did not write new user to firestore
                    Toast.makeText(PreSignInActivity.this,
                            R.string.something_wrong,
                            Toast.LENGTH_SHORT).show();

                    Log.e(LOG_TAG, task.getException().getStackTrace().toString());
                }
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(PreSignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }





}
