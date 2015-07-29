package com.v2soft.AndLib.mediatests;

import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidStreamHelper;
import com.v2soft.AndLib.filecache.AndroidFileCache;
import com.v2soft.AndLib.filecache.FileCache;
import com.v2soft.AndLib.media.JPEGHelper;
import com.v2soft.AndLib.streams.StreamHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class JPEGHelperTests extends AndroidTestCase {

	@SmallTest
	public void testGetOptions() throws IOException, NoSuchAlgorithmException, InterruptedException {
        URI uri = URI.create(BitmapOperationsTests.ASSET_LARGE_FILE_PATH);
        FileCache cache = new AndroidFileCache.Builder(getContext()).build();
        cache.clear();
        File file = cache.getFileByURI(uri);
        StreamHelper stream = AndroidStreamHelper.getStream(getContext(), uri);
        FileOutputStream output = new FileOutputStream(file);
        stream.copyToOutputStream(output);
        output.close();
        JPEGHelper helper = new JPEGHelper();
        BitmapFactory.Options options = helper.getImageOptions(file);
        assertNotNull("Options wasn't read", options);
        assertEquals("Wrong width", 3508, options.outWidth);
        assertEquals("Wrong height", 4960, options.outHeight);
	}

}
