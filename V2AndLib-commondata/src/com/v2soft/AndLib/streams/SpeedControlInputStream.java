/*
 * Copyright (C) 2014 V.Shcryabets (vshcryabets@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.v2soft.AndLib.streams;

import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream that allows to control transfer speed.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class SpeedControlInputStream extends InputStream {
    protected ResourceTimeController mSpeedControl;
    protected InputStream mInnerStream;

    public SpeedControlInputStream(InputStream input, int speedInBytesPerSecond) {
        mInnerStream = input;
        mSpeedControl = new ResourceTimeController(speedInBytesPerSecond);
    }

    @Override
    public int read() throws IOException {
        try {
            mSpeedControl.checkRemainedResources();
        } catch (InterruptedException e) {
            throw new IOException("Interrupted", e);
        }
        mSpeedControl.decrementResources(1);
        return mInnerStream.read();
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        return this.read(bytes, 0, bytes.length);
    }

    @Override
    public int read(byte[] bytes, int offset, int count) throws IOException {
        try {
            mSpeedControl.checkRemainedResources();
        } catch (InterruptedException e) {
            throw new IOException("Interrupted", e);
        }
        count = (int)mSpeedControl.requestResources(count);
        count = mInnerStream.read(bytes, offset, count);
        mSpeedControl.decrementResources(count);
        return count;
    }
}
