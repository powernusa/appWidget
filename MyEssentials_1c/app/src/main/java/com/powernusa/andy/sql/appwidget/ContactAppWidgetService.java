package com.powernusa.andy.sql.appwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Andy on 2/6/2017.
 */

public class ContactAppWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ContactAppWidgetFactory(getApplicationContext(),intent);
    }
}
