package com.powernusa.andy.sql.provider;

import android.content.ContentResolver;
import android.net.Uri;

import com.powernusa.andy.sql.database.DatabaseConstants;

/**
 * Created by Andy on 1/22/2017.
 */

public final class PersonalContactContract implements DatabaseConstants{
    public static final String AUTHORITY = "com.powernusa.andy.sql.provider";
    public static final String BASE_PATH = "path_contacts";

    public static final Uri CONTENT_URI = Uri.parse("content://"
                                                    + AUTHORITY + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.com.powernusa.andy.sql.provider.table";

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.com.powernusa.andy.sql.provider.table_item";

    public static final String[] PROJECTION_ALL = {"_id",
                                                    "contact_name",
                                                    "contact_number",
                                                    "contact_email",
                                                    "photo_id"
                                                    };

}
