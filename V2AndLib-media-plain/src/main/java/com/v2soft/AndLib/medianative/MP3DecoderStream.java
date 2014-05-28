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

    public MP3DecoderStream(String filepath) {
        nativeLoad(filepath);
    }

    @Override
    public void close() throws IOException {
        super.close();
        nativeRelease();
    }

    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] bytes, int i, int i2) throws IOException {
        return super.read(bytes, i, i2);
    }

    /**
     * Native routines.
     */
    protected native int nativeLoad(String path);
    protected native int nativeRelease();
}
