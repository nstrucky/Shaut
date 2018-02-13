package com.ventoray.shaut.client_data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nick on 2/12/2018.
 */

public class FriendRequestsContract {

    public static final String AUTHORITY = "com.ventoray.shaut";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FRIEND_REQUESTS = "requests";


    public static final class FriendRequestEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_FRIEND_REQUESTS)
                .build();

        public static final String TABLE_NAME = "requests";

        public static final String COLUMN_REQUESTER_USER_KEY = "requesterUserKey";
        public static final String COLUMN_REQUESTER_USER_NAME = "requesterUserName";
        public static final String COLUMN_REQUESTER_IMAGE_URL = "requesterImageUrl";
        public static final String COLUMN_REQUESTER_PROFILE_CONTENT = "requesterProfileContent";
        public static final String COLUMN_POTENTIAL_FRIEND_KEY = "potentialFriendKey";
        public static final String COLUMN_CITY_KEY = "cityKey";
        public static final String COLUMN_CITY_NAME = "cityName";

    }

}
