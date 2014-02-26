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

/**
 * MP3 routines.
 * @author V.Shcryabets (vshcryabets@gmail.com)
 *
 */
public class MP3Helper {
	public enum LAMEMode {
		stereo, jstereo, dualchannel, mono
	};
	private int mEncoderId;

	public MP3Helper(int inSampleRate, LAMEMode mode) {
		mEncoderId = allocateEncoderNative();
		if ( mEncoderId == 0 ) {
			throw new IllegalStateException("Can't allocate encoder");
		}
	}

	private native String getVersion();
	private native int allocateEncoderNative();
	private native int encodeBufferNative(byte [] inbuffer, int offset, int size, byte[] outbuffer);
}
