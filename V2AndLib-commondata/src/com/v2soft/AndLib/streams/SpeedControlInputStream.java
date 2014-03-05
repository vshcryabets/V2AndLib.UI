package com.v2soft.AndLib.streams;

import java.io.IOException;
import java.io.InputStream;

/**
 * Stream that allows to control transfer speed.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class SpeedControlInputStream extends InputStream {
    private static final long TIME_SLOT_MS = 100;
    protected InputStream mInnerStream;
    protected int mRemainBytes;
    protected int mSpeedBytesPerScond;
    protected long mNextTime;

    public SpeedControlInputStream(InputStream input, int speedInBytesPerSecond) {
        mInnerStream = input;
        mSpeedBytesPerScond = speedInBytesPerSecond;
        updateStat();
    }

    private void updateStat() {
        mNextTime = System.currentTimeMillis()+ TIME_SLOT_MS;
        mRemainBytes = (int) (mSpeedBytesPerScond*TIME_SLOT_MS/1000);
    }

    @Override
    public int read() throws IOException {
        if ( mRemainBytes < 1 ) {
            waitTimeSlot();
        }
        mRemainBytes--;
        return mInnerStream.read();
    }

    private void waitTimeSlot() throws IOException {
        long current = System.currentTimeMillis();
        if ( mNextTime > current ) {
            try {
                Thread.sleep(mNextTime-current);
            } catch (InterruptedException e) {
                throw new IOException("Interrupted", e);
            }
        }
        updateStat();
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return this.read(bytes, 0, bytes.length);
    }

    @Override
    public int read(byte[] bytes, int offset, int count) throws IOException {
        if ( mRemainBytes < 1 ) {
            waitTimeSlot();
        }
        if ( count > mRemainBytes ) {
            count = mRemainBytes;
        }
        count = mInnerStream.read(bytes, offset, count);
        mRemainBytes -= count;
        return count;
    }
}
