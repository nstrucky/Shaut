package com.ventoray.shaut.firebase;

import java.util.Map;

/**
 * Created by Nick on 2/5/2018.
 */

public class FirebaseContract {

    public interface FirebaseMapObject {
        Map<String, Object> toMap();
    }

    private static final String USERS_NODE = "users";
    public static final String SHAUTS_NODE = "shauts";
    private static final String CITIES_NODE =  "cities";
    public static final String CHATROOMS_NODE = "chatroom";

    public static class CitiesNode {
        public static final String NAME = FirebaseContract.CITIES_NODE;

        public static class City {
            public static final String USERS_NODE = "users";
            public static final String SHAUTS_NODE = "shauts";
            public static final String USER_COUNT_VARIABLE = "user_count";
        }
    }


    public static class UsersNode {
        public static final String NAME = FirebaseContract.USERS_NODE;

        public static class User {
            public static final String CITY_VARIABLE = "city";
            public static final String USER_OBJECT = "user_object";
            public static final String USERS_REQUESTS_NODE = "users_requests";
            public static final String STRANGERS_REQUESTS_NODE = "strangers_requests";
            public static final String SHAUTS_NODE = "shauts";
            public static final String FRIENDS_NODE = "friends";
        }
    }


}
