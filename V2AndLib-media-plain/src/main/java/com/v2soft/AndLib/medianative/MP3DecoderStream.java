package com.v2soft.AndLib.medianative;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by V.Shcryabets on 5/27/14.
 *
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public class MP3DecoderStream extends InputStream {
    static {
        System.loadLibrary("audiostreams");
    }

    private int mHandlerId = 0;

    public MP3DecoderStream(String filepath) {
        int handler = nativeOpen(filepath);
        if ( handler ==  0 ) {
            throw new IllegalStateException("Can't open "+filepath);
        }
        mHandlerId = handler;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if ( mHandlerId ==  0 ) {
            throw new IllegalStateException("Decoder wasn't initialized");
        }
        int result = nativeRelease(mHandlerId);
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] bytes, int i, int i2) throws IOException {
        if ( mHandlerId ==  0 ) {
            throw new IllegalStateException("Decoder wasn't initialized");
        }
        return nativeRead(bytes, i, i2, mHandlerId);
    }

    /**
     * Native routines.
     */
    protected native int nativeOpen(String path);
    protected native int nativeRelease(int handler);
    protected native int nativeRead(byte[] buffer, int offset, int count, int handler);
}
