package com.ventoray.shaut.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ventoray.shaut.R;
import com.ventoray.shaut.util.FileManager;

import static com.ventoray.shaut.client_data.FriendRequestsContract.BASE_CONTENT_URI;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_IMAGE_URL;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_USER_KEY;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_USER_NAME;
import static com.ventoray.shaut.client_data.FriendRequestsContract.PATH_FRIEND_REQUESTS;

public class StackWidgetService extends RemoteViewsService {


    public interface OnBitmapRetrievedListener {
        void onBitmapRetrieved(Bitmap bitmap);
    }

    public static final String LOG_TAG = "StackWidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteStackViewFactory(this.getApplicationContext());
    }
}

class RemoteStackViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private Cursor cursor;

    public RemoteStackViewFactory(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Uri REQUESTS_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_FRIEND_REQUESTS)
                .build();

        if (cursor != null) cursor.close();

        cursor = context.getContentResolver()
                .query(REQUESTS_URI,
                        null,
                        null,
                        null,
                        COLUMN_REQUESTER_USER_NAME);
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public int getCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        final RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.stackwidget_item_friend_requests);
        if (cursor == null || !cursor.moveToPosition(position)) return null;
        String imageUrl =
                cursor.getString(cursor.getColumnIndex(COLUMN_REQUESTER_IMAGE_URL));
        String requesterName =
                cursor.getString(cursor.getColumnIndex(COLUMN_REQUESTER_USER_NAME));
        String requesterId =
                cursor.getString(cursor.getColumnIndex(COLUMN_REQUESTER_USER_KEY));


        Bitmap bitmap = FileManager.getBitmapFromURL(imageUrl);
        if (bitmap != null) {
            views.setImageViewBitmap(R.id.imageView_profilePicture, bitmap);
        } else {
            views.setImageViewResource(R.id.imageView_profilePicture, R.drawable.spanky);
        }
        views.setTextViewText(R.id.textView_requestername, requesterName);


        Bundle extras = new Bundle();
        extras.putString(RespondToRequestService.EXTRA_REQUESTER_ID, requesterId);
        extras.putString(RespondToRequestService.EXTRA_REQUESTER_NAME, requesterName);

        //Accept request fill Intent
        Intent acceptFillIntent = new Intent();
        acceptFillIntent.putExtras(extras);
        acceptFillIntent.setAction(RespondToRequestService.ACTION_ACCEPT_FRIEND_REQUEST);


        //Decline request fill intent
        Intent declineFillIntent = new Intent();
        declineFillIntent.putExtras(extras);
        declineFillIntent.setAction(RespondToRequestService.ACTION_DECLINE_FRIEND_REQUEST);

        //set fill intents
        views.setOnClickFillInIntent(R.id.imageButton_accept, acceptFillIntent);
        views.setOnClickFillInIntent(R.id.imageButton_decline, declineFillIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}


