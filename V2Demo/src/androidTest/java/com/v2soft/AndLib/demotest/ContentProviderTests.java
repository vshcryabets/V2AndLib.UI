package com.v2soft.AndLib.demotest;

import java.util.Date;
import java.util.UUID;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.v2soft.V2AndLib.demoapp.database.DemoDataItem;
import com.v2soft.V2AndLib.demoapp.providers.DemoListProvider;

/**
 * Unit tests for DemoContentProvider logic.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public class ContentProviderTests extends AndroidTestCase {
    private static final String LOG_TAG = ContentProviderTests.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test speed of single insert method and bulkInsert.
     * Latest results:
     * D/ContentProviderTests( 5624): Test raw insert for 10 items - duration 152
     * D/ContentProviderTests( 5624): Test bulk insert for 10 items - duration 79
     * D/ContentProviderTests( 5624): Test raw insert for 100 items - duration 1265
     * D/ContentProviderTests( 5624): Test bulk insert for 100 items - duration 116
     * D/ContentProviderTests( 5624): Test raw insert for 1000 items - duration 9352
     * D/ContentProviderTests( 5624): Test bulk insert for 1000 items - duration 1219
     * D/ContentProviderTests( 5624): Test raw insert for 10000 items - duration 91095
     * D/ContentProviderTests( 5624): Test bulk insert for 10000 items - duration 9487
     */
    @LargeTest
    public void testInsertSpeed() {
        int maxCount = 10000;
        ContentValues [] preparedArray = new ContentValues[maxCount];
        for ( int i = 0 ;i < maxCount; i++ ) {
            preparedArray[i] = new ContentValues();
            preparedArray[i].put(DemoDataItem.FIELD_TITLE, UUID.randomUUID().toString());
            preparedArray[i].put(DemoDataItem.FIELD_PUBLISH_DATE, new Date().getTime());
        }
        ContentResolver resolver = mContext.getContentResolver();
        int itemCounts[] = new int[]{10, 100, 1000, maxCount};
        for (int i : itemCounts) {
            clearDatabase();
            Cursor cursor = resolver.query(DemoListProvider.CONTENT_URI, new String[]{DemoDataItem.FIELD_ID}, null, null, null);
            assertEquals("Wrong records count clear database", 0, cursor.getCount());

            long startTime = System.currentTimeMillis();
            doSingleInsert(resolver, i, preparedArray);
            long endTime = System.currentTimeMillis();
            long diff = endTime-startTime;
            Log.d(LOG_TAG, "Test raw insert for "+i+" items - duration "+diff);
            cursor = resolver.query(DemoListProvider.CONTENT_URI, new String[]{DemoDataItem.FIELD_ID}, null, null, null);
            assertEquals("Wrong records count after raw insert", i, cursor.getCount());
            
            clearDatabase();
            cursor = resolver.query(DemoListProvider.CONTENT_URI, new String[]{DemoDataItem.FIELD_ID}, null, null, null);
            assertEquals("Wrong records count clear database", 0, cursor.getCount());
            
            ContentValues[] subArray = new ContentValues[i]; 
            System.arraycopy(preparedArray, 0, subArray, 0, i);
            startTime = System.currentTimeMillis();
            doBulkInsert(resolver, subArray);
            endTime = System.currentTimeMillis();
            diff = endTime-startTime;
            Log.d(LOG_TAG, "Test bulk insert for "+i+" items - duration "+diff);
            cursor = resolver.query(DemoListProvider.CONTENT_URI, new String[]{DemoDataItem.FIELD_ID}, null, null, null);
            assertEquals("Wrong records count after bulk insert", i, cursor.getCount());
            System.gc();
        }
        clearDatabase();
    }

    private void doBulkInsert(ContentResolver resolver, ContentValues[] subArray) {
        resolver.bulkInsert(DemoListProvider.CONTENT_URI, subArray);
    }

    private void clearDatabase() {
        getContext().getContentResolver().delete(DemoListProvider.CONTENT_URI, null, null);
    }

    private void doSingleInsert(ContentResolver resolver, int count, ContentValues[] preparedArray) {
        for ( int i = 0 ; i < count; i++  ) {
            resolver.insert(DemoListProvider.CONTENT_URI, preparedArray[i]);
        }
    }
}
