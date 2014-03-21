package com.v2soft.V2AndLib.demoapp.providers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;

import com.v2soft.AndLib.dataproviders.AndroidStreamHelper;
import com.v2soft.AndLib.streams.StreamHelper;
import com.v2soft.AndLib.filecache.AndroidFileCache;
import com.v2soft.V2AndLib.demoapp.database.DemoDataItem;
import com.v2soft.V2AndLib.demoapp.database.DemoDatabaseHelper;

/**
 * Sample data provider.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public class DemoListProvider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.v2soft.V2AndLib.demoapp.providers.DemoListProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME + "/items");
    public static final Uri CONTENT_INSERT_URI = Uri.parse("content://"+ PROVIDER_NAME + "/insert10Items");
    private UriMatcher mUriMatcher;
    private static final int FEED_ITEMS = 1;
    private static final int FEED_INSERT = 2;
    public static final String METHOD_CLEAN_DB = "clean";
    private DemoDatabaseHelper mDatabase;
	private AndroidFileCache mCache;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mDatabase.getWritableDatabase().delete(DemoDatabaseHelper.TABLE_DEMO_ITEMS, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)){
        case FEED_ITEMS:
            return "vnd.android.cursor.dir/com.v2soft.V2AndLib.demoapp.providers.items";
        case FEED_INSERT:
            return "vnd.android.cursor.item/com.v2soft.V2AndLib.demoapp.providers.action";
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = mUriMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        long id = 0;
        switch (uriType) {
        case FEED_ITEMS:
            id = sqlDB.insertWithOnConflict(DemoDatabaseHelper.TABLE_DEMO_ITEMS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse("items/" + id);
    }

    @Override
    public boolean onCreate() {
        mDatabase = new DemoDatabaseHelper(getContext());
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(PROVIDER_NAME, "items", FEED_ITEMS);
        mUriMatcher.addURI(PROVIDER_NAME, "insert10Items", FEED_INSERT);
		mCache = new AndroidFileCache.Builder(getContext()).useExternalCacheFolder("test").build();
		return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        // prepare query
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DemoDatabaseHelper.TABLE_DEMO_ITEMS);

        int uriType = mUriMatcher.match(uri);
        switch (uriType) {
        case FEED_ITEMS:
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int uriType = mUriMatcher.match(uri);
        switch (uriType) {
        case FEED_INSERT:
            int maxCount = 10;
            ContentValues [] preparedArray = new ContentValues[maxCount];
            for ( int i = 0 ;i < maxCount; i++ ) {
                preparedArray[i] = new ContentValues();
                preparedArray[i].put(DemoDataItem.FIELD_TITLE, UUID.randomUUID().toString());
                preparedArray[i].put(DemoDataItem.FIELD_PUBLISH_DATE, new Date().getTime());
            }
            bulkInsert(CONTENT_URI, preparedArray);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int uriType = mUriMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        switch (uriType) {
        case FEED_ITEMS:
            sqlDB.beginTransaction();
            for (ContentValues contentValues : values) {
                sqlDB.insertWithOnConflict(DemoDatabaseHelper.TABLE_DEMO_ITEMS, null, contentValues, SQLiteDatabase.CONFLICT_FAIL);
            }
            sqlDB.setTransactionSuccessful();
            sqlDB.endTransaction();
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return values.length;
    }

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		return this.openFile(uri, mode, null);
	}

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode, CancellationSignal signal) throws FileNotFoundException {
		try {
			File file = mCache.getFileByUri(uri);
			if ( !mCache.isInCache(uri) ) {
				StreamHelper stream = AndroidStreamHelper.getStream(getContext(),
                        URI.create("file:///android_asset/data.jpg"));
				stream.copyToOutputStream(new FileOutputStream(file));
				stream.close();
			}
			ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
			return parcel;
		} catch (NoSuchAlgorithmException e) {
		} catch (IOException e) {
		} catch (InterruptedException e) {
        }
        return null;
	}

	@Override
	public AssetFileDescriptor openAssetFile(Uri uri, String mode, CancellationSignal signal) throws FileNotFoundException {
		return super.openAssetFile(uri, mode, signal);
	}
}
