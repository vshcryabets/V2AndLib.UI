package com.v2soft.V2AndLib.demoapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
* Demo data base open helper.
* @author Vladimir Shcryabets <vshcryabets@gmail.com>
*
*/
public class DemoDatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = DemoDatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "v2andlib.sqlite";
    private static final int DATABASE_VERSION = 2;
    // Database table
    public static final String TABLE_DEMO_ITEMS = "feeditems";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_DEMO_ITEMS
            + "("+DemoDataItem.FIELD_ID+" integer primary key autoincrement, "
            + DemoDataItem.FIELD_AUTHOR + " text, "
            + DemoDataItem.FIELD_DESCRIPTION + " text, "
            + DemoDataItem.FIELD_FULLTEXT + " text, "
            + DemoDataItem.FIELD_IMAGE + " text, "
            + DemoDataItem.FIELD_LINK + " text, "
            + DemoDataItem.FIELD_TITLE + " text not null, "
            + DemoDataItem.FIELD_PUBLISH_DATE + " integer not null"
            + ");";

    public DemoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEMO_ITEMS);
        onCreate(db);
    }

}
