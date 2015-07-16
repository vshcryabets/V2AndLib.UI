package com.v2soft.AndLib.demotest;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidStreamHelper;
import com.v2soft.AndLib.streams.StreamHelper;
import com.v2soft.AndLib.filecache.AndroidFileCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.UUID;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class CacheLogicTests extends AndroidTestCase {
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

    @SmallTest
    public void testCacheFolders() throws URISyntaxException, IOException, NoSuchAlgorithmException, InterruptedException {
        AndroidFileCache.Builder builder = new AndroidFileCache.Builder(getContext());
        String folderName = UUID.randomUUID().toString();
        File cacheFolder = mContext.getExternalFilesDir(folderName);
        builder.useExternalFilesFolder(folderName);
        AndroidFileCache cache = builder.build();
        File file = cache.getCacheFolder();
        assertEquals(cacheFolder.getAbsolutePath(), file.getAbsolutePath());

        builder = new AndroidFileCache.Builder(getContext());
        folderName = UUID.randomUUID().toString();
        cacheFolder = new File(mContext.getFilesDir(), folderName);
        builder.useInternalFilesFolder(folderName);
        cache = builder.build();
        file = cache.getCacheFolder();
        assertEquals(cacheFolder.getAbsolutePath(), file.getAbsolutePath());

        builder = new AndroidFileCache.Builder(getContext());
        folderName = UUID.randomUUID().toString();
        cacheFolder = new File(mContext.getExternalCacheDir(), folderName);
        builder.useExternalCacheFolder(folderName);
        cache = builder.build();
        file = cache.getCacheFolder();
        assertEquals(cacheFolder.getAbsolutePath(), file.getAbsolutePath());

        builder = new AndroidFileCache.Builder(getContext());
        folderName = UUID.randomUUID().toString();
        cacheFolder = new File(mContext.getCacheDir(), folderName);
        builder.useInternalCacheFolder(folderName);
        cache = builder.build();
        file = cache.getCacheFolder();
        assertEquals(cacheFolder.getAbsolutePath(), file.getAbsolutePath());

    }
}
