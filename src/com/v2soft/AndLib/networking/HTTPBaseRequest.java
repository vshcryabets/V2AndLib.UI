package com.v2soft.AndLib.networking;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.v2soft.AndLib.dao.AbstractDataRequest;

/**
 * 
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public abstract class HTTPBaseRequest<R, Params, RawData> 
	extends AbstractDataRequest<R, Params, RawData> {
	// ====================================================
	// Constants
	// ====================================================
	private static final String LOG_TAG = HTTPBaseRequest.class.getSimpleName();
	// ====================================================
	// Constructors
	// ====================================================
	protected HttpClient mClient;
	
	public HTTPBaseRequest(AbstractDataRequestCallback<R> callback) {
		super(callback);
		mClient = new DefaultHttpClient();
	}
}
