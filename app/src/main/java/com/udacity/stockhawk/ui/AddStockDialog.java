package com.udacity.stockhawk.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.constant.ErrorCodeConstants;
import com.udacity.stockhawk.validate.ValidateStockLoaderCallbacks;
import com.udacity.stockhawk.validate.ValidationFinishedListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddStockDialog extends DialogFragment implements ValidationFinishedListener {

    private static final int VALIDATION_LOADER = 100;

    private static final String TAG = AddStockDialog.class.getName();

    private Context context;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.dialog_stock)
    EditText stock;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") View custom = inflater.inflate(R.layout.add_stock_dialog, null);

        ButterKnife.bind(this, custom);

        stock.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                tryAddStock();
                return true;
            }
        });
        builder.setView(custom);

        builder.setMessage(getString(R.string.dialog_title));
        builder.setPositiveButton(getString(R.string.dialog_add), null);
        builder.setNegativeButton(getString(R.string.dialog_cancel), null);
        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button addButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tryAddStock();
                    }
                });
            }
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        return dialog;
    }

    private void tryAddStock() {
        final String stockName = stock.getText().toString();
        final Bundle bundle = new Bundle();
        bundle.putString(ValidateStockLoaderCallbacks.BUNDLE_STOCK_NAME, stockName);
        final LoaderManager loaderManager = ((AppCompatActivity) getActivity()).getSupportLoaderManager();
        final Loader loader = loaderManager.getLoader(VALIDATION_LOADER);
        if (loader == null) {
            loaderManager.initLoader(VALIDATION_LOADER, bundle, new ValidateStockLoaderCallbacks(getActivity(), this));
        } else {
            loaderManager.restartLoader(VALIDATION_LOADER, bundle, new ValidateStockLoaderCallbacks(getActivity(), this));
        }
    }

    private void addStock() {
        Activity parent = (Activity) context;
        if (parent instanceof MainActivity) {
            ((MainActivity) parent).addStock(stock.getText().toString());
        }
        dismissAllowingStateLoss();
    }

    @Override
    public void onValidationFinished(final int errorCode) {
        Log.d(TAG, "ENTER onValidationFinished() errorCode=[" + errorCode + "]");
        if (errorCode != -1) {
            String message = context.getString(R.string.error_add_stock);
            final String stockName = stock.getText().toString();
            switch (errorCode) {
                case ErrorCodeConstants.ERR_CODE_STOCK_ALREADY_ADDED:
                    message = context.getString(R.string.error_stock_already_added, stockName);
                    break;
                case ErrorCodeConstants.ERR_CODE_STOCK_NOT_EXIST:
                    message = context.getString(R.string.error_stock_not_exist, stockName);
                    break;
                case ErrorCodeConstants.ERR_CODE_SYMBOL_NOT_ALPHANUMERIC:
                    message = context.getString(R.string.error_symbol_not_alphanumeric, stockName);
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        } else {
            addStock();
        }
        Log.d(TAG, "EXIT onValidationFinished()");
    }



}
