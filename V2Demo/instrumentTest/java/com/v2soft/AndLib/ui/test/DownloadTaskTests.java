package com.v2soft.AndLib.ui.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.AndroidStreamHelper;
import com.v2soft.AndLib.dataproviders.Cancelable;
import com.v2soft.AndLib.dataproviders.tasks.DownloadTask;
import com.v2soft.AndLib.filecache.AndroidFileCache;
import com.v2soft.AndLib.filecache.FileCache;
import com.v2soft.AndLib.streams.SpeedControlInputStream;
import com.v2soft.AndLib.streams.SpeedControlOutputStream;
import com.v2soft.AndLib.streams.StreamHelper;
import com.v2soft.AndLib.streams.ZeroInputStream;
import com.v2soft.V2AndLib.demoapp.providers.DemoListProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class DownloadTaskTests extends AndroidTestCase {
    private Random mRandom = new Random();

    @SmallTest
    public void testDownloadSpeedLimit() throws IOException, NoSuchAlgorithmException, AbstractDataRequestException {
        URI source = URI.create(DataStreamsWrapperTests.HTTP_FILE);
        StreamHelper helper = StreamHelper.getStream(source);
        long sourceSize = helper.getAvaiableDataSize();
        helper.close();
        long speedLimit = 1024*(mRandom.nextInt(20)+5);// random speed between 5-25 kb/s

        FileCache cache = new AndroidFileCache.Builder(mContext).build();
        cache.clear();
        DownloadTask task = new DownloadTask(source, cache);
        task.setSpeedLimit((int) speedLimit);
        long startTime = System.currentTimeMillis();
        task.execute(null);
        long endTime = System.currentTimeMillis();
        long time = (endTime-startTime)/1000;
        long expectedTime1 = sourceSize/speedLimit;
        long expectedTime2 = expectedTime1*120/100; // +20%

        File file = cache.getFileByURI(source);
        assertNotNull("No file", file);
        assertTrue("File doesn't exists", file.exists());
        assertEquals("Wrong file size", sourceSize, file.length());

        assertTrue("Speed limit doesn't affect (was " + time + " expected > " + expectedTime1 + ")", time >= expectedTime1);
        assertTrue("Speed limit doesn't affect (was "+time+" expected < "+expectedTime2+")", time <= expectedTime2);
    }

}
