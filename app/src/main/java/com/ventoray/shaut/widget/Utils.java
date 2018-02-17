package com.ventoray.shaut.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

import com.ventoray.shaut.R;

/**
 * Created by Nick on 2/17/2018.
 */

public class Utils {

    public static void notifyAppWidget(Context context) {
        int []ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context,
                        StackWidgetProvider.class));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        for (int i = 0; i < ids.length; i++) {
            int appWidgetId = ids[i];
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_stackview);
        }
    }
}
