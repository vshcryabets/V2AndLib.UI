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
package com.v2soft.AndLib.medianative;

/**
 * MP3 routines.
 * @author V.Shcryabets (vshcryabets@gmail.com)
 *
 */
public class MP3Helper {
	public enum LAMEMode {
		stereo,
		jstereo,
		dualchannel, // LAME doesn't supports this!
		mono,
		not_set,
		max_indicator // Don't use this! It's used for sanity checks.
	};
	static {
		System.loadLibrary("lamewrapper_full");
	}

	private int mEncoderId;

	public MP3Helper(int numberOfCahnnels, int inSampleRate, int outSampleRate, LAMEMode mode) {
		mEncoderId = allocateEncoderNative(numberOfCahnnels, inSampleRate, outSampleRate, mode.ordinal());
		if ( mEncoderId == 0 ) {
			throw new IllegalStateException("Can't allocate encoder");
		}
	}
	public int encodeBuffer(byte[] buffer, int offset, int count, byte[] outputBuffer) {
		if ( buffer == null) {
			throw new NullPointerException("Input buffer is null");
		}
		if ( outputBuffer == null) {
			throw new NullPointerException("Output buffer is null");
		}
		// TODO use offset & count
		return lameEncodeBufferNative(buffer, outputBuffer, mEncoderId);
	}

	private native String getVersion();
	private native int allocateEncoderNative(int numberOfChannels, int inputSampleRate, int outputSampleRate, int mode);
	private native int lameEncodeBufferNative(byte [] inbuffer, byte[] outbuffer, int handler);
	private native int finishNative(int handler);
}
