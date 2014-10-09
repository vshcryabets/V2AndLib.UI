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

import java.io.File;

/**
 * MP3 routines.
 * @author V.Shcryabets (vshcryabets@gmail.com)
 *
 */
public class JPEGHelper {
    static {
        System.loadLibrary("graphics");
    }

    public JPEGHelper() {
        getVersion();
    }

    /**
     * Read header information from local jpeg-file.
     * @param file
     * @return
     */
    public JPEGOptions getImageOptions(File file) {
        JPEGOptions result = new JPEGOptions();
        int errorCode = nativeGetJPEGInfo(file.getAbsolutePath(), result);
        checkErrorCode(errorCode);
        return result;
    }
    
    public String getVersion() {
        return nativeGetVersion();
    }
    
    private void checkErrorCode(int code) {
        if ( code != 0 ) {
            throw new IllegalStateException("Error code "+code);
        }
    }

    protected native String nativeGetVersion();
    protected native int nativeGetJPEGInfo(String localFilePath, JPEGOptions options);
}
