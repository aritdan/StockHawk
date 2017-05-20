package com.udacity.stockhawk.history.pojo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by dan.ariton on 17-May-17.
 */

public class HistoryItem {

    private Date date;
    private BigDecimal close;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    @Override
    public String toString() {
        return "HistoryItem{" +
                "date=" + date +
                ", close=" + close +
                '}';
    }
}
