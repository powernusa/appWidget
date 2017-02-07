package com.powernusa.andy.sql;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.powernusa.andy.sql.database.DatabaseConstants;
import com.powernusa.andy.sql.provider.PersonalContactContract;

/**
 * Created by Andy on 1/21/2017.
 */

public class CustomListAdapter extends CursorAdapter{
    private Context mContext;

    public CustomListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.contact_list_row,parent,false);
        return rootView;

    }

    public void deleteRow(int position){

        Cursor c = getCursor();   //get underlying cursor for CursorAdapter
        c.moveToPosition(position);
        int itemId = c.getInt(c.getColumnIndexOrThrow(DatabaseConstants.TABLE_ROW_ID));
        Toast.makeText(mContext,"Position Clicked: " + position
                + "Cursor Count: " + c.getCount()
                + "Item Id: " + itemId,Toast.LENGTH_LONG).show();
        Uri uri = Uri.parse(PersonalContactContract.CONTENT_URI + "/" + itemId);
        mContext.getContentResolver().delete(uri,null,null);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
/*
    public long getItemRowId(int position){
        Cursor c = getCursor();
        c.moveToPosition(position);

        return c.getLong(c.getColumnIndexOrThrow(DatabaseConstants.TABLE_ROW_ID));
    }
    */


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView photoView = (ImageView) view.findViewById(R.id.contact_photo);
        TextView contactNameTV = (TextView) view.findViewById(R.id.contact_name);
        TextView contactPhoneNumTV = (TextView) view.findViewById(R.id.contact_phone);
        TextView contactEmailTV = (TextView) view.findViewById(R.id.contact_email);

        contactNameTV.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TABLE_ROW_NAME)));
        contactPhoneNumTV.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TABLE_ROW_PHONENUM)));
        contactEmailTV.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TABLE_ROW_EMAIL)));

    }

}
