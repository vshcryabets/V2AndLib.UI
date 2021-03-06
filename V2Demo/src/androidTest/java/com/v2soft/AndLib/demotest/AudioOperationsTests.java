package com.v2soft.AndLib.demotest;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidStreamHelper;
import com.v2soft.AndLib.streams.StreamHelper;
import com.v2soft.AndLib.filecache.AndroidFileCache;
import com.v2soft.AndLib.filecache.FileCache;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

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

//		OutputStream output = cache.getFileOutputStream(URI.create(ASSET_SOURCE_FILE_PATH));
//		MP3EncodingOutputStream encoderStream = new MP3EncodingOutputStream(
//				output, 1, 8000, 44100, MP3Helper.LAMEMode.stereo);
//		wrapper.copyToOutputStream(encoderStream);
//		wrapper.close();
//		encoderStream.close();
//
//		StreamHelper outWrapper = StreamHelper.getStream(
//                URI.create("file://" + cache.getCachePathURI(URI.create(ASSET_SOURCE_FILE_PATH))));
//		assertTrue("Zero size output file", outWrapper.getAvaiableDataSize() > 0 );
//		assertTrue("Wrong compression", wrapper.getAvaiableDataSize() >= outWrapper.getAvaiableDataSize() );

        // generate sine and write it to file
        Random random = new Random();
        int sourceFrequency = random.nextInt(8000)+1000;
        int sourceDuration = 1500;
        int sourceSampleRate = 44100;
        byte[] data = sineGenerator(true, sourceSampleRate, sourceDuration, sourceFrequency);
        int frequency = checkFrequency(data, true, sourceSampleRate);
        assertTrue("Wrong frequency 1 "+sourceFrequency+" expected but was "+ frequency, Math.abs(frequency - sourceFrequency) < 50 );

        String path = cache.getCachePathURI(URI.create("local:///sineData4600"));
        FileOutputStream out = cache.getFileOutputStream(URI.create("local:///sineData4600"));
        out.write(data);
        out.close();
        // encode data from file
        // decode from MP3 file
        // check PCM data
	}

    private byte[] sineGenerator(boolean stereo, int sampleRate, int longInMs, int frequencyInHZ) {
        int framesCount = sampleRate * longInMs / 1000;
        int bytesPerFrame = (stereo ? 4 : 2);
        byte dataArray[] = new byte[framesCount*bytesPerFrame];
        double left = 0.0;
        double right = Math.PI/2;
        double scale = frequencyInHZ/(1/(2*Math.PI));
        for ( int i = 0; i < framesCount; i++ ) {
            double t = (double)i / (double)sampleRate;
            short leftSample = (short) (Short.MAX_VALUE*Math.sin(t*scale+left));
            short rightSample = (short) (Short.MAX_VALUE*Math.sin(t*scale+right));
            byte first = (byte) (leftSample >> 8);
            byte second = (byte) (leftSample & 0xFF);
            dataArray[i*bytesPerFrame] = first;
            dataArray[i*bytesPerFrame+1] = second;
            if ( stereo ) {
                first = (byte) (rightSample >> 8);
                second = (byte) (rightSample & 0xFF);
                dataArray[i * bytesPerFrame + 2] = first;
                dataArray[i * bytesPerFrame + 3] = second;
            }
        }
        return dataArray;
    }

    private int checkFrequency(byte rawData[], boolean stereo, int sampleRate) {
        int zeroCount = 0;
        int offset = 0;
        int bytesPerFrame = (stereo ? 4 : 2);
        int prevValue = 0;
        while (offset < rawData.length ) {
            short first = rawData[offset];
            short second = rawData[offset+1];
            short value = (short) ((first << 8) | (second & 0xFF));
            if ( prevValue < 0 && value >= 0) {
                zeroCount++;
            }
            prevValue = value;
            offset += bytesPerFrame;
        }
        int result = zeroCount*(bytesPerFrame*sampleRate)/rawData.length;
        return result;
    }
}
