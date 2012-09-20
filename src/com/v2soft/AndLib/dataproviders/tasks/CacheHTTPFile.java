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

import com.v2soft.AndLib.dataproviders.ITask;

/**
 * Task that will download file from specified URL to local cache
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class CacheHTTPFile implements ITask {
    private int mId;
    private int mTag;
    private Object mTagObj;
    private URL mFileAddress;
    private File mLocalCacheDir;
    
    public CacheHTTPFile(URL filePath, File localCacheDir) {
        mFileAddress = filePath;
        mLocalCacheDir = localCacheDir;
    }
    
    public String getLocalPath() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // get file path hash
        final MessageDigest md = MessageDigest.getInstance("MD5");
        final byte [] path = mFileAddress.toExternalForm().getBytes("utf-8");
        md.update(path, 0, path.length);
        final String filename = new BigInteger( 1, md.digest() ).toString( 16 );
        return filename;
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

    @Override
    public void setTaskId(int id) {
        mId = id;
    }

    @Override
    public int getTaskId() {
        return mId;
    }

    @Override
    public int getTaskTag() {
        return mTag;
    }

    @Override
    public ITask setTaskTag(int id) {
        mTag = id;
        return this;
    }

    @Override
    public ITask setTaskTagObject(Object tag) {
        mTagObj = tag;
        return this;
    }

    @Override
    public Object getTaskTagObject() {
        return mTagObj;
    }
}
