/*
 * Copyright (C) 2013 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.dataproviders;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

/**
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public abstract class AbstractHTTPServiceRequest<ResultType, ParametrType, RawData> 
    extends AbstractServiceRequest<ResultType, ParametrType, RawData> {
    private static final long serialVersionUID = 1L;

    public AbstractHTTPServiceRequest(Context context) {
        super(context);
    }
    /**
     * Prepare url connection for this request.
     * @param url
     * @param method
     * @return
     * @throws IOException
     */
    protected HttpURLConnection prepareConnection(URL url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(getMethod());
        connection.setUseCaches(false);
        connection.setDoInput(true);
        if (getContentType() != null) {
            connection.setRequestProperty("Content-Type", getContentType());
        }
        return connection;
    }
    /**
     * Get HTTP request method type. (POST/GET/DELETE etc)
     * 
     * @return HTTP request method type
     */
    protected abstract String getMethod();
    /**
     * Get type of content for request
     */
    protected abstract String getContentType();
    /**
     * Should we dump request/response to logcat console.
     * @return
     */
    protected abstract boolean shouldLogInteractionToLogCat();
}
