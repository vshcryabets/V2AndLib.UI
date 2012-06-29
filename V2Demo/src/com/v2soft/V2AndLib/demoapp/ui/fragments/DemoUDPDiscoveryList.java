package com.v2soft.V2AndLib.demoapp.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;
import com.v2soft.V2AndLib.demoapp.networking.DemoBroadcast;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class DemoUDPDiscoveryList 
extends BaseFragment<DemoApplication, DemoAppSettings>  {
    private DemoBroadcast mDiscovery;
    
    public static Fragment newInstance() {
        return new DemoUDPDiscoveryList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(com.v2soft.AndLib.UI.R.layout.fragment_button, null);
        view.findViewById(R.id.btnStart).setOnClickListener(this);
        try {
            mDiscovery = new DemoBroadcast(InetAddress.getByName("192.168.0.255"));
            mDiscovery.startReceiver(1235, InetAddress.getByName("192.168.0.0"));
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return view;
    }    

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if ( id == R.id.btnStart ) {
            mDiscovery.startDiscovery();
            return;
        }
    }
}
