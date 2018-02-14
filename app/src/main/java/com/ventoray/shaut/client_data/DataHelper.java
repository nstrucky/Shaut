package com.ventoray.shaut.client_data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ventoray.shaut.model.FriendRequest;

import java.util.List;

import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_CITY_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_CITY_NAME;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_POTENTIAL_FRIEND_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_IMAGE_URL;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_PROFILE_CONTENT;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_USER_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_USER_NAME;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.CONTENT_URI;

/**
 * Created by Nick on 2/12/2018.
 */

public class DataHelper {

    public static final String LOG_TAG = "DataHellper";


    public static void refreshFriendRequests(Context context, List<FriendRequest> friendRequests) {
        ContentResolver contentResolver = context.getContentResolver();

//        int deleted = contentResolver.delete(CONTENT_URI, null, null);
//        Log.d(LOG_TAG, "Deleted " + deleted + " records");

        for (FriendRequest request : friendRequests) {
            String requesterName = request.getRequesterUserName();
            String requesterKey = request.getRequesterUserKey();
            String requesterPhoto = request.getRequesterImageUrl();
            String userKey = request.getPotentialFriendKey();
            String cityKey = request.getCityKey();
            String cityName = request.getCityName();
            String requeterProfile = request.getRequesterProfileContent();

            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUMN_REQUESTER_USER_NAME, requesterName);
            contentValues.put(COLUMN_REQUESTER_USER_KEY, requesterKey);
            contentValues.put(COLUMN_REQUESTER_IMAGE_URL, requesterPhoto);
            contentValues.put(COLUMN_REQUESTER_PROFILE_CONTENT, requeterProfile);
            contentValues.put(COLUMN_POTENTIAL_FRIEND_KEY, userKey);
            contentValues.put(COLUMN_CITY_KEY, cityKey);
            contentValues.put(COLUMN_CITY_NAME, cityName);

            contentResolver.insert(CONTENT_URI, contentValues);

        }

        testCursor(contentResolver);
    }


    private static void testCursor(ContentResolver contentResolver) {
        Cursor cursor = contentResolver.query(
                CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (!cursor.moveToFirst()) return;


        for (int i = 1; i < cursor.getCount(); i++) {
            if (!cursor.moveToPosition(i)) break;
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_REQUESTER_USER_NAME));
            Log.d(LOG_TAG, "Requester: " + name);

        }

        cursor.close();
    }
}
