package com.mattlykins.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.AndroidException;

public class SimpleTrackerDbAdapter {
    private static final String DATABASE_NAME = "database";
    private static final String DATABASE_TABLE = "dbTable";
    private static final int DATABASE_VERSION = 1;

    public static final String KEY_ROWID = "_id";
    public static final String KEY_POINTS = "points";
    public static final String KEY_DATE_TIME = "date_time";

    private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " ("
            + KEY_ROWID + " integer primary key autoincrement, " + KEY_POINTS + " text not null, "
            + KEY_DATE_TIME + " text not null);";

    private static final String[] allRows = { KEY_ROWID, KEY_POINTS, KEY_DATE_TIME };

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mContext;

    public SimpleTrackerDbAdapter(Context mContext) {
        this.mContext = mContext;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub

        }

    }

    public SimpleTrackerDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long saveEntry(String points, String dateTime) {
        ContentValues cV = new ContentValues();
        cV.put(KEY_POINTS, points);
        cV.put(KEY_DATE_TIME, dateTime);

        return mDb.insert(DATABASE_TABLE, null, cV);
    }

    public boolean deleteEntry(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor getEntry(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE, allRows, KEY_ROWID + "=" + rowId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public int updateEntry(long rowId, String points, String dateTime){
        ContentValues cV = new ContentValues();
        cV.put(KEY_POINTS, points);
        cV.put(KEY_DATE_TIME, dateTime);
        
        return mDb.update(DATABASE_TABLE, cV, KEY_ROWID + "=" + rowId, null);
    }

}
