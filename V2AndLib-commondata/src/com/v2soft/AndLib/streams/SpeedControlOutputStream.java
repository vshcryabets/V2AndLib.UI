package com.v2soft.AndLib.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Stream that allows to control transfer speed.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class SpeedControlOutputStream extends OutputStream {
    protected OutputStream mInnerStream;
    protected ResourceTimeController mSpeedControl;

    public SpeedControlOutputStream(OutputStream input, int speedInBytesPerSecond) {
        mInnerStream = input;
        mSpeedControl = new ResourceTimeController(speedInBytesPerSecond);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        this.write(bytes, 0, bytes.length);
    }

    @Override
    public void write(int i) throws IOException {
        try {
            mSpeedControl.checkRemainedResources();
        } catch (InterruptedException e) {
            throw new IOException("Interrupted", e);
        }
        mSpeedControl.decrementResources(1);
        mInnerStream.write(i);
    }

    @Override
    public void write(byte[] bytes, int offset, int count) throws IOException {
        try {
            mSpeedControl.checkRemainedResources();
        } catch (InterruptedException e) {
            throw new IOException("Interrupted", e);
        }
        mSpeedControl.requestResources(count);
        mInnerStream.write(bytes, offset, count);
        mSpeedControl.decrementResources(count);
    }
}
