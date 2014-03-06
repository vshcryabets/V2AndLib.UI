package com.v2soft.AndLib.ui.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidDataStreamWrapper;
import com.v2soft.AndLib.dataproviders.Cancelable;
import com.v2soft.AndLib.dataproviders.DataStreamWrapper;
import com.v2soft.AndLib.streams.SpeedControlInputStream;
import com.v2soft.AndLib.streams.SpeedControlOutputStream;
import com.v2soft.AndLib.streams.ZeroInputStream;
import com.v2soft.V2AndLib.demoapp.providers.DemoListProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class DataStreamsWrapperTests extends AndroidTestCase {
    public static final String ASSET_FILE_PATH = "file:///android_asset/test.mp3";
    public static final URI ASSETS_FILE = URI.create(ASSET_FILE_PATH);

    @SmallTest
    public void testOpenFile() throws URISyntaxException, IOException, InterruptedException {
        DataStreamWrapper wrapper = AndroidDataStreamWrapper.getStream(getContext(), ASSETS_FILE);
        File tempOutFile = File.createTempFile("tmp", "", getContext().getExternalCacheDir());
        FileOutputStream output = new FileOutputStream(tempOutFile);
        wrapper.copyToOutputStream(output).close();

        wrapper = AndroidDataStreamWrapper.getStream(getContext(), new URI("file", "",
                tempOutFile.getAbsolutePath(), ""));
        assertNotNull(wrapper);
        assertNotNull(wrapper.getInputStream());
        assertEquals(1024, wrapper.getAvaiableDataSize());
    }
    @SmallTest
    public void testOpenContent() throws URISyntaxException, IOException {
        DataStreamWrapper wrapper = AndroidDataStreamWrapper.getStream(getContext(),
                new URI("content://"+ DemoListProvider.PROVIDER_NAME+"/data.jpg"));
        assertNotNull(wrapper);
        assertNotNull(wrapper.getInputStream());
        assertEquals(76367, wrapper.getAvaiableDataSize());
    }
    @SmallTest
    public void testOpenAssets() throws URISyntaxException, IOException {
        DataStreamWrapper wrapper = AndroidDataStreamWrapper.getStream(getContext(),
                new URI("file:///android_asset/BT139_SERIES.pdf"));
        assertNotNull(wrapper);
        assertNotNull(wrapper.getInputStream());
        assertEquals(Long.MIN_VALUE, wrapper.getAvaiableDataSize());
        wrapper.close();
        wrapper = AndroidDataStreamWrapper.getStream(getContext(),
                new URI("file:///android_asset/test.mp3"));
        assertNotNull(wrapper);
        assertNotNull(wrapper.getInputStream());
        assertEquals(1024, wrapper.getAvaiableDataSize());
        wrapper.close();
    }
    @SmallTest
    public void testOpenHTTP() throws URISyntaxException, IOException {
        DataStreamWrapper wrapper = AndroidDataStreamWrapper.getStream(getContext(),
                new URI("https://dl.dropboxusercontent.com/u/18391781/Datasheets/STK500%20Protocol.pdf"));
        assertNotNull(wrapper);
        assertNotNull(wrapper.getInputStream());
        assertEquals(161607, wrapper.getAvaiableDataSize());
        wrapper.close();
    }
    @SmallTest
    public void testCancelation() throws IOException {
        DataStreamWrapper wrapper = AndroidDataStreamWrapper.getStream(getContext(),
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
    @SmallTest
    public void testInputSpeedControl() throws IOException {
        int speedLimit = 50*1024;
        int sizeLimit = 1024*1024;
        SpeedControlInputStream stream = new SpeedControlInputStream(new ZeroInputStream(), speedLimit);
        DataStreamWrapper wrapper = new DataStreamWrapper(stream, Long.MIN_VALUE);
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
    @SmallTest
    public void testOutputSpeedControl() throws IOException {
        int speedLimit = 50*1024;
        int sizeLimit = 1024*1024;
        InputStream input = new ZeroInputStream();
        TestOutputStream output = new TestOutputStream(sizeLimit);
        SpeedControlOutputStream stream = new SpeedControlOutputStream(output, speedLimit);
        DataStreamWrapper wrapper = new DataStreamWrapper(input, Long.MIN_VALUE);
        long startTime = System.currentTimeMillis();
        try {
            wrapper.copyToOutputStream(stream, null, output);
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
}
