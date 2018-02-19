package com.ventoray.shaut.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.ventoray.shaut.BaseActivity;
import com.ventoray.shaut.R;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.util.FileManager;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ventoray.shaut.firebase.FirebaseStorageContract.PublicDirectory.PROFILE_PICS_DIRECTORY;

public class ProfileEditorActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.editText_profileText)
    EditText profileSummaryEditText;
    @BindView(R.id.button_editProfile)
    FloatingActionButton editProfileFab;
    @BindView(R.id.imageView_profilePicture)
    ImageView profilePictureImageView;
    @BindView(R.id.button_camera)
    ImageButton cameraButton;

    static final int RC_IMAGE_CAPTURE = 1001;

    private static final String LOG_TAG = "ProfileEditorActivity";

    //storage
    private StorageReference storageReference;
    private boolean pictureTaken;
    private boolean textEdited;

    private User userObject;
    private Bitmap newProfileBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editor);
        ButterKnife.bind(this);
        setButtonListeners();
        setUpActionBar();
        storageReference = FirebaseStorage.getInstance().getReference();
        userObject = (User)
                FileManager.readObjectFromFile(this, FileManager.USER_OBJECT_FILE);

        setUserData();
    }

    private void setUserData() {
        String profileText = userObject.getProfileSummary();
        String picUrl = userObject.getProfileImageUrl();
        if (picUrl != null) {
            Picasso
                    .with(this)
                    .load(picUrl)
                    .into(profilePictureImageView);
        }

        if (profileText != null) {
            profileSummaryEditText.setText(profileText);
        }
    }

    private void setButtonListeners() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        editProfileFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileSummaryEditText.setEnabled(true);
                textEdited = true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_editor, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_done:
                Toast.makeText(this, "Saving", Toast.LENGTH_SHORT).show();
                saveChanges();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle(R.string.edit_profile);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RC_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            newProfileBitmap = (Bitmap) extras.get("data");
            profilePictureImageView.setImageBitmap(newProfileBitmap);
            pictureTaken = true;
        }
    }

    private void saveChanges() {
        if (pictureTaken) {
            saveImageToFirebase();
        } else {
            saveUserDataToFirebase();
            finish();
        }
    }

    private void saveImageToFirebase() {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId.isEmpty()) {
            Toast.makeText(this,
                    R.string.something_wrong, Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Could not use userId: " + userId);

        }
        StorageReference profilePicRef = storageReference.child(PROFILE_PICS_DIRECTORY)
                .child(userId);

        if (newProfileBitmap == null) {
            Toast.makeText(this, "no bitmap", Toast.LENGTH_SHORT).show();
            return; //nothing to do
        }

        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        newProfileBitmap.compress(Bitmap.CompressFormat.PNG, 100, boas);
        byte[] picBytes = boas.toByteArray();

        UploadTask uploadTask = profilePicRef.putBytes(picBytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(LOG_TAG, "Firebase Storage Error: " + e.getLocalizedMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d(LOG_TAG, "User Profile Pic: " + downloadUrl.toString());
                userObject.setProfileImageUrl(downloadUrl.toString());
                saveUserDataToFirebase();
            }
        });
    }

    private void saveUserDataToFirebase() {
        userObject.setProfileSummary(profileSummaryEditText.getText().toString());
        FileManager.writeObjectToFile(ProfileEditorActivity.this,
                userObject, FileManager.USER_OBJECT_FILE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection(FirebaseContract.UsersCollection.NAME)
                .document(userObject.getUserKey())
                .update(userObject.toMap())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileEditorActivity.this,
                                R.string.saved, Toast.LENGTH_SHORT).show();
                        finish(); //nothing left to do
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(LOG_TAG, "Error saving user profile: " + e.getLocalizedMessage());
            }
        });
    }

}
