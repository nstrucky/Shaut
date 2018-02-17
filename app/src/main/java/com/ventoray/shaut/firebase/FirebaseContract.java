package com.ventoray.shaut.firebase;

import java.util.Map;

import static com.ventoray.shaut.firebase.FirebaseContract.UsersCollection.User.STRANGERS_FRIEND_REQUESTS_COLLECTION;

/**
 * Created by Nick on 2/5/2018.
 */

public class FirebaseContract {

    public interface FirebaseMapObject {
        Map<String, Object> toMap();
    }

    private static final String USERS_COLLECTION = "users";
    private static final String SHAUTS_COLLECTION = "shauts";
    private static final String CITIES_COLLECTION = "cities";
    private static final String MESSAGES_COLLECTION = "messages";
    private static final String USERS_FRIEND_REQUESTS_COLLECTION = "users_friend_requests";


    public static class CitiesNode {
        public static final String COLLECTION = CITIES_COLLECTION;

        public static class City {
            public static final String USERS_NODE = "users";
            public static final String SHAUTS_NODE = "shauts";
            public static final String USER_COUNT_VARIABLE = "user_count";
        }
    }

    public static class MessagesCollection {
        public static final String NAME = MESSAGES_COLLECTION;
        public static final String CHAT_MESSAGES_COLLECTION = "chatMessagesCollection";


        public static class ChatMessagesCollection {
            public static final String NAME = CHAT_MESSAGES_COLLECTION;
            public static class ChatMessage {
                public static final String FIELD_USER_NAME = "userName";
                public static final String FIELD_USER_KEY = "userKey";
                public static final String FIELD_MESSAGE_TEXT = "messageText";
                public static final String FIELD_MESSAGE_TIME = "messageTime";
            }
        }
    }

    public static class ShautsCollection {
        public static final String NAME = SHAUTS_COLLECTION;

        public static class Shauts {
            public static final String FIELD_USER_NAME = "userName";
            public static final String FIELD_USER_KEY = "userKey";
            public static final String FIELD_CITY_KEY = "cityKey";
            public static final String FIELD_PROFILE_IMAGE_URL = "profileImageUrl";
            public static final String FIELD_MESSAGE_TEXT = "messageText";
            public static final String FIELD_MESSAGE_TIME = "messageTime";
            public static final String FIELD_UP_VOTE = "upVote";
            public static final String FIELD_DOWN_VOTE = "downVote";
        }
    }

    public static class UsersCollection {
        public static final String NAME = USERS_COLLECTION;

        public static class User {
            public static final String CITY_VARIABLE = "city";
            public static final String NAME = "user_object";
            public static final String CHATROOMS_COLLECTION = "chatroom";
            public static final String STRANGERS_FRIEND_REQUESTS_COLLECTION =
                    "strangers_friend_requests";

            //Field names for user object
            public static final String FIELD_USER_KEY = "userKey";
            public static final String FIELD_USER_NAME = "userName";
            public static final String FIELD_USER_EMAIL_ADDRESS = "userEmailAddress";
            public static final String FIELD_CITY_KEY = "cityKey";
            public static final String FIELD_CITY_NAME = "cityName";
            public static final String FIELD_PROFILE_SUMMARY = "profileSummary";
            public static final String FIELD_PROFILE_IMAGE_URL = "profileImageUrl";
            public static final String FIELD_MOVED_TO_CITY_DATE = "movedToCityDate";


            public static class ChatroomsCollection {
                public static final String NAME = CHATROOMS_COLLECTION;

                public static class ChatMetaData {
                    public static final String NAME = "chatmetadata_object";

                    public static final String FIELD_CHATROOM_ID = "chatroomId";
                    public static final String FIELD_TIMESTAMP = "timeStamp";
                    public static final String FIELD_USER_KEY = "userKey";
                    public static final String FIELD_USER_NAME = "userName";
                    public static final String FIELD_FRIEND_KEY = "friendKey";
                    public static final String FIELD_FRIEND_NAME = "friendName";
                    public static final String FIELD_LAST_MESSAGE = "lastMessage";
                }
            }


            public static class StrangersRequestCollection {
                public static final String NAME = STRANGERS_FRIEND_REQUESTS_COLLECTION;

                public static class Request {
                    public static final String NAME = "request_object";

                    //field names for strangers friend request object
                    public static final String FIELD_REQUESTER_USER_KEY = "requesterUserKey";
                    public static final String FIELD_REQUESTER_USER_NAME = "requesterUserName";
                    public static final String FIELD_REQUESTER_IMAGE_URL = "requesterImageUrl";
                    public static final String FIELD_POTENTIAL_FRIEND_KEY = "potentialFriendKey";
                    public static final String FIELD_REQUESTER_PROFILE_CONTENT = "requesterProfileContent";
                    public static final String FIELD_CITY_KEY = "cityKey";
                    public static final String FIELD_CITY_NAME = "cityName";
                }
            }
        }
    }
}
