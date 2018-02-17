package com.ventoray.shaut.client_data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.ventoray.shaut.client_data.FriendRequestsContract.AUTHORITY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.CONTENT_URI;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.TABLE_NAME;
import static com.ventoray.shaut.client_data.FriendRequestsContract.PATH_FRIEND_REQUESTS;

/**
 * Created by Nick on 2/12/2018.
 */

public class FriendRequestsContentProvider extends ContentProvider {

    private FriendRequestsDbHelper friendRequestsDbHelper;
    public static final int URI_FRIEND_REQUEST_SINGLE = 1000;
    public static final int URI_FRIEND_REQUESTS_ALL = 1001;
    private static final UriMatcher sUriMatcher = buildMatcher();

    public static UriMatcher buildMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PATH_FRIEND_REQUESTS, URI_FRIEND_REQUESTS_ALL);
        uriMatcher.addURI(AUTHORITY, PATH_FRIEND_REQUESTS + "/#", URI_FRIEND_REQUEST_SINGLE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        friendRequestsDbHelper = new FriendRequestsDbHelper(context);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        final SQLiteDatabase database = friendRequestsDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case URI_FRIEND_REQUESTS_ALL:
                cursor = database.query(
                        TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Could not match uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri returnedUri;
        final SQLiteDatabase database = friendRequestsDbHelper.getWritableDatabase();

        long id = database.insert(TABLE_NAME, null, contentValues);

        if (id > 0) {
            returnedUri = ContentUris.withAppendedId(CONTENT_URI, id);

        } else {
            throw new SQLException("Could not insert row into Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = friendRequestsDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int numRows = 0;

        switch (match) {
            case URI_FRIEND_REQUESTS_ALL:

                if (selectionArgs != null && selectionArgs.length > 0) {
                    selection = selection + "=?";
                }

                numRows = database.delete(
                        TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;

            default:
                throw new UnsupportedOperationException("Could not match Uri: " + uri);
        }

        if (numRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }



}
