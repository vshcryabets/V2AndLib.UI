package com.v2soft.AndLib.streams;

import java.io.IOException;
import java.io.InputStream;

/**
 * Stream that always return zero.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class ZeroInputStream extends InputStream {
    @Override
    public int read() throws IOException {
        return 0;
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        for ( int i = 0; i < bytes.length; i++) {
            bytes[i] = 0;
        }
        return bytes.length;
    }

    @Override
    public int read(byte[] bytes, int offset, int count) throws IOException {
        for ( int i = offset; i < count; i++) {
            bytes[i] = 0;
        }
        return count;
    }
}
