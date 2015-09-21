/*
 * Copyright (C) 2014-2015 V.Shcryabets (vshcryabets@gmail.com)
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

import java.io.File;

/**
 * JPEG routines.
 * @author V.Shcryabets (vshcryabets@gmail.com)
 *
 */
public class PNGHelper {

    public enum Rotate {
        CW_90,
        CW_180,
        CW_270
    }
    static {
        System.loadLibrary("graphics");
    }

    public PNGHelper() {
        getVersion();
    }

    /**
     * Read header information from local jpeg-file.
     * @param file JPEG file
     * @return JPEGOptions structure
     */
    public JPEGOptions getImageOptions(File file) throws JPEGHelperException {
        JPEGOptions result = new JPEGOptions();
        int errorCode = nativeGetJPEGInfo(file.getAbsolutePath(), result);
        checkErrorCode(errorCode);
        return result;
    }

    public String getVersion() {
        return nativeGetVersion();
    }

    public void rotate(File file, Rotate rotateAngle, File outfile) throws JPEGHelperException {
        int errorCode = nativeRotateJPEG(file.getAbsolutePath(), rotateAngle.ordinal(), outfile.getAbsolutePath());
        checkErrorCode(errorCode);
    }

    public void crop(File file, Rect cropArea, File outfile) throws JPEGHelperException {
        int errorCode = nativeCropJPEG(file.getAbsolutePath(), cropArea.toArray(), outfile.getAbsolutePath(), 100);
        checkErrorCode(errorCode);
    }

    public byte[] load(File file, Rect cropArea) {
        return nativeLoadJPEG(file.getAbsolutePath(), (cropArea == null ? null : cropArea.toArray()));
    }

    public byte[] load(File file) {
        return nativeLoadJPEG(file.getAbsolutePath(), null);
    }

    /**
     * Save image represented by the byte array to the JPEg file.
     * @param file
     * @param data
     * @param quality
     * @throws JPEGHelperException
     */
    public void save(File file, byte[] data, int width, int height, int quality) throws JPEGHelperException {
        int errorCode = nativeSaveJPEG(file.getAbsolutePath(), data, width, height, quality);
        checkErrorCode(errorCode);
    }

    private void checkErrorCode(int code) throws JPEGHelperException {
        if ( code != 0 ) {
            throw new JPEGHelperException(code);
        }
    }

    protected native String nativeGetVersion();
    protected native int nativeGetJPEGInfo(String localFilePath, JPEGOptions options);
    private native int nativeRotateJPEG(String inputPath, int ordinal, String outputPath);
    private native int nativeCropJPEG(String input, int[] cropArea, String output, int jpegOutputQuality);
    private native byte[] nativeLoadJPEG(String input, int[] cropArea);
    private native int nativeSaveJPEG(String output, byte[] data, int width, int height, int quality);
}
