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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Task that will download file from specified URL to local cache
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class CacheHTTPFile extends DummyTask {
    private URL mFileAddress;
    private File mLocalCacheDir;
    private String mCustomHashString;
    private HttpClient mClient;

    public CacheHTTPFile(URL filePath, File localCacheDir) {
        mFileAddress = filePath;
        mLocalCacheDir = localCacheDir;
        mClient = new DefaultHttpClient();
    }

    public CacheHTTPFile(URL filePath, File localCacheDir, String customHashString) {
        mFileAddress = filePath;
        mLocalCacheDir = localCacheDir;
        mCustomHashString = customHashString;
        mClient = new DefaultHttpClient();
    }
    public void setHttpClient(HttpClient client) {
        mClient = client;
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
        final HttpGet request = new HttpGet(mFileAddress.toString());
        final HttpResponse response = mClient.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        if ( statusCode != 200 ) {
            throw new IOException("Status code "+statusCode);
        }
        final InputStream is = response.getEntity().getContent();
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
