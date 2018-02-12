package com.ventoray.shaut.ui;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.ventoray.shaut.R;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.firebase.Write;
import com.ventoray.shaut.model.ChatMessage;
import com.ventoray.shaut.model.ChatMetaData;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.util.FileHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageActivity extends AppCompatActivity {

    public static final String PARCEL_KEY_CHAT_META_DATA =
            "com.ventoray.shaut.ui.MessageActivity.PARCEL_KEY_CHAT_META_DATA";

    @BindView(R.id.recyclerView_messages) RecyclerView recyclerView;
    @BindView(R.id.toolbar_messages) Toolbar toolbar;
    @BindView(R.id.button_send) ImageButton sendButton;
    @BindView(R.id.textInputEditText) TextInputEditText textInputEditText;

    private ChatMetaData userChatMetaData;
    private User userObject;
    private List<ChatMessage> messages;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        initialieObjects();
        setUpActionBar();
        db = FirebaseFirestore.getInstance();
        setSendButtonListener();

    }

    private void setUpActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (userChatMetaData == null) return;
            String friendName = userChatMetaData.getFriendName();
            actionBar.setTitle(friendName);
        }

    }

    private void initialieObjects() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(PARCEL_KEY_CHAT_META_DATA)) {
            userChatMetaData = (ChatMetaData) intent.getParcelableExtra(PARCEL_KEY_CHAT_META_DATA);
        }
        userObject =
                (User) FileHelper.readObjectFromFile(this, FileHelper.USER_OBJECT_FILE);
    }

    private void setSendButtonListener() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textInputEditText.getText().toString();
                Toast.makeText(MessageActivity.this, message, Toast.LENGTH_SHORT).show();
                textInputEditText.setText(null);
                textInputEditText.clearAnimation();
                if (message != null && !message.isEmpty()) {
                    Write.sendMessage(message, db, userObject, userChatMetaData);
                }
            }
        });
    }







    private void initializeMessages() {
        messages = new ArrayList<>();

        if (userChatMetaData == null) return;



    }

}
