package com.ventoray.shaut.ui;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ventoray.shaut.R;
import com.ventoray.shaut.firebase.FirebaseContract;
import com.ventoray.shaut.firebase.Write;
import com.ventoray.shaut.model.ChatMessage;
import com.ventoray.shaut.model.ChatMetaData;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.ui.adapter.ChatMessageAdapter;
import com.ventoray.shaut.util.FileHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MessageActivity";
    public static final String PARCEL_KEY_CHAT_META_DATA =
            "com.ventoray.shaut.ui.MessageActivity.PARCEL_KEY_CHAT_META_DATA";

    @BindView(R.id.recyclerView_messages) RecyclerView recyclerView;
    @BindView(R.id.toolbar_messages) Toolbar toolbar;
    @BindView(R.id.button_send) ImageButton sendButton;
    @BindView(R.id.textInputEditText) TextInputEditText textInputEditText;

    private ChatMetaData userChatMetaData;
    private User userObject;
    private List<ChatMessage> messages;
    private ChatMessageAdapter adapter;
    private FirebaseFirestore db;
    private ListenerRegistration messagesReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        initialieObjects();
        setUpActionBar();
        db = FirebaseFirestore.getInstance();
        setSendButtonListener();
        initializeMessages();

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
        adapter = new ChatMessageAdapter(this, messages, userObject.getUserKey());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        if (userChatMetaData == null) return;

        Query messagesQuery = db
                .collection(FirebaseContract.MessagesCollection.NAME)
                .document(userChatMetaData.getChatroomId())
                .collection(FirebaseContract.MessagesCollection.ChatMessagesCollection.NAME)
                .orderBy(FirebaseContract.MessagesCollection.ChatMessagesCollection.ChatMessage.FIELD_MESSAGE_TIME,
                        Query.Direction.ASCENDING);
        messagesReg = messagesQuery.addSnapshotListener(messagesListener);
    }

    EventListener<QuerySnapshot> messagesListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            messages.clear();
            if (documentSnapshots != null && documentSnapshots.size() > 0) {
                for (DocumentSnapshot documentSnapshot : documentSnapshots) {

                    ChatMessage chatMessage = documentSnapshot.toObject(ChatMessage.class);
                    Log.d(LOG_TAG, chatMessage.getMessageText());
                    messages.add(chatMessage);
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);
            } else {
                Log.d(LOG_TAG, "no docs received");
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesReg != null) {
            messagesReg.remove();
        }
    }
}
