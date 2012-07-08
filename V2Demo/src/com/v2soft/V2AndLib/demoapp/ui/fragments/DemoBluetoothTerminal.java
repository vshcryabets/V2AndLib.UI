package com.v2soft.V2AndLib.demoapp.ui.fragments;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;

/**
 * Bluetooth terminal 
 * @author vshcryabets@gmail.com
 *
 */
public class DemoBluetoothTerminal 
extends BaseFragment<DemoApplication, DemoAppSettings>  {
    private static final UUID SPP_UUID = 
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String EXTRA_DEVICE = "device";
    private ArrayAdapter<String> mAdapter;
    private SocketReader mReader;

    public static Fragment newInstance(BluetoothDevice device) {
        final Bundle extra = new Bundle();
        extra.putParcelable(EXTRA_DEVICE, device);
        Fragment fragment = new DemoBluetoothTerminal();
        fragment.setArguments(extra);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(com.v2soft.AndLib.UI.R.layout.v2andlib_fragment_list, null);
        ListView list = (ListView) view.findViewById(android.R.id.list);
        list.setDivider(null);
        list.setDescendantFocusability(ListView.FOCUS_BEFORE_DESCENDANTS);
        list.setFastScrollEnabled(false);
        list.setSmoothScrollbarEnabled(true);
        list.setItemsCanFocus(false);
        list.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mAdapter = new ArrayAdapter<String>(getActivity(), 
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        list.setAdapter(mAdapter);
        return view;
    }


    @Override
    public void onClick(View v) {
    }

    @Override
    public void onResume() {
        BluetoothDevice device = getArguments().getParcelable(EXTRA_DEVICE);
        try {
            mReader = new SocketReader();
            mReader.execute(device);
        } catch (Exception e) {
            // TODO: handle exception
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        try {
            mReader.cancel(true);
        } catch (Exception e) {
            // TODO: handle exception
        }
        super.onPause();
    }

    private class SocketReader extends AsyncTask<BluetoothDevice, String, Void> {

        @Override
        protected Void doInBackground(BluetoothDevice... params) {
            try {
                final BluetoothSocket socket = params[0].createRfcommSocketToServiceRecord(SPP_UUID);
                socket.connect();
                final InputStream is = socket.getInputStream();
                final OutputStream os = socket.getOutputStream();
                final BufferedReader bur = new BufferedReader(new InputStreamReader(is));

                while ( !isCancelled() ) {
                    try {
                        String answer = bur.readLine();
                        publishProgress(answer);
                    } catch (Exception e) {
                    }
                }
                bur.close();
                is.close();
                os.close();
                socket.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if ( mAdapter.getCount() > 100 ) {
                mAdapter.remove(mAdapter.getItem(0));
            }
            mAdapter.add(values[0]);
        }
    }
}
