package com.mattlykins.database;

import android.content.Context;
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
    
    
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " (" 
            + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_POINTS + " text not null, "
            + KEY_DATE_TIME + " text not null);";
    
    
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private final Context mContext;
    
    public SimpleTrackerDbAdapter(Context mContext){
        this.mContext = mContext;
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper{

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
    
    public SimpleTrackerDbAdapter open() throws SQLException{
        mDbHelper = new DatabaseHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close(){
        mDbHelper.close();
    }
    

}
