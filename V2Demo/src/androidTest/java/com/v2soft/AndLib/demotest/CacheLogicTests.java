package com.v2soft.AndLib.demotest;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidStreamHelper;
import com.v2soft.AndLib.streams.StreamHelper;
import com.v2soft.AndLib.filecache.AndroidFileCache;

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

	@SmallTest
	public void testCacheNaming() throws URISyntaxException, IOException, NoSuchAlgorithmException, InterruptedException {
		AndroidFileCache.Builder builder = new AndroidFileCache.Builder(getContext());
		builder.setOutdate(Calendar.DAY_OF_MONTH, 30);
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
		StreamHelper wrapper = AndroidStreamHelper.getStream(mContext, DataStreamsWrapperTests.ASSETS_FILE);
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
