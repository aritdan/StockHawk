package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.history.HistoryAdapter;
import com.udacity.stockhawk.history.StockHistoryLoaderCallbacks;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by dan.ariton on 17-May-17.
 */

public class HistoryActivity extends AppCompatActivity {

    private static final int LOADER_HISTORY = 10;

    @BindView(R.id.tv_hist_title)
    protected TextView titleTextView;

    @BindView(R.id.rv_stock_history)
    protected RecyclerView historyRecyclerView;

    @BindView(R.id.lc_history)
    protected LineChart historyLineChart;

    @BindView(R.id.history_error)
    protected TextView errorTextView;

    public HistoryActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("ENTER onCreate()");
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        final String stockName = getIntent().getStringExtra(Contract.Quote.COLUMN_SYMBOL);

        titleTextView.setText(stockName);

        final HistoryAdapter historyAdapter = new HistoryAdapter();
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(historyAdapter);

        historyLineChart.getAxisLeft().setTextColor(getResources().getColor(android.R.color.white));
        historyLineChart.getAxisLeft().setTextSize(15f);
        historyLineChart.getXAxis().setTextColor(getResources().getColor(android.R.color.white));
        historyLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        historyLineChart.getXAxis().setTextSize(12f);
        historyLineChart.getLegend().setTextColor(getResources().getColor(android.R.color.white));
        historyLineChart.getLegend().setTextSize(15f);

        final Bundle loaderBundle = new Bundle();
        loaderBundle.putString(Contract.Quote.COLUMN_SYMBOL, stockName);

        final Loader loader = getSupportLoaderManager().getLoader(LOADER_HISTORY);
        if (loader == null) {
            getSupportLoaderManager().initLoader(LOADER_HISTORY, loaderBundle, new StockHistoryLoaderCallbacks(this,
                    historyAdapter, historyLineChart, errorTextView));
        } else {
            getSupportLoaderManager().restartLoader(LOADER_HISTORY, loaderBundle, new StockHistoryLoaderCallbacks(this,
                    historyAdapter, historyLineChart, errorTextView));
        }

        Timber.d("EXIT onCreate()");
    }

}
