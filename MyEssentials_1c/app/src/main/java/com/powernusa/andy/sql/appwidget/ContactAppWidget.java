package com.powernusa.andy.sql.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.powernusa.andy.sql.R;

/**
 * Implementation of App Widget functionality.
 */
public class ContactAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.contact_app_widget);
        setList(rv,context,appWidgetId);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.contact_list);
    }

    private static void setList(RemoteViews rv,Context context,int appWidgetId){
        Intent adapter = new Intent(context,ContactAppWidgetService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetId);
        rv.setRemoteAdapter(R.id.contact_list,adapter);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

