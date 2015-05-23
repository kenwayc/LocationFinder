package com.kenway.locationfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MapHelper extends SQLiteOpenHelper {

    /*
     * Table and column information
     */
    public static final String TABLE_LOCATION = "LOCATION";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_LAT = "LATITUDE";
    public static final String COLUMN_LONG = "LONGITUDE";

    /*
     * Database information
     */
    private static final String DB_NAME = "location.db";
    private static final int DB_VERSION = 1; // Must increment to trigger an upgrade
    private static final String DB_CREATE =
            "CREATE TABLE " + TABLE_LOCATION + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LAT + " DOUBLE, " +
                    COLUMN_LONG + " DOUBLE)";

    private static final String DB_DROP =
            "DROP TABLE " + TABLE_LOCATION;

    public MapHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    /*
     * This is triggered by incrementing DB_VERSION
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DB_DROP);/**/
        db.execSQL(DB_CREATE);
    }
}
