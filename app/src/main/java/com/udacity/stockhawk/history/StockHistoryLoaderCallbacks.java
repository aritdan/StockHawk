package com.udacity.stockhawk.history;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.history.pojo.HistoryItem;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private LineChart lineChart;
    private TextView errorTextView;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public StockHistoryLoaderCallbacks(final Context context,
                                       final HistoryAdapter historyAdapter,
                                       final LineChart lineChart,
                                       final TextView errorTextView) {
        this.historyAdapter = historyAdapter;
        this.context = context;
        this.lineChart = lineChart;
        this.errorTextView = errorTextView;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //Timber.d("ENTER onCreateLoader()");

        errorTextView.setVisibility(View.GONE);

        final String stockName = args.getString(Contract.Quote.COLUMN_SYMBOL);
        Timber.d("stockName=[%s]", stockName);

        final Uri uri = Contract.Quote.URI.buildUpon().appendPath(stockName).build();
        Timber.d("uri=[%s]", uri);

        final CursorLoader cursorLoader = new CursorLoader(context,
                uri, null, null, null, null);
        //Timber.d("EXIT onCreateLoader()");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Timber.d("ENTER onLoadFinished()");

        if (data != null && data.getCount() > 0) {
            data.moveToFirst();

            final String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
            final String history = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
            //Timber.d("history=[%s]", history);

            if (!Strings.isNullOrEmpty(history)) {
                final String[] historyLines = history.split("\n");
                Timber.d("historyLines.length=[%s]", historyLines.length);
                final List<Entry> entryList = new ArrayList<>();
                final List<String> dateLabelList = new ArrayList<>();

                final List<HistoryItem> historyItemList = new ArrayList<>();
                for (int i = 0; i <historyLines.length; i++) {
                    final String historyLine = historyLines[i];
                    final HistoryItem historyItem = new HistoryItem();
                    final String[] historyItemData = historyLine.split(",");
                    //Timber.d("historyDateInMillis=[%s], historyClose=[%s]", historyItemData[0], historyItemData[1]);

                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.valueOf(historyItemData[0].trim()));
                    final Date historyDate = calendar.getTime();
                    //Timber.d("historyDate=[%s]", historyDate);

                    historyItem.setDate(historyDate);
                    historyItem.setClose(new BigDecimal(historyItemData[1].trim()));

                    historyItemList.add(historyItem);

                }

                for (int i = historyLines.length - 1; i >= 0; i--) {
                    final String historyLine = historyLines[i];
                    final String[] historyItemData = historyLine.split(",");

                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.valueOf(historyItemData[0].trim()));
                    final Date historyDate = calendar.getTime();

                    entryList.add(new Entry(new Long(historyItemData[0].trim()).floatValue(),
                            Float.parseFloat(historyItemData[1].trim())));

                    dateLabelList.add(DATE_FORMAT.format(historyDate));
                }
                final LineDataSet lineDataSet = new LineDataSet(entryList, context.getString(R.string.chart_label));
                lineDataSet.setColor(context.getResources().getColor(R.color.material_green_700));
                final LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);

                final XAxis xAxis = lineChart.getXAxis();
                xAxis.setLabelCount(4);
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis((long) value);
                        return DATE_FORMAT.format(calendar.getTime());
                    }
                });

                historyAdapter.setHistoryItemList(historyItemList);
            } else {
                errorTextView.setText(context.getString(R.string.error_history_not_found, symbol));
                errorTextView.setVisibility(View.VISIBLE);
            }
        }


        //Timber.d("EXIT onLoadFinished()");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Timber.d("ENTER onLoaderReset()");
        historyAdapter.setHistoryItemList(new ArrayList<HistoryItem>());
        errorTextView.setVisibility(View.GONE);
        //Timber.d("EXIT onLoaderReset()");
    }
}
