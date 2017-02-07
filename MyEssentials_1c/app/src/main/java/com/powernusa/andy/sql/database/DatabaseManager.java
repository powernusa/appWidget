package com.powernusa.andy.sql.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.powernusa.andy.sql.object.ContactModel;

import java.util.ArrayList;

/**
 * Created by Andy on 1/21/2017.
 */

public class DatabaseManager {
    private SQLiteDatabase db; // a reference to the database manager class.
    private static final String DB_NAME = "contact.db"; // the name of our database
    private static final int DB_VERSION = 1; // the version of the database

    private static final String TABLE_NAME = "contact_table";// table name

    // the names for our database columns
    private static final String TABLE_ROW_ID = "_id";
    private static final String TABLE_ROW_NAME = "contact_name";
    private static final String TABLE_ROW_PHONENUM = "contact_number";
    private static final String TABLE_ROW_EMAIL = "contact_email";
    private static final String TABLE_ROW_PHOTOID = "photo_id";
    private Context context;

    public DatabaseManager(Context context) {
        this.context = context;

        // create or open the database
        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        this.db = helper.getWritableDatabase();
    }

    public Cursor getAllCursor(String[] projection, String selection,
                               String[] selectionArgs, String sortOrder) {

        Cursor cr = null;
        try {
            cr = db.query(TABLE_NAME, projection, selection, selectionArgs,
                    null, null, sortOrder);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return cr;
    }

    public long addRow(ContentValues values) {
        long id = -1;
        // ask the database object to insert the new data
        try {
            id = db.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString()); // prints the error message to
            // the log
            e.printStackTrace(); // prints the stack trace to the log
        }
        return id;
    }


    private ContentValues prepareData(ContactModel contactObj) {

        ContentValues values = new ContentValues();
        values.put(TABLE_ROW_NAME, contactObj.getName());
        values.put(TABLE_ROW_PHONENUM, contactObj.getContactNo());
        values.put(TABLE_ROW_EMAIL, contactObj.getEmail());
        values.put(TABLE_ROW_PHOTOID, contactObj.getPhoto());
        return values;
    }

    // Returns row data in form of ContactModel object
    public ContactModel getRowAsObject(int rowID) {

        ContactModel rowContactObj = new ContactModel();
        Cursor cursor;

        try {

            cursor = db.query(TABLE_NAME, new String[]{TABLE_ROW_ID,
                            TABLE_ROW_NAME, TABLE_ROW_PHONENUM, TABLE_ROW_EMAIL,
                            TABLE_ROW_PHOTOID}, TABLE_ROW_ID + "=" + rowID, null,
                    null, null, null, null);

            cursor.moveToFirst();
            prepareSendObject(rowContactObj, cursor);

        } catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return rowContactObj;
    }


    // Returns all the rows data in form of ContactModel object list

    public ArrayList<ContactModel> getAllData() {

        ArrayList<ContactModel> allRowsObj = new ArrayList<ContactModel>();
        Cursor cursor;
        ContactModel rowContactObj;

        String[] columns = new String[]{TABLE_ROW_ID, TABLE_ROW_NAME,
                TABLE_ROW_PHONENUM, TABLE_ROW_EMAIL, TABLE_ROW_PHOTOID};

        try {

            cursor = db
                    .query(TABLE_NAME, columns, null, null, null, null, null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()) {
                do {
                    rowContactObj = new ContactModel();
                    rowContactObj.setId(cursor.getInt(0));
                    prepareSendObject(rowContactObj, cursor);
                    allRowsObj.add(rowContactObj);

                } while (cursor.moveToNext()); // try to move the cursor's
                // pointer forward one position.
            }
        } catch (SQLException e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return allRowsObj;

    }


    private void prepareSendObject(ContactModel rowObj, Cursor cursor) {
        rowObj.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_ROW_ID)));
        rowObj.setName(cursor.getString(cursor
                .getColumnIndexOrThrow(TABLE_ROW_NAME)));
        rowObj.setContactNo(cursor.getString(cursor
                .getColumnIndexOrThrow(TABLE_ROW_PHONENUM)));
        rowObj.setEmail(cursor.getString(cursor
                .getColumnIndexOrThrow(TABLE_ROW_EMAIL)));
        rowObj.setPhoto(cursor.getBlob(cursor
                .getColumnIndexOrThrow(TABLE_ROW_PHOTOID)));
    }

    public void deleteRow(int rowID) {
        // ask the database manager to delete the row of given id
        try {
            db.delete(TABLE_NAME, TABLE_ROW_ID + "=" + rowID, null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }
    public int deleteRow(String whereClause, String[] whereArgs) {

        int count = 0;

        // ask the database manager to delete the row of given id
        try {
            count = db.delete(TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return count;
    }

    public int updateRow(ContentValues values,String whereClause, String[] whereArgs){
        int count = 0;
        try {
            count = db.update(TABLE_NAME, values, whereClause, whereArgs);
        }
        catch (SQLException e){
            Log.e("DB ERROR",e.toString());
            e.printStackTrace();
        }
        return count;
    }

    public void updateRow(int rowId, ContactModel contactObj) {

        ContentValues values = prepareData(contactObj);

        String whereClause = TABLE_ROW_ID + "=?";
        String whereArgs[] = new String[]{String.valueOf(rowId)};

        db.update(TABLE_NAME, values, whereClause, whereArgs);

    }

    // the beginnings our SQLiteOpenHelper class
    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper {

        public CustomSQLiteOpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // the SQLite query string that will create our column database
            // table.
            String newTableQueryString = "create table " + TABLE_NAME + " ("
                    + TABLE_ROW_ID
                    + " integer primary key autoincrement not null,"
                    + TABLE_ROW_NAME + " text not null," + TABLE_ROW_PHONENUM
                    + " text not null," + TABLE_ROW_EMAIL + " text not null,"
                    + TABLE_ROW_PHOTOID + " BLOB" + ");";

            // execute the query string to the database.
            db.execSQL(newTableQueryString);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // LATER, WE WOULD SPECIFIY HOW TO UPGRADE THE DATABASE
            // FROM OLDER VERSIONS.
            String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
            db.execSQL(DROP_TABLE);
            onCreate(db);

        }

    }

}
