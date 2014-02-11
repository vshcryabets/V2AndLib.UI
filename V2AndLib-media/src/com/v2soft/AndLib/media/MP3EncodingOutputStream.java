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
package com.v2soft.AndLib.media;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;

/**
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public class MP3EncodingOutputStream extends OutputStream {
	private OutputStream mInnerOutputStream;
	private Queue<byte[]> mEncodeBuffers;
	private int position;

	public MP3EncodingOutputStream(OutputStream output) {
		mInnerOutputStream = output;
	}

	@Override
	public void write(int oneByte) throws IOException {

	}

	@Override
	public void write(byte[] buffer) throws IOException {
		this.write(buffer, 0, buffer.length);
	}

	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
		super.write(buffer, offset, count);
	}
}
