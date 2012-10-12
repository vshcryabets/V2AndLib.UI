/*
 * Copyright (C) 2012 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.dataproviders.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Task that will download file from specified URL to local cache
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class CacheHTTPFile extends DummyTask {
    private URL mFileAddress;
    private File mLocalCacheDir;
    private String mCustomHashString;

    public CacheHTTPFile(URL filePath, File localCacheDir) {
        mFileAddress = filePath;
        mLocalCacheDir = localCacheDir;
    }

    public CacheHTTPFile(URL filePath, File localCacheDir, String customHashString) {
        mFileAddress = filePath;
        mLocalCacheDir = localCacheDir;
        mCustomHashString = customHashString;
    }

    public String getLocalPath() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if ( mCustomHashString != null ) {
            return mCustomHashString;
        } else {
            // get file path hash
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte [] path = mFileAddress.toExternalForm().getBytes("utf-8");
            md.update(path, 0, path.length);
            final String filename = new BigInteger( 1, md.digest() ).toString( 16 );
            return filename;
        }
    }

    @Override
    public void execute() throws Exception {
        final String filename = getLocalPath();
        final File file = new File(mLocalCacheDir, filename);
        if ( file.exists() ) {
            // we have this file in cache
            return;
        }
        // download this file
        // Open a connection to that URL.
        final URLConnection connection = mFileAddress.openConnection();
        final InputStream is = connection.getInputStream();
        final FileOutputStream fos = new FileOutputStream(file);
        final byte [] buffer = new byte[4096];
        int read = 0;
        while ( (read = is.read(buffer)) > 0 ) {
            fos.write(buffer, 0, read);
        }
        is.close();
        fos.close();
    }
}
