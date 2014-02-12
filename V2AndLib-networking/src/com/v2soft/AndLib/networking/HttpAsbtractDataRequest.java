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
package com.v2soft.AndLib.networking;

import org.apache.http.HttpVersion;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.v2soft.AndLib.dataproviders.AbstractDataRequest;

import java.io.Serializable;

/**
 * 
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
@Deprecated
public abstract class HttpAsbtractDataRequest<R extends Serializable,Params,RawData>
extends AbstractDataRequest<R, Params, RawData> {
    private static final long serialVersionUID = 1L;
    // -----------------------------------------------------------------------
    // Class fields
    // -----------------------------------------------------------------------
    protected DefaultHttpClient mHttpClient = null;

    public HttpAsbtractDataRequest() {
        super();
        final HttpParams httpParams = new BasicHttpParams();
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
        mHttpClient = new DefaultHttpClient(httpParams);
    }
}
