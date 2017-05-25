package com.magdy.mguide.widget;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.magdy.mguide.Data.Contract;
import com.magdy.mguide.R;
import com.magdy.mguide.UI.DetailActivity;
import com.magdy.mguide.UI.MainActivity;


/**
 * Created by engma on 5/23/2017.
 */

public class MovieWidgetProvider extends AppWidgetProvider {
    @SuppressLint("PrivateResource")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_movie_grid);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Set up the collection
                setRemoteAdapter(context, remoteViews);

            Intent clickIntentTemplate = new Intent(context, DetailActivity.class);

            PendingIntent pendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.widget_grid, pendingIntentTemplate);
            remoteViews.setEmptyView(R.id.widget_grid, R.id.widget_empty);
            remoteViews.setInt(R.id.widget_grid, "setBackgroundResource", R.color.back_grey);
            remoteViews.setInt(R.id.widget_content, "setBackgroundResource", R.color.back_grey);
            remoteViews.setContentDescription(R.id.widget_grid, context.getString(R.string.widget_cd));


            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (Contract.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid);
        }
    }
    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_grid,
                new Intent(context, MovieWidgetService.class));
    }

    /*
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */

}
