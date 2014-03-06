package com.v2soft.AndLib.ui.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidDataStreamWrapper;
import com.v2soft.AndLib.dataproviders.DataStreamWrapper;
import com.v2soft.AndLib.filecache.AndroidFileCache;
import com.v2soft.AndLib.filecache.FileCache;
import com.v2soft.AndLib.filecache.MD5CacheFactory;
import com.v2soft.AndLib.media.BitmapOperations;
import com.v2soft.AndLib.media.MP3EncodingOutputStream;
import com.v2soft.AndLib.media.MP3Helper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class AudioOperationsTests extends AndroidTestCase {
	public static final String ASSET_SOURCE_FILE_PATH = "file:///android_asset/sample.wav";

	@SmallTest
	public void testMP3Encoder() throws IOException, NoSuchAlgorithmException, InterruptedException {
		DataStreamWrapper wrapper = AndroidDataStreamWrapper.getStream(getContext(),
				URI.create(ASSET_SOURCE_FILE_PATH));
		AndroidFileCache.Builder builder = new AndroidFileCache.Builder(getContext());
		builder.useExternalCacheFolder("audio");
		FileCache cache = builder.build();

		OutputStream output = cache.getFileOutputStream(URI.create(ASSET_SOURCE_FILE_PATH));
		MP3EncodingOutputStream encoderStream = new MP3EncodingOutputStream(
				output, 1, 8000, 44100, MP3Helper.LAMEMode.stereo);
		wrapper.copyToOutputStream(encoderStream);
		wrapper.close();
		encoderStream.close();

		DataStreamWrapper outWrapper = DataStreamWrapper.getStream(
				URI.create("file://"+cache.getCachePathURI(URI.create(ASSET_SOURCE_FILE_PATH))));
		assertTrue("Zero size output file", outWrapper.getAvaiableDataSize() > 0 );
		assertTrue("Wrong compression", wrapper.getAvaiableDataSize() >= outWrapper.getAvaiableDataSize() );
	}

}
