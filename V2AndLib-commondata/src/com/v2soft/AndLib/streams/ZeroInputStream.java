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
