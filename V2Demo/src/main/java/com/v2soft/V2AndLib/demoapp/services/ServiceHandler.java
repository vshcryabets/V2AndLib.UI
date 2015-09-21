package com.v2soft.V2AndLib.demoapp.services;

import android.app.Service;
import android.os.Looper;

import com.v2soft.AndLib.dataproviders.AbstractServiceTaskHandler;

/**
 * Service handler
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class ServiceHandler extends AbstractServiceTaskHandler {

    public ServiceHandler(Looper looper, Service context) {
        super(looper, context, ServiceConstants.ACTION_RECIVE,
                ServiceConstants.ACTION_SERVICE_ERROR);
    }

    @Override
    protected boolean shouldStop() {
        return true;
    }
}
