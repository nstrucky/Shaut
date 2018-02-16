package com.ventoray.shaut.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.ventoray.shaut.R;

import static com.ventoray.shaut.client_data.FriendRequestsContract.BASE_CONTENT_URI;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_IMAGE_URL;
import static com.ventoray.shaut.client_data.FriendRequestsContract.FriendRequestEntry.COLUMN_REQUESTER_USER_NAME;
import static com.ventoray.shaut.client_data.FriendRequestsContract.PATH_FRIEND_REQUESTS;

public class StackWidgetService extends RemoteViewsService {

    public static final String LOG_TAG = "StackWidgetService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteStackViewFactory(this.getApplicationContext());
    }


    class RemoteStackViewFactory implements RemoteViewsFactory {

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

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.stackwidget_item_friend_requests);
            if (cursor == null || !cursor.moveToPosition(position)) return null;
//            Log.d(LOG_TAG, "NOT NULL!!");
//                String profileSummary = testUsers.get(position).getProfileSummary();
            String imageUrl =
                    cursor.getString(cursor.getColumnIndex(COLUMN_REQUESTER_IMAGE_URL));
            String requesterName =
                    cursor.getString(cursor.getColumnIndex(COLUMN_REQUESTER_USER_NAME));



            if (imageUrl != null && !imageUrl.isEmpty()) {
            }


//            Bitmap profileBitmap = FileHelper.getBitmapFromURL(imageUrl);

//            if bitmap is null show a picture of a cute little guy with a bowtie
//            if (profileBitmap != null) {
//                views.setImageViewBitmap(R.id.imageView_profilePicture, profileBitmap);
            //Might need to be something like this:
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (!Utils.isEmpty(imageUrl)) {
//                        picasso.load(imageUrl)
//                                .placeholder(R.drawable.empty_photo)
//                                .into(remoteView, R.id.widget_listitem_picture, new int[]{appWidgetId});
//                    }
//                }
//            });
//

//            } else {
//                views.setImageViewResource(R.id.imageView_profilePicture, R.drawable.spanky);
//            }

            views.setTextViewText(R.id.textView_requestername, requesterName);

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




}
