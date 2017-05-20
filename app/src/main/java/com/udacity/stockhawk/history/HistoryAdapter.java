package com.udacity.stockhawk.history;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.history.pojo.HistoryItem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by dan.ariton on 17-May-17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private static final SimpleDateFormat DEFAULT_DATE_FORMAT =
            new SimpleDateFormat("dd.MM.yyyy");

    private static final DecimalFormat DEFAULT_DECIMAL_FORMAT =
            new DecimalFormat();

    static {
        DEFAULT_DECIMAL_FORMAT.setMinimumFractionDigits(2);
        DEFAULT_DECIMAL_FORMAT.setMaximumFractionDigits(2);
        DEFAULT_DECIMAL_FORMAT.setPositivePrefix("$");
        DEFAULT_DECIMAL_FORMAT.setNegativePrefix("$");
    }

    private List<HistoryItem> historyItemList = new ArrayList<>();

    public void setHistoryItemList(final List<HistoryItem> historyItemList) {
        this.historyItemList = historyItemList;
        notifyDataSetChanged();
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.d("ENTER onCreateViewHolder()");
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_history_item, parent, false);

        final HistoryViewHolder historyViewHolder = new HistoryViewHolder(view);

        Timber.d("EXIT onCreateViewHolder()");
        return historyViewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        Timber.d("ENTER onBindViewHolder() position=[%s]", position);

        final String histDate = DEFAULT_DATE_FORMAT.format(historyItemList.get(position).getDate());
        Timber.d("histDate=[%s]", histDate);
        holder.histDateTextView.setText(histDate);

        final String histClose = DEFAULT_DECIMAL_FORMAT.format(historyItemList.get(position).getClose());
        Timber.d("histClose=[%s]", histClose);
        holder.histCloseTextView.setText(histClose);

        Timber.d("EXIT onBindViewHolder()");
    }

    @Override
    public int getItemCount() {
        return historyItemList != null ? historyItemList.size() : 0;
    }

    protected class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_hist_date)
        protected TextView histDateTextView;

        @BindView(R.id.tv_hist_close)
        protected TextView histCloseTextView;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            Timber.d("CREATE HistoryViewHolder");
            ButterKnife.bind(this, itemView);
        }


    }
}
