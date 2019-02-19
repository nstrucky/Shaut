package com.ventoray.shaut.widget;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ventoray.shaut.firebase.Write;
import com.ventoray.shaut.model.FriendRequest;
import com.ventoray.shaut.model.User;
import com.ventoray.shaut.util.FileManager;

import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_USER_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.CONTENT_URI;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class RespondToRequestService extends IntentService {

    public static final String LOG_TAG = "RespondToRequestService";

    public static final String ACTION_ACCEPT_FRIEND_REQUEST =
            "com.ventoray.shaut.widget.action.action_accept_friend_request";
    public static final String ACTION_DECLINE_FRIEND_REQUEST =
            "com.ventoray.shaut.widget.action.action_decline_friend_request";


    public static final String EXTRA_REQUESTER_ID =
            "com.ventoray.shaut.widget.extra.requester_id";
    public static final String EXTRA_REQUESTER_NAME =
            "com.ventorya.shaut.widget.extra.requester_name";

    public RespondToRequestService() {
        super("RespondToRequestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "Intent Handled");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ACCEPT_FRIEND_REQUEST.equals(action)) {
                final String requesterId = intent.getStringExtra(EXTRA_REQUESTER_ID);
                final String requesterName = intent.getStringExtra(EXTRA_REQUESTER_NAME);
                handleActionAccept(requesterId, requesterName);
            } else if (ACTION_DECLINE_FRIEND_REQUEST.equals(action)) {
                final String requesterId = intent.getStringExtra(EXTRA_REQUESTER_ID);
                handleActionDecline(requesterId);
            }
        }
    }

    /**
     * Accept the friend request
     *
     * @param requesterId
     */
    private void handleActionAccept(String requesterId, String requesterName) {
        acceptFriendRequest(requesterId, requesterName);
        Log.d(LOG_TAG, "Accept button pushed " + requesterId);

    }

    /**
     * Decline the friend request
     *
     * @param requesterId
     */
    private void handleActionDecline(final String requesterId) {
        Write.deleteFriendRequest(requesterId, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                int deleted = getApplicationContext().getContentResolver().delete(CONTENT_URI,
                        COLUMN_REQUESTER_USER_KEY, new String[]{requesterId});
                Log.i(LOG_TAG, "Deleted " + deleted + " records");
                Utils.notifyAppWidget(getApplicationContext());
            }
        });

    }

    /**
     * Accepts the friend requests and makes a call to Write.initializeFriendship
     *
     * @param requesterId
     * @param requesterName
     */
    private void acceptFriendRequest(String requesterId, String requesterName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        User userObject =
                (User) FileManager.readObjectFromFile(this, FileManager.USER_OBJECT_FILE);
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setRequesterUserKey(requesterId);
        friendRequest.setRequesterUserName(requesterName);

        Write.initializeFriendship(friendRequest, db, userObject);

    }

}
