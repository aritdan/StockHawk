package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.AdapterViewFlipper;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by dan.ariton on 18-May-17.
 */

public class UpdateWidgetIntentService extends IntentService {

    public UpdateWidgetIntentService() {
        super("UpdateStockWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Timber.d("ENTER onHandleIntent()");

        final int[] widgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        for (final int widgetId : widgetIds) {
            updateWidget(widgetId);
        }

        //Timber.d("EXIT onHandleIntent()");
    }

    private void updateWidget(final int widgetId) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        final RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.widget_stock_layout);
        remoteViews.setRemoteAdapter(R.id.wd_flipper, new Intent(this, StockRemoteViewsService.class));

        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
}