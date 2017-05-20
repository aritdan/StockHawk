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

    private static final DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    private static final DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
    static {
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
    }

    public UpdateWidgetIntentService() {
        super("UpdateStockWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("ENTER onHandleIntent()");

        final Cursor cursor = getContentResolver().query(Contract.Quote.URI, Contract.Quote.QUOTE_COLUMNS.toArray(new String[] {}),
                null, null, null);

        final Map<Integer, String> widgetMapping = new HashMap<>();
        final int[] widgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        for (final int widgetId : widgetIds) {
            final String symbol = PrefUtils.getSymbolForWidgetId(widgetId, this);
            if (!"".equals(symbol)) {
                widgetMapping.put(widgetId, symbol);
            }
        }

        if (cursor != null && cursor.getCount() > 0) {
            for (final Map.Entry<Integer, String> widgetSymbol : widgetMapping.entrySet()) {

                boolean found = false;
                cursor.moveToFirst();

                do {
                    final String symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
                    if (widgetSymbol.getValue().equals(symbol)) {
                        updateWidget(cursor, widgetSymbol.getKey());
                        found = true;
                    }
                } while (!found && cursor.moveToNext());
            }


        }

        Timber.d("EXIT onHandleIntent()");
    }

    private void updateWidget(final Cursor cursor, final int widgetId) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        final String symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
        final String price = dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE));
        final float perc = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE) / 100;
        final String percChange = percentageFormat.format(perc);

        final RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.widget_stock_layout);

        remoteViews.setTextViewText(R.id.wd_symbol, symbol);
        remoteViews.setTextViewText(R.id.wd_price, price);
        remoteViews.setTextViewText(R.id.wd_change, percChange);
        if (perc > 0) {
            remoteViews.setInt(R.id.wd_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteViews.setInt(R.id.wd_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }

        appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
}