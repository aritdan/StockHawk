package com.udacity.stockhawk.validate;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.constant.ErrorCodeConstants;

import timber.log.Timber;

/**
 * Created by dan.ariton on 12-May-17.
 */

public class ValidateStockLoaderCallbacks implements LoaderManager.LoaderCallbacks<Integer> {

    private static final String TAG = ValidateStockLoaderCallbacks.class.getName();
    public static final String BUNDLE_STOCK_NAME = "stockName";

    private String stockName;

    private Context context;
    private ValidationFinishedListener validationFinishedListener;

    public ValidateStockLoaderCallbacks(final Context context, final ValidationFinishedListener validationFinishedListener) {
        Log.d(TAG, "CREATE ValidateStockLoaderCallbacks()");
        this.validationFinishedListener = validationFinishedListener;
        this.context = context;
    }



    @Override
    public Loader<Integer> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "ENTER onCreateLoader() id=[" + id + "], args=[" + args + "]");
        stockName = args.getString(BUNDLE_STOCK_NAME);
        final ValidateStockLoader validateStockLoader = new ValidateStockLoader(context, stockName);
        Log.d(TAG, "EXIT onCreateLoader()");
        return validateStockLoader;
    }

    @Override
    public void onLoadFinished(Loader<Integer> loader, Integer data) {
        Log.d(TAG, "ENTER onLoadFinished()");
        int errorCode = -1;
        if (data != null && !data.equals(ErrorCodeConstants.ERR_CODE_NO_ERR)) {
            errorCode = data.intValue();
        }
        validationFinishedListener.onValidationFinished(errorCode);
        Log.d(TAG, "EXIT onLoadFinished()");
    }

    @Override
    public void onLoaderReset(Loader<Integer> loader) {

    }
}