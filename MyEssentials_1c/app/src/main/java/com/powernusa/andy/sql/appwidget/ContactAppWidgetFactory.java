package com.powernusa.andy.sql.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.powernusa.andy.sql.R;
import com.powernusa.andy.sql.database.DatabaseConstants;
import com.powernusa.andy.sql.provider.PersonalContactContract;

/**
 * Created by Andy on 2/6/2017.
 */

public class ContactAppWidgetFactory implements RemoteViewsService.RemoteViewsFactory{
    private Context mContext;
    private Cursor mCursor;
    private int mWidgetId;

    public ContactAppWidgetFactory(Context context, Intent intent){
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if(mCursor!= null){
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(PersonalContactContract.CONTENT_URI,
                PersonalContactContract.PROJECTION_ALL,null,null,null);

    }

    @Override
    public void onDestroy() {
        if(mCursor!=null){
            mCursor.close();
        }

    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.contact_widget_list_content);
        if(mCursor.moveToPosition(position)){
            rv.setTextViewText(R.id.contact_name_tv,
                    mCursor.getString(mCursor.getColumnIndex(DatabaseConstants.TABLE_ROW_NAME)));
            rv.setTextViewText(R.id.contact_number_tv,
                    mCursor.getString(mCursor.getColumnIndex(DatabaseConstants.TABLE_ROW_PHONENUM)));
            rv.setTextViewText(R.id.contact_email_tv,
                    mCursor.getString(mCursor.getColumnIndex(DatabaseConstants.TABLE_ROW_EMAIL)));
        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {


        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
