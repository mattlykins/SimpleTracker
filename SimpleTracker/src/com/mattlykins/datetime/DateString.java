package com.mattlykins.datetime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

public class DateString {

    private static final String TAG = "DateString";
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public Date stringToDate(String sDate) {
        Date mDate = null;
        try {
            mDate = format.parse(sDate);
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return mDate;
    }

    public String dateToString(Date dDate) {
        String mString = null;
        try {
            mString = format.format(dDate);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, Log.getStackTraceString(e));
        }
        
        return mString;
    }

}
