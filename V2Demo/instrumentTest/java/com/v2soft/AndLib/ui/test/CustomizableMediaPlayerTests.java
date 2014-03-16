package com.v2soft.AndLib.ui.test;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.v2soft.AndLib.dataproviders.AndroidStreamHelper;
import com.v2soft.AndLib.filecache.AndroidFileCache;
import com.v2soft.AndLib.media.CustomizableMediaPlayer;
import com.v2soft.AndLib.streams.StreamHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class CustomizableMediaPlayerTests extends AndroidTestCase {
    private static final String HTTP_FILE_03S = "https://dl.dropboxusercontent.com/u/18391781/v2andlib-externaldata/sin440s3.mp3";
    private static final String HTTP_FILE_07S = "https://dl.dropboxusercontent.com/u/18391781/v2andlib-externaldata/sin880s7.ogg";
    private static final String HTTP_FILE_12S = "https://dl.dropboxusercontent.com/u/18391781/v2andlib-externaldata/saw1220s13.mp3";
    private static final String HTTP_STREAM = "http://icecast2.pulsradio.com:80/pulstranceAAC32.mp3";

    @SmallTest
    public void testPrepareSync() throws IOException, InterruptedException {
        HandlerThread testThread = new HandlerThread("testPrepareAsync thread");
        testThread.start();

        Handler handler = new Handler(testThread.getLooper());
        checkSynchroniousLoad(URI.create(HTTP_FILE_03S), 3000, handler);
//        checkSynchroniousLoad(URI.create(HTTP_FILE_07S), 7000, handler);
        checkSynchroniousLoad(URI.create(HTTP_FILE_12S), 12000, handler);
    }

	@SmallTest
	public void testPrepareAsync() throws IOException, InterruptedException {
        HandlerThread testThread = new HandlerThread("testPrepareAsync thread");
        testThread.start();

        Handler handler = new Handler(testThread.getLooper());
        checkAsynchroniousLoad(URI.create(HTTP_FILE_03S), 3000, handler);
//        checkAsynchroniousLoad(URI.create(HTTP_FILE_07S), 7000, handler);
        checkAsynchroniousLoad(URI.create(HTTP_FILE_12S), 12000, handler);

	}

    private void checkAsynchroniousLoad(final URI uri, int expectedDuration, final Handler handler) throws IOException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch createLatch = new CountDownLatch(1);
        Looper looper = Looper.myLooper();
        final CustomizableMediaPlayer players[] = new CustomizableMediaPlayer[1];
        players[0] = new CustomizableMediaPlayer(mContext, handler);

        players[0].setPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                latch.countDown();
            }
        });

        long t1 = System.currentTimeMillis();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Looper looper = Looper.myLooper();
                try {
                    players[0].setSourceUri(uri, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                createLatch.countDown();
            }
        });
        // wait untill player will be created
        createLatch.await(200, TimeUnit.SECONDS);


        long diff = System.currentTimeMillis() - t1;
        assertTrue("Wrong time " + diff, diff < 500);
        latch.await(200, TimeUnit.SECONDS);

        diff = System.currentTimeMillis()-t1;
        assertTrue("Wrong duration " + players[0].getDuration(), Math.abs(players[0].getDuration() - expectedDuration) < 100);
        assertTrue("Wrong time "+diff, diff > 1000);
        players[0].close();
    }

    private void checkSynchroniousLoad(URI uri, int expectedDuration, Handler handler) throws IOException {
        CustomizableMediaPlayer player = new CustomizableMediaPlayer(mContext, handler);
        long t1 = System.currentTimeMillis();
        player.setSourceUri(uri, false);
        long diff = System.currentTimeMillis()-t1;
        assertTrue("Wrong time "+diff, diff > 1000);
        assertTrue("Wrong duration " + player.getDuration(), Math.abs(player.getDuration() - expectedDuration) < 100);
        player.close();
    }
}
