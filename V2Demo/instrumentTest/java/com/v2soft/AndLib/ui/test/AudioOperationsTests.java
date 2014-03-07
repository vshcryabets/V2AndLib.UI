package com.v2soft.AndLib.ui.test;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidStreamHelper;
import com.v2soft.AndLib.streams.StreamHelper;
import com.v2soft.AndLib.filecache.AndroidFileCache;
import com.v2soft.AndLib.filecache.FileCache;
import com.v2soft.AndLib.media.MP3EncodingOutputStream;
import com.v2soft.AndLib.media.MP3Helper;

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
		StreamHelper wrapper = AndroidStreamHelper.getStream(getContext(),
                URI.create(ASSET_SOURCE_FILE_PATH));
		FileCache cache = new AndroidFileCache.Builder(getContext()).useExternalCacheFolder("audio").build();

		OutputStream output = cache.getFileOutputStream(URI.create(ASSET_SOURCE_FILE_PATH));
		MP3EncodingOutputStream encoderStream = new MP3EncodingOutputStream(
				output, 1, 8000, 44100, MP3Helper.LAMEMode.stereo);
		wrapper.copyToOutputStream(encoderStream);
		wrapper.close();
		encoderStream.close();

		StreamHelper outWrapper = StreamHelper.getStream(
                URI.create("file://" + cache.getCachePathURI(URI.create(ASSET_SOURCE_FILE_PATH))));
		assertTrue("Zero size output file", outWrapper.getAvaiableDataSize() > 0 );
		assertTrue("Wrong compression", wrapper.getAvaiableDataSize() >= outWrapper.getAvaiableDataSize() );
	}

}
