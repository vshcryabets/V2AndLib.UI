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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public class MP3EncodingOutputStream extends BufferedOutputStream {
	protected OutputStream mInternalStream;
	/**
	 *
	 * @param output
	 * @param inSampleRate input sample rate in Hz
	 * @param mode stereo, jstereo, dual channel (not supported), mono default: lame picks based on compression ration and input channels
	 */
	public MP3EncodingOutputStream(final OutputStream output, int inSampleRate,
								   MP3Helper.LAMEMode mode) {
		super(new InternallStream(output, inSampleRate, mode), 8192);
		mInternalStream = output;
	}

	@Override
	public synchronized void close() throws IOException {
		mInternalStream.close();
		super.close();
	}

	private static class InternallStream extends OutputStream {
		private byte[] mEncodedBuffer;
		private MP3Helper mHelper;
		private OutputStream mEncodedStream;

		InternallStream(OutputStream output, int inSampleRate,
						MP3Helper.LAMEMode mode) {
			mEncodedStream = output;
			mHelper = new MP3Helper(inSampleRate, mode);
		}

		@Override
		public void write(int oneByte) throws IOException {
		}
		@Override
		public void write(byte[] buffer, int offset, int count) throws IOException {
			// send data to encoder
//			int encoded = mHelper.encodeBuffer(buffer, offset, count, mEncodedBuffer);
			// write encoded data to main output stream
//			mEncodedStream.write(mEncodedBuffer, 0, encoded);
		}
	}
}
