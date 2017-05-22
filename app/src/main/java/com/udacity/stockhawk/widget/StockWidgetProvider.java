package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import timber.log.Timber;

/**
 * Created by dan.ariton on 17-May-17.
 */

public class StockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Timber.d("ENTER onUpdate()");

        final ComponentName stockWidgetComponentName = new ComponentName(context, StockWidgetProvider.class);
        final int[] widgetIds = appWidgetManager.getAppWidgetIds(stockWidgetComponentName);

        final Intent intent = new Intent(context.getApplicationContext(), UpdateWidgetIntentService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        context.startService(intent);

        //Timber.d("EXIT onUpdate()");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Timber.d("ENTER onReceive()");
        super.onReceive(context, intent);
        //Timber.d("EXIT onReceive()");
    }
}
