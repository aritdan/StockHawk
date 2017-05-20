package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.history.HistoryAdapter;
import com.udacity.stockhawk.history.StockHistoryLoaderCallbacks;
import com.udacity.stockhawk.history.pojo.HistoryItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by dan.ariton on 17-May-17.
 */

public class HistoryActivity extends AppCompatActivity {

    private static final int LOADER_HISTORY = 10;

    @BindView(R.id.rv_stock_history)
    protected RecyclerView historyRecyclerView;

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

        final HistoryAdapter historyAdapter = new HistoryAdapter();
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(historyAdapter);

        final Bundle loaderBundle = new Bundle();
        loaderBundle.putString(Contract.Quote.COLUMN_SYMBOL, stockName);

        final Loader loader = getSupportLoaderManager().getLoader(LOADER_HISTORY);
        if (loader == null) {
            getSupportLoaderManager().initLoader(LOADER_HISTORY, loaderBundle, new StockHistoryLoaderCallbacks(this,
                    historyAdapter));
        } else {
            getSupportLoaderManager().restartLoader(LOADER_HISTORY, loaderBundle, new StockHistoryLoaderCallbacks(this,
                    historyAdapter));
        }
        historyAdapter.setHistoryItemList(createMockList());

        Timber.d("EXIT onCreate()");
    }

    private List<HistoryItem> createMockList() {
        final List<HistoryItem> list = new ArrayList<>();

        final HistoryItem item = new HistoryItem();
        item.setDate(Calendar.getInstance().getTime());
        item.setClose(new BigDecimal("10"));
        list.add(item);

        return list;
    }
}
