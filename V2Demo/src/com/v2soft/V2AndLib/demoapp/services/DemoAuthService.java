package com.v2soft.V2AndLib.demoapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.v2soft.V2AndLib.demoapp.providers.DemoAccountAuthenticator;

/**
 * Demonstration auth server.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class DemoAuthService extends Service {
    public static final String ACCOUNT_TYPE="com.v2soft.V2AndLib.demoapp.services.DemoAuthService";
    // Instance field that stores the authenticator object
    private DemoAccountAuthenticator mAuthenticator;
    
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new DemoAccountAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
