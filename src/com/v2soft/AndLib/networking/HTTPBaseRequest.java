package com.v2soft.AndLib.networking;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public class HTTPBaseRequest implements Runnable {
	// ====================================================
	// Constants
	// ====================================================
	private static final String LOG_TAG = HTTPBaseRequest.class.getSimpleName();
	// ====================================================
	// Constructors
	// ====================================================
	protected HttpClient mClient;
	
	public HTTPBaseRequest() {
		mClient = new DefaultHttpClient();
	}
	
	@Override
	public void run() {
		// prepare parameters
		// send request
		// parse results
		// delivery results
	}

}
