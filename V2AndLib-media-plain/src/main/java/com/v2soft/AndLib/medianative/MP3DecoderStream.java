package com.v2soft.AndLib.medianative;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by V.Shcryabets on 5/27/14.
 *
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public class MP3DecoderStream extends BufferedOutputStream {
    private static final int BUFFER_SIZE = 8192;

    public interface Callback {
        void handleBuffer(ByteBuffer buffer) throws IOException;

        void setChannelsCount(int count);

        void setSampleRate(int sampleRate);
    }

    public interface DecoderStateListener {
        void onInitialized(MP3DecoderStream decoder);
    }
    static {
        System.loadLibrary("audiostreams");
    }

    private int mHandlerId = 0;
    protected OutputStream mInternalStream;
    protected ByteBuffer mBuffer;
    protected int mChannelsCount;
    protected int mSampleRate;
    protected boolean isInitialized;
    protected int mHandledBuffersCount;
    protected DecoderStateListener mStateListener;

    public MP3DecoderStream(OutputStream output) {
        super(output, BUFFER_SIZE);
        mInternalStream = output;
        mHandlerId = nativeOpenDecoder(mCallback, BUFFER_SIZE);
        mBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        isInitialized = false;
    }

    public void setListener(DecoderStateListener listener) {
        mStateListener = listener;
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
        isInitialized = false;
        mInternalStream.close();
        int res = nativeReleaseDecoder(mHandlerId);
        checkError(res);
    }

    @Override
    public void write(byte[] buffer, int offset, int count) throws IOException {
        mBuffer.clear();
        mBuffer.put(buffer, offset, count);
        // send data to encoder
        int res = nativeWriteDecoder(mHandlerId, mBuffer);
        checkError(res);
    }

    private void checkError(int res) throws IOException {
        switch (res) {
            case -2:
                throw new IllegalStateException("Wrong decoder/encoder handler (-2)");
            case -3:
                throw new IOException("Internal decoder/encoder exception (-3)");
            case -4:
                throw new IOException("Not implemented (-4)");
            case 0:
            default:
                break;
        }
    }

    public int getChannelsCount() {
        return mChannelsCount;
    }

    public int getSampleRate() {
        return mSampleRate;
    }

    private final Callback mCallback = new Callback() {
        @Override
        public void handleBuffer(ByteBuffer buffer) throws IOException {
            mHandledBuffersCount++;
            byte[] bytebuffer = new byte[buffer.remaining()]; // TODO reuse buffer
            buffer.get(bytebuffer);
            mInternalStream.write(bytebuffer);
        }

        @Override
        public void setChannelsCount(int count) {
            mChannelsCount = count;
            if ( mSampleRate > 0 ) {
                setIsInitialized();
            }
        }

        @Override
        public void setSampleRate(int sampleRate) {
            mSampleRate = sampleRate;
            if (mChannelsCount > 0) {
                setIsInitialized();
            }
        }
    };

    private void setIsInitialized() {
        isInitialized = true;
        if (mStateListener != null) {
            mStateListener.onInitialized(this);
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public int getHandledBuffersCount() {
        return mHandledBuffersCount;
    }

    /**
     * Native routines.
     */
    protected native int nativeOpenDecoder(Callback callback, int maxBufferSize);

    protected native int nativeReleaseDecoder(int handler);

    protected native int nativeWriteDecoder(int handler, ByteBuffer buffer);
}
