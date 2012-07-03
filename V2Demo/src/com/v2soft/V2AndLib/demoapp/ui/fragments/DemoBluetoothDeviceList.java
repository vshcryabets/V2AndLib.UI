package com.v2soft.V2AndLib.demoapp.ui.fragments;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.view.View;
import android.widget.AdapterView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.v2soft.AndLib.ui.fragments.BluetoothDeviceList;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;

/**
 * 
 * @author vshcryabets@gmail.com
 *
 */
public class DemoBluetoothDeviceList 
extends BluetoothDeviceList<DemoApplication, DemoAppSettings>  {
    private static final UUID SPP_UUID = 
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    
    public static Fragment newInstance() {
        return new DemoBluetoothDeviceList();
    }
    
    @Override
    public void onClick(View v) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        BluetoothDevice device = (BluetoothDevice) mAdapter.getItem(position);
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
            socket.connect();
            InputStream ins = socket.getInputStream();
            OutputStream outs = socket.getOutputStream();
            outs.write("AT\n".getBytes());
            outs.flush();
            byte[] buffer = new byte[1024];
            int read = ins.read(buffer);
            String answer = new String(buffer, 0, read);
            System.out.println(answer);
            
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
