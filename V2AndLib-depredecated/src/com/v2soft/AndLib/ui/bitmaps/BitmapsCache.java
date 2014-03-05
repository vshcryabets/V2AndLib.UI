package com.v2soft.AndLib.ui.bitmaps;

import android.graphics.Bitmap;
import android.util.Log;

import com.v2soft.AndLib.dataproviders.ITask;
import com.v2soft.AndLib.dataproviders.ITaskHub;
import com.v2soft.AndLib.dataproviders.ITaskListener;
import com.v2soft.AndLib.dataproviders.tasks.DownloadAndDecodeImage;
import com.v2soft.AndLib.filecache.FileCache;

import java.net.URI;
import java.util.HashMap;

/**
 * !!! Experimental class !!!! Do not use it!
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
@Deprecated
public class BitmapsCache implements ITaskListener {
    public interface BitmapsCacheListener {
        public void onBitmapUpdated(Bitmap bitmap, int id);
    }

    private static final String LOG_TAG = BitmapsCache.class.getSimpleName();

    private HashMap<Integer, BitmapsCacheListener> mListeners;
    private HashMap<URI, Bitmap> mBitmaps;
    private HashMap<Bitmap, Integer> mCountRefs;
    private ITaskHub mTaskHub;
    private FileCache mCacheDir;

    public BitmapsCache(ITaskHub taskHub, FileCache cacheDir) {
        mCountRefs = new HashMap<Bitmap, Integer>();
        mBitmaps = new HashMap<URI, Bitmap>();
        mListeners = new HashMap<Integer, BitmapsCacheListener>();
        mTaskHub = taskHub;
        mCacheDir = cacheDir;
    }

    public void releaseBitmap(Bitmap bitmap) {
        int count = 0;
        synchronized ( mCountRefs ) {
            if ( mCountRefs.containsKey(bitmap)) {
                count = mCountRefs.get(bitmap);
                count--;
                if ( count > 0 ) {
                    mCountRefs.put(bitmap, count);
                } else {
                    mCountRefs.remove(bitmap);
                }
            }
        }
        if ( count < 1 ) {
            bitmap.recycle();
        }
    }

    /**
     * Increments the reference count for the specified bitmap.
     * @param bitmap
     */
    public synchronized void addRef(Bitmap bitmap) {
        synchronized ( mCountRefs ) {
            int count = 0;
            if ( mCountRefs.containsKey(bitmap) ) {
                count = mCountRefs.get(bitmap);
            }
            count++;
            mCountRefs.put(bitmap, count);
        }
    }
    
    public void releaseAll() {
        synchronized ( mBitmaps ) {
            for (URI url : mBitmaps.keySet()) {
                Bitmap bitmap = mBitmaps.get(url);
                if ( !bitmap.isRecycled() ) {
                    bitmap.recycle();
                }
                mBitmaps.remove(url);
            }
        }
    }

    public void getBitmap(URI url, BitmapsCacheListener listener, int tag) {
        if ( url == null ) {
            return;
        }
        Log.d(LOG_TAG, "Request bitmap for id = "+tag);
        if ( mBitmaps.containsKey(url) ) {
            listener.onBitmapUpdated(mBitmaps.get(url), tag);
            Log.d(LOG_TAG, "Found cache bitmap for id = "+tag);
        } else {
            Log.d(LOG_TAG, "Download bitmap for id = "+tag);
            final DownloadAndDecodeImage getThumb =
                    new DownloadAndDecodeImage(url, mCacheDir);
            getThumb.setTaskTagObject(tag);
            mTaskHub.addTask(getThumb, this);
            mListeners.put(tag, listener);
        }
    }

    @Override
    public void onMessageFromTask(ITask task, Object message) {
    }

    @Override
    public void onTaskFinished(ITask t) {
        int id = (Integer)t.getTaskTagObject();
        Log.d(LOG_TAG, "Ready bitmap for id = "+id);
        if ( mListeners.containsKey(id)) { 
            BitmapsCacheListener listener = mListeners.get(id);
            DownloadAndDecodeImage task = (DownloadAndDecodeImage) t;
            Bitmap bitmap = task.getBitmap();
            URI url = task.getURL();
            mBitmaps.put(url, bitmap);
            listener.onBitmapUpdated(bitmap, id);
            Log.d(LOG_TAG, "Return bitmap for id = "+id);
        }
    }

    @Override
    public void onTaskFailed(ITask task, Throwable error) {
    }
}
