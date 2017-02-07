package com.powernusa.andy.sql.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.powernusa.andy.sql.database.DatabaseConstants;
import com.powernusa.andy.sql.database.DatabaseManager;

/**
 * Created by Andy on 1/22/2017.
 */

public class PersonalContactProvider extends ContentProvider {
    public static final int CONTACTS_TABLE = 1001;
    public static final int CONTACTS_TABLE_ITEM = 1002;
    public static final String LOG_TAG = PersonalContactProvider.class.getSimpleName();

    public static UriMatcher mmUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        mmUriMatcher.addURI(PersonalContactContract.AUTHORITY,
                PersonalContactContract.BASE_PATH,CONTACTS_TABLE);
        mmUriMatcher.addURI(PersonalContactContract.AUTHORITY,
                PersonalContactContract.BASE_PATH + "/#",CONTACTS_TABLE_ITEM);
    }

    private DatabaseManager mDb;
    @Override
    public boolean onCreate() {
        mDb = new DatabaseManager(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        int uriType = mmUriMatcher.match(uri);

        switch (uriType) {
            case CONTACTS_TABLE:
                break;
            case CONTACTS_TABLE_ITEM:
                if (TextUtils.isEmpty(selection)) {
                    selection = DatabaseConstants.TABLE_ROW_ID + "="
                            + uri.getLastPathSegment();
                } else {
                    selection = DatabaseConstants.TABLE_ROW_ID + "="
                            + uri.getLastPathSegment() + " and " + selection;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cr = mDb.getAllCursor(projection, selection, selectionArgs,
                sortOrder);
        cr.setNotificationUri(getContext().getContentResolver(), uri);
        return cr;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = mmUriMatcher.match(uri);
        long id = -1;
        switch (uriType){
            case CONTACTS_TABLE:
                id = mDb.addRow(values);
                break;
            default:
                throw new IllegalArgumentException("Unkown URI: " + uri);
        }
        Uri ur = ContentUris.withAppendedId(uri,id);
        Log.d(LOG_TAG,"Inserted Uri: " + ur.toString());
        getContext().getContentResolver().notifyChange(uri,null);
        return ur;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = mmUriMatcher.match(uri);
        switch (uriType){
            case CONTACTS_TABLE:
                break;
            case CONTACTS_TABLE_ITEM:
                if(TextUtils.isEmpty(selection)){
                    selection = DatabaseConstants.TABLE_ROW_ID + "="
                            + uri.getLastPathSegment();
                }
                else{

                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        int count = mDb.deleteRow(selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = mmUriMatcher.match(uri);
        switch (uriType){
            case CONTACTS_TABLE:
                break;
            case CONTACTS_TABLE_ITEM:
                if(TextUtils.isEmpty(selection)){
                    selection = DatabaseConstants.TABLE_ROW_ID + "="
                            + uri.getLastPathSegment();
                }
                else{

                }
                break;
            default:
                throw new IllegalArgumentException("UNKNOW URI: " + uri);
        }
        int count = mDb.updateRow(values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
}
