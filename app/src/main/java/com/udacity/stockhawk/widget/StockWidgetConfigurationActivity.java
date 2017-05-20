package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by dan.ariton on 19-May-17.
 */

public class StockWidgetConfigurationActivity extends AppCompatActivity
        implements View.OnClickListener {

    @BindView(R.id.sp_cfg_symbol)
    protected Spinner stockSelectSpinner;

    @BindView(R.id.btn_cfg_select)
    protected Button selectConfirmButton;

    private int widgetId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        ButterKnife.bind(this);

        final Intent intent = getIntent();
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        Timber.d("widgetId=[%s]", widgetId);

        if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            final List<String> stocks = new ArrayList<>(PrefUtils.getStocks(this));
            Collections.sort(stocks);

            final ArrayAdapter<String> stocksAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, stocks);

            stockSelectSpinner.setAdapter(stocksAdapter);

            selectConfirmButton.setOnClickListener(this);

            setResult(RESULT_CANCELED);

        }
    }

    @Override
    public void onClick(View v) {
        final String symbol = (String) stockSelectSpinner.getSelectedItem();
        Timber.d("symbol=[%s]", symbol);

        PrefUtils.addSymbolForWidgetId(widgetId, symbol, this);
        setResult(RESULT_OK);

        final Intent intent = new Intent(this, StockWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {widgetId});
        sendBroadcast(intent);

        finish();
    }
}
