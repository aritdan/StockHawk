package com.udacity.stockhawk.history;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.history.pojo.HistoryItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by dan.ariton on 17-May-17.
 */

public class StockHistoryLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context context;
    private HistoryAdapter historyAdapter;

    public StockHistoryLoaderCallbacks(final Context context,
                                       final HistoryAdapter historyAdapter) {
        this.historyAdapter = historyAdapter;
        this.context = context;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Timber.d("ENTER onCreateLoader()");

        final String stockName = args.getString(Contract.Quote.COLUMN_SYMBOL);
        Timber.d("stockName=[%s]", stockName);

        final Uri uri = Contract.Quote.URI.buildUpon().appendPath(stockName).build();
        Timber.d("uri=[%s]", uri);

        final CursorLoader cursorLoader = new CursorLoader(context,
                uri, null, null, null, null);
        Timber.d("EXIT onCreateLoader()");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Timber.d("ENTER onLoadFinished()");

        if (data != null && data.getCount() > 0) {
            data.moveToFirst();

            final String history = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
            Timber.d("history=[%s]", history);

            if (history != null && !history.trim().equals("")) {
                final String[] historyLines = history.split("\n");
                Timber.d("historyLines.length=[%s]", historyLines.length);

                final List<HistoryItem> historyItemList = new ArrayList<>();
                for (final String historyLine : historyLines) {
                    final HistoryItem historyItem = new HistoryItem();
                    final String[] historyItemData = historyLine.split(",");
                    Timber.d("historyDateInMillis=[%s], historyClose=[%s]", historyItemData[0], historyItemData[1]);

                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.valueOf(historyItemData[0].trim()));
                    final Date historyDate = calendar.getTime();
                    Timber.d("historyDate=[%s]", historyDate);

                    historyItem.setDate(historyDate);
                    historyItem.setClose(new BigDecimal(historyItemData[1].trim()));

                    historyItemList.add(historyItem);
                }

                historyAdapter.setHistoryItemList(historyItemList);
            }
        }


        Timber.d("EXIT onLoadFinished()");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Timber.d("ENTER onLoaderReset()");
        historyAdapter.setHistoryItemList(new ArrayList<HistoryItem>());
        Timber.d("EXIT onLoaderReset()");
    }
}
