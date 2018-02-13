package com.ventoray.shaut.client_data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.CITY_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.CITY_NAME;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_USER_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.POTENTIAL_FRIEND_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.REQUESTER_IMAGE_URL;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.REQUESTER_PROFILE_CONTENT;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.REQUESTER_USER_NAME;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.TABLE_NAME;

/**
 * Created by Nick on 2/12/2018.
 */

public class FriendRequestsDbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "friend_requests.db";


    public FriendRequestsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE_TABLE " + TABLE_NAME + " (" +
                _ID                             + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_REQUESTER_USER_KEY       + " TEXT NOT NULL," +
                REQUESTER_USER_NAME             + " TEXT," +
                REQUESTER_IMAGE_URL             + " TEXT," +
                REQUESTER_PROFILE_CONTENT       + " TEXT NOT NULL," +
                POTENTIAL_FRIEND_KEY            + " TEXT NOT NULL," +
                CITY_KEY                        + " TEXT NOT NULL," +
                CITY_NAME                       + " TEXT NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
