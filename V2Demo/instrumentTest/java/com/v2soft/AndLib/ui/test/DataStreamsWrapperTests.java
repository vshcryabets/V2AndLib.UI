package com.v2soft.AndLib.ui.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidDataStreamWrapper;
import com.v2soft.AndLib.dataproviders.Cancelable;
import com.v2soft.AndLib.dataproviders.DataStreamWrapper;
import com.v2soft.V2AndLib.demoapp.providers.DemoListProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        TestOutputStream output = new TestOutputStream();
        try {
            wrapper.copyToOutputStream(output, null, output);
            assertTrue("PCopy process wasn't interrupted", false);
        } catch (InterruptedException e) {}
        wrapper.close();
        assertTrue("Cancelation doesn't works counter=" + output.getCounter(), output.getCounter() < 20000);
        assertTrue("Cancelation doesn't works counter="+output.getCounter(), output.getCounter() > 10000);
    }

    private class TestOutputStream extends OutputStream implements Cancelable {
        private long mCounter = 0;
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
            return mCounter > 10000;
        }
        public long getCounter(){return mCounter;}
    }
}
