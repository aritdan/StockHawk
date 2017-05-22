package com.udacity.stockhawk.widget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dan.ariton on 22-May-17.
 */

public class StockRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    private static final DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
    static {
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");
        percentageFormat.setNegativePrefix("-");
    }

    final List<Bundle> stockList = new ArrayList<>();
    private Context context;

    public StockRemoteViewFactory (final Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {
        // NOTHING
    }

    @Override
    public int getCount() {
        return stockList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.list_item_widget);

        final Bundle bundle = stockList.get(position);

        remoteViews.setTextViewText(R.id.wd_symbol, bundle.getString(Contract.Quote.COLUMN_SYMBOL));
        remoteViews.setTextViewText(R.id.wd_price, bundle.getString(Contract.Quote.COLUMN_PRICE));

        final float perc = bundle.getFloat(Contract.Quote.COLUMN_PERCENTAGE_CHANGE);
        final String percChange = percentageFormat.format(perc);
        remoteViews.setTextViewText(R.id.wd_change, percChange);

        if (perc > 0) {
            remoteViews.setInt(R.id.wd_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteViews.setInt(R.id.wd_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }

        return remoteViews;
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
        return stockList.get(position).getLong(Contract.Quote._ID);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        stockList.clear();

        final Cursor cursor = context.getContentResolver()
                .query(Contract.Quote.URI, null, null, null, Contract.Quote.COLUMN_SYMBOL);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                final Long id = cursor.getLong(Contract.Quote.POSITION_ID);
                final String symbol = cursor.getString(Contract.Quote.POSITION_SYMBOL);
                final String price = dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE));
                final float perc = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE) / 100;

                final Bundle bundle = new Bundle();
                bundle.putLong(Contract.Quote._ID, id);
                bundle.putString(Contract.Quote.COLUMN_SYMBOL, symbol);
                bundle.putString(Contract.Quote.COLUMN_PRICE, price);
                bundle.putFloat(Contract.Quote.COLUMN_PERCENTAGE_CHANGE, perc);
                stockList.add(bundle);

            } while (cursor.moveToNext());
        }
    }
}
