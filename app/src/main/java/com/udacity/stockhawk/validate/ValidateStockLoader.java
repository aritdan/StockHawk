package com.udacity.stockhawk.validate;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContentResolverCompat;

import com.udacity.stockhawk.constant.ErrorCodeConstants;
import com.udacity.stockhawk.data.Contract;

import java.io.IOException;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * Created by dan.ariton on 12-May-17.
 */

public class ValidateStockLoader extends AsyncTaskLoader<Integer> {


    private String stockName;

    public ValidateStockLoader(final Context context, final String stockName) {
        super(context);
        this.stockName = stockName;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Integer loadInBackground() {

        final Cursor cursor = ContentResolverCompat.query(getContext().getContentResolver(),
                Contract.Quote.URI.buildUpon().appendPath(stockName).build(),
                null, null, null,
                Contract.Quote.COLUMN_SYMBOL, null);

        if (cursor.getCount() > 0) {
            return ErrorCodeConstants.ERR_CODE_STOCK_ALREADY_ADDED;
        }

        try {
            final Stock stock = YahooFinance.get(stockName.toUpperCase());
            if (stock.getQuote().getPrice() == null) {
                return ErrorCodeConstants.ERR_CODE_STOCK_NOT_EXIST;
            }
        } catch (IOException e) {
            return ErrorCodeConstants.ERR_GENERIC;
        }

        return -1;
    }
}