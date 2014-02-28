package com.v2soft.AndLib.ui.test;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidDataStreamWrapper;
import com.v2soft.AndLib.dataproviders.DataStreamWrapper;
import com.v2soft.AndLib.filecache.AndroidFileCache;
import com.v2soft.AndLib.filecache.JavaHashFactory;
import com.v2soft.AndLib.filecache.MD5CacheFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class CacheLogicTests extends AndroidTestCase {
	private static final String MD5_NAME = "eef5051286bff7a55c25645bf44606aa";
	private static final String JAVA_HASH_NAME = "784584757";
	private static final String CACHE_TYPE = "testCache";
	String inputName = "http://developer.android.com/reference/android/app/Activity.html";

	@SmallTest
	public void testNamesFactory() throws URISyntaxException, IOException, NoSuchAlgorithmException {
		String inputName = "http://developer.android.com/reference/android/app/Activity.html";
		AndroidFileCache.NameFactory factory = new MD5CacheFactory();
		String result = factory.getLocalName(inputName);
		assertEquals("Wrong MD5 result", MD5_NAME, result);
		// test Java hash naming
		factory = new JavaHashFactory();
		result = factory.getLocalName(inputName);
		assertEquals("Wrong Java hash result", JAVA_HASH_NAME, result);
	}
	@SmallTest
	public void testCacheNaming() throws URISyntaxException, IOException, NoSuchAlgorithmException {
		AndroidFileCache.Builder builder = new AndroidFileCache.Builder(getContext());
		builder.setNamesFactory(new MD5CacheFactory());
		builder.setOutdate(Calendar.DAY_OF_MONTH, 30);
		builder.useExternalCacheFolder(CACHE_TYPE);
		AndroidFileCache cache = builder.build();
		cache.clear();

		String localPath = cache.getCachePathURI(DataStreamsWrapperTests.ASSETS_FILE);
		assertNotNull("No local path", localPath);
		boolean isInCache = cache.isInCache(DataStreamsWrapperTests.ASSETS_FILE);
		assertFalse("File shouldn't be present in cache", isInCache);
		isInCache = cache.isInCache(DataStreamsWrapperTests.ASSET_FILE_PATH);
		assertFalse("File shouldn't be present in cache", isInCache);
		isInCache = cache.isInCache(Uri.parse(DataStreamsWrapperTests.ASSET_FILE_PATH));
		assertFalse("File shouldn't be present in cache", isInCache);
		FileOutputStream out = cache.getFileOutputStream(DataStreamsWrapperTests.ASSETS_FILE);
		DataStreamWrapper wrapper = AndroidDataStreamWrapper.getStream(mContext, DataStreamsWrapperTests.ASSETS_FILE);
		wrapper.copyToOutputStream(out);
		wrapper.close();
		out.close();

		isInCache = cache.isInCache(DataStreamsWrapperTests.ASSETS_FILE);
		assertTrue("File should be in cache", isInCache);
		isInCache = cache.isInCache(DataStreamsWrapperTests.ASSET_FILE_PATH);
		assertTrue("File should be in cache", isInCache);
		isInCache = cache.isInCache(Uri.parse(DataStreamsWrapperTests.ASSET_FILE_PATH));
		assertTrue("File should be in cache", isInCache);

		boolean result = cache.remove(DataStreamsWrapperTests.ASSETS_FILE);
		assertTrue(result);

		isInCache = cache.isInCache(DataStreamsWrapperTests.ASSETS_FILE);
		assertFalse("File shouldn't be present in cache", isInCache);
		isInCache = cache.isInCache(DataStreamsWrapperTests.ASSET_FILE_PATH);
		assertFalse("File shouldn't be present in cache", isInCache);
		isInCache = cache.isInCache(Uri.parse(DataStreamsWrapperTests.ASSET_FILE_PATH));
		assertFalse("File shouldn't be present in cache", isInCache);
		// TODO outdate tests
	}
}
