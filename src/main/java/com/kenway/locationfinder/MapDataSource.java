package com.kenway.locationfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MapDataSource {



    private SQLiteDatabase mDatabase;       // The actual DB!
    private MapHelper mMapHelper; // Helper class for creating and opening the DB
    private Context mContext;
    private static String DB_PATH = "/data/data/com.javapapers.currentlocationinmap/databases/";
    private static String DB_NAME = "location.db";

    public MapDataSource(Context context) {
        //mContext = context;
        mMapHelper = new MapHelper(mContext);
        this.mContext = context;
        this.createDataBase();
    }

    /*
     * Open the db. Will create if it doesn't exist
     */
//    public void open() throws SQLException {
//        String myPath = DB_PATH + MapHelper.TABLE_LOCATION;
//        mDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
//        //mDatabase = mMapHelper.getWritableDatabase();
//    }

    /*
     * We always need to close our db connections
     */
    public void close() {
        mDatabase.close();
    }

    /*
     * CRUD operations!
     */

    /*
     * INSERT
     */
    public void insertLocation(double latitude, double longitude) {
        mDatabase.beginTransaction();

        try {

            ContentValues values = new ContentValues();
            values.put("LATITUDE",Double.toString(latitude));
            values.put("LONGITUDE", Double.toString(longitude)); // GPS location
            mDatabase.insert(MapHelper.TABLE_LOCATION, null, values);

            mDatabase.setTransactionSuccessful();
        }
        finally {
            mDatabase.endTransaction();
        }
    }

    /*
     * SELECT ALL
     */
    public Cursor selectAllLocations() {
        Cursor cursor = mDatabase.query(
                MapHelper.TABLE_LOCATION, // table
                new String[] { MapHelper.COLUMN_LAT, MapHelper.COLUMN_LONG }, // column names
                null, // where clause
                null, // where params
                null, // groupby
                null, // having
                null  // orderby
        );
        cursor.moveToFirst(); //added

        return cursor;
    }


    /*
     * DELETE
     */
    public void deleteAll() {
        mDatabase.delete(
                MapHelper.TABLE_LOCATION, // table
                null, // where clause
                null  // where params
        );
    }










    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase(){
        try {
            boolean dbExist = checkDataBase();

            if(dbExist){
                //do nothing - database already exist
            }else{
                //By calling this method and empty database will be created into the default system path
                //of your application so we are gonna be able to overwrite that database with our database.
                mMapHelper.getWritableDatabase();


                copyDataBase();

            }
        }
        catch (Exception e) {

        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase(){

        try{
            //Open your local db as the input stream
            InputStream myInput = mContext.getAssets().open(DB_NAME);

            // Path to the just created empty db
            String outFileName = DB_PATH + DB_NAME;

            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (Exception e) {
            //catch exception
        }
    }

    public SQLiteDatabase openDataBase() throws SQLException{

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        mDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        return mDatabase;

    }

//
//    @Override
//    public synchronized void close() {
//
//        if(mDatabase != null)
//        { mDatabase.close();}
//
//        //super.close();
//
//    }
}
