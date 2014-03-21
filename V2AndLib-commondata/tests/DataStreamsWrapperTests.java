import com.v2soft.AndLib.dataproviders.Cancelable;
import com.v2soft.AndLib.streams.SpeedControlInputStream;
import com.v2soft.AndLib.streams.SpeedControlOutputStream;
import com.v2soft.AndLib.streams.StreamHelper;
import com.v2soft.AndLib.streams.ZeroInputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class DataStreamsWrapperTests  {
    public static final String HTTP_FILE = "https://dl.dropboxusercontent.com/u/18391781/Datasheets/STK500%20Protocol.pdf";

    @Before
    public void setUp() {
    }

    @Test
    public void testOpenHTTP() throws URISyntaxException, IOException {
        StreamHelper wrapper = StreamHelper.getStream(
                new URI(HTTP_FILE));
        assertNotNull(wrapper);
        assertNotNull(wrapper.getInputStream());
        assertEquals(161607, wrapper.getAvaiableDataSize());
        wrapper.close();
    }
    @Test
    public void testCancelation() throws IOException {
        StreamHelper wrapper = StreamHelper.getStream(
                URI.create("https://dl.dropboxusercontent.com/u/18391781/Datasheets/STK500%20Protocol.pdf"));
        assertNotNull(wrapper);
        assertNotNull(wrapper.getInputStream());
        assertEquals(161607, wrapper.getAvaiableDataSize());
        TestOutputStream output = new TestOutputStream(10000);
        try {
            wrapper.copyToOutputStream(output, null, output);
            assertTrue("Copy process wasn't interrupted", false);
        } catch (InterruptedException e) {}
        wrapper.close();
        assertTrue("Cancelation doesn't works counter=" + output.getCounter(), output.getCounter() < 20000);
        assertTrue("Cancelation doesn't works counter="+output.getCounter(), output.getCounter() > 10000);
    }
    @Test
    public void testInputSpeedControl() throws IOException {
        int speedLimit = 50*1024;
        int sizeLimit = 1024*1024;
        SpeedControlInputStream stream = new SpeedControlInputStream(new ZeroInputStream(), speedLimit);
        StreamHelper wrapper = new StreamHelper(stream, Long.MIN_VALUE);
        TestOutputStream output = new TestOutputStream(sizeLimit);
        long startTime = System.currentTimeMillis();
        try {
            wrapper.copyToOutputStream(output, null, output);
            assertTrue("Copy process wasn't interrupted", false);
        } catch (InterruptedException e) {}
        long endTime = System.currentTimeMillis();
        long time = (endTime-startTime)/1000;
        long expectedTime1 = sizeLimit/speedLimit;
        long expectedTime2 = sizeLimit/speedLimit*110/100;
        assertTrue("Speed limit doesn't affect", time >= expectedTime1);
        assertTrue("Speed limit doesn't affect", time <= expectedTime2);
        wrapper.close();
    }
    @Test
    public void testOutputSpeedControl() throws IOException {
        int speedLimit = 50*1024;
        int sizeLimit = 1024*1024;
        InputStream input = new ZeroInputStream();
        TestOutputStream output = new TestOutputStream(sizeLimit);
        SpeedControlOutputStream stream = new SpeedControlOutputStream(output, speedLimit);
        StreamHelper wrapper = new StreamHelper(input, Long.MIN_VALUE);
        long startTime = System.currentTimeMillis();
        try {
            wrapper.copyToOutputStream(stream, null, output);
            assertTrue("Copy process wasn't interrupted", false);
        } catch (InterruptedException e) {}
        long endTime = System.currentTimeMillis();
        long time = (endTime-startTime)/1000;
        long expectedTime1 = sizeLimit/speedLimit;
        long expectedTime2 = sizeLimit/speedLimit*110/100;
        assertTrue("Speed limit doesn't affect (was "+time+" expected > "+expectedTime1+")", time >= expectedTime1);
        assertTrue("Speed limit doesn't affect (was "+time+" expected < "+expectedTime2+")", time <= expectedTime2);
        wrapper.close();
    }
    private class TestOutputStream extends OutputStream implements Cancelable {
        private long mCounter = 0;
        private int mLimit;
        public TestOutputStream(int limit) {
            mLimit = limit;
        }
        @Override
        public void write(int oneByte) throws IOException {
            mCounter++;
        }
        @Override
        public void write(byte[] buffer) throws IOException {
            mCounter+=buffer.length;
        }
        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            mCounter += count;
        }
        @Override
        public void cancel() {}
        @Override
        public boolean canBeCanceled() {return true;}
        @Override
        public boolean isCanceled() {
            return mCounter > mLimit;
        }
        public long getCounter(){return mCounter;}
    }

    @Test
    public void testWrongURI() throws URISyntaxException, IOException {
        // unknown scheme
        try {
            StreamHelper.getStream(new URI("unknown://test.test/file1"));
            assertTrue("UnknownServiceException exception should happens there", false);
        } catch (UnknownServiceException ex) {
            assertTrue("Wrong exception "+ex.getMessage(), ex.getMessage().startsWith(StreamHelper.EXCEPTION_UNKNOWN_SCHEME));
        }
        // no scheme
        try {
            StreamHelper.getStream(new URI("/test.test/file1"));
        } catch (UnknownServiceException ex) {
            assertEquals("Wrong exception "+ex.getMessage(), StreamHelper.EXCEPTION_NO_DATA_SCHEME, ex.getMessage());
        }
    }
}
