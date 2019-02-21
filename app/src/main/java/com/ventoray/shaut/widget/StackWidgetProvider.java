package com.ventoray.shaut.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.ventoray.shaut.R;
import com.ventoray.shaut.ShautApplication;

import static com.ventoray.shaut.client_data.JobUtils.FRIEND_REQUEST_PULL_JOB_TAG;

/**
 * Implementation of App Widget functionality.
 */
public class StackWidgetProvider extends AppWidgetProvider {

    private final String LOG_TAG = "StackWidgetProvider";

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //set the StackWidgetService to act as the adapter for StackView
        Intent serviceIntent = new Intent(context, StackWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stackview);
        views.setRemoteAdapter(R.id.widget_stackview, serviceIntent);
        views.setEmptyView(R.id.widget_stackview, R.id.textview_empty_stack);

        //Button intents
        Intent responseIntent = new Intent(context, RespondToRequestService.class);
        PendingIntent responsePendingIntent =
                PendingIntent.getService(context, 0, responseIntent, 0);
        views.setPendingIntentTemplate(R.id.widget_stackview, responsePendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled

        FirebaseJobDispatcher dispatcher =
                ((ShautApplication) context.getApplicationContext()).getFirebaseJobDispatcher();
        if (dispatcher != null) {

            int result = dispatcher.cancel(FRIEND_REQUEST_PULL_JOB_TAG);
            Log.i(LOG_TAG, "Cancelling Friend Request Pull Service with result: " + result);

        }
        super.onDisabled(context);
    }
}

