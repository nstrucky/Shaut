package com.ventoray.shaut.client_data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nick on 2/12/2018.
 */

public class FriendRequestsContract {

    public static final String AUTHORITY = "com.ventoray.shaut";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FRIEND_REQUESTS = "friend_requests";


    public static final class FriendRequestEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendEncodedPath(PATH_FRIEND_REQUESTS)
                .build();

        public static final String TABLE_NAME = "friend_reqeusts";

        public static final String COLUMN_REQUESTER_USER_KEY = "requester_user_key";
        public static final String REQUESTER_USER_NAME = "requester_user_name";
        public static final String REQUESTER_IMAGE_URL = "requester_image_url";
        public static final String REQUESTER_PROFILE_CONTENT = "requester_profile_content";
        public static final String POTENTIAL_FRIEND_KEY = "potential_friend_key";
        public static final String CITY_KEY = "city_key";
        public static final String CITY_NAME = "city_name";

    }

}
