package com.v2soft.AndLib.ui.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.DataStreamWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class DataStreamsWrapperTests extends AndroidTestCase {
	@SmallTest
	public void testOpenFile() throws URISyntaxException, IOException {
		DataStreamWrapper wrapper = DataStreamWrapper.getStream(getContext(),
				new URI("file:///android_asset/test.mp3"));
		File tempOutFile = File.createTempFile("tmp", "", getContext().getExternalCacheDir());
		FileOutputStream output = new FileOutputStream(tempOutFile);
		wrapper.copyToOutputStream(output).close();

		wrapper = DataStreamWrapper.getStream(getContext(), new URI("file", "",
				tempOutFile.getAbsolutePath(), ""));
		assertNotNull(wrapper);
		assertNotNull(wrapper.getInputStream());
		assertEquals(1024, wrapper.getAvaiableDataSize());
	}
	@SmallTest
	public void testOpenContent() throws URISyntaxException, IOException {
		DataStreamWrapper wrapper = DataStreamWrapper.getStream(getContext(), new URI(""));
	}
	@SmallTest
	public void testOpenAssets() throws URISyntaxException, IOException {
		DataStreamWrapper wrapper = DataStreamWrapper.getStream(getContext(),
				new URI("file:///android_asset/BT139_SERIES.pdf"));
		assertNotNull(wrapper);
		assertNotNull(wrapper.getInputStream());
		assertEquals(Long.MIN_VALUE, wrapper.getAvaiableDataSize());
		wrapper.close();
		wrapper = DataStreamWrapper.getStream(getContext(),
				new URI("file:///android_asset/test.mp3"));
		assertNotNull(wrapper);
		assertNotNull(wrapper.getInputStream());
		assertEquals(1024, wrapper.getAvaiableDataSize());
		wrapper.close();
	}
	@SmallTest
	public void testOpenHTTP() throws URISyntaxException, IOException {
		DataStreamWrapper wrapper = DataStreamWrapper.getStream(getContext(),
				new URI("https://dl.dropboxusercontent.com/u/18391781/Datasheets/STK500%20Protocol.pdf"));
		assertNotNull(wrapper);
		assertNotNull(wrapper.getInputStream());
		assertEquals(161607, wrapper.getAvaiableDataSize());
		wrapper.close();
	}
}
