package com.v2soft.AndLib.dao;

import org.apache.http.HttpVersion;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;


/**
 * 
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public abstract class HttpAsbtractDataRequest<R,Params,RawData> 
	extends AbstractDataRequest<R, Params, RawData> {
	// -----------------------------------------------------------------------
	// Class fields
	// -----------------------------------------------------------------------
	protected DefaultHttpClient mHttpClient = null;

	public HttpAsbtractDataRequest(AbstractDataRequestCallback<R> callback) {
		super(callback);
		final HttpParams httpParams = new BasicHttpParams();
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
		mHttpClient = new DefaultHttpClient(httpParams);
	}
}
