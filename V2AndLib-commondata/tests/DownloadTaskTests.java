import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.tasks.DownloadTask;
import com.v2soft.AndLib.filecache.FileCache;
import com.v2soft.AndLib.streams.StreamHelper;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class DownloadTaskTests  {
    private Random mRandom = new Random();

    @Before
    public void setUp() {
    }

    @Test
    public void testDownloadSpeedLimit() throws IOException, NoSuchAlgorithmException, AbstractDataRequestException {
        URI source = URI.create(DataStreamsWrapperTests.HTTP_FILE);
        StreamHelper helper = StreamHelper.getStream(source);
        long sourceSize = helper.getAvaiableDataSize();
        helper.close();
        long speedLimit = 1024*(mRandom.nextInt(20)+5);// random speed between 5-25 kb/s

        FileCache cache = new FileCache.Builder().setCacheFolder(new File("./cache/")).build();
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
