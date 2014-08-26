package com.v2soft.AndLib.demotest;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidStreamHelper;
import com.v2soft.AndLib.streams.StreamHelper;
import com.v2soft.V2AndLib.demoapp.providers.DemoListProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        StreamHelper wrapper = AndroidStreamHelper.getStream(getContext(), ASSETS_FILE);
        File tempOutFile = File.createTempFile("tmp", "", getContext().getExternalCacheDir());
        FileOutputStream output = new FileOutputStream(tempOutFile);
        wrapper.copyToOutputStream(output).close();

        wrapper = AndroidStreamHelper.getStream(getContext(), new URI("file", "",
                tempOutFile.getAbsolutePath(), ""));
        assertNotNull(wrapper);
        assertNotNull(wrapper.getInputStream());
        assertEquals(1024, wrapper.getAvaiableDataSize());
    }
    @SmallTest
    public void testOpenAssets() throws IOException {
        StreamHelper wrapper = AndroidStreamHelper.getStream(getContext(),
                URI.create("file:///android_asset/BT139_SERIES.pdf"));
        assertNotNull(wrapper);
        assertNotNull(wrapper.getInputStream());
        assertEquals(Long.MIN_VALUE, wrapper.getAvaiableDataSize());
        wrapper.close();
        wrapper = AndroidStreamHelper.getStream(getContext(),
                URI.create("file:///android_asset/test.mp3"));
        assertNotNull(wrapper);
        assertNotNull(wrapper.getInputStream());
        assertEquals(1024, wrapper.getAvaiableDataSize());
        wrapper.close();
    }
    @SmallTest
    public void testOpenContent() throws URISyntaxException, IOException {
        StreamHelper wrapper = AndroidStreamHelper.getStream(getContext(),
                new URI("content://" + DemoListProvider.PROVIDER_NAME + "/data.jpg"));
        assertNotNull(wrapper);
        assertNotNull(wrapper.getInputStream());
        assertEquals(76367, wrapper.getAvaiableDataSize());
    }
}
