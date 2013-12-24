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
package com.v2soft.V2AndLib.demoapp.ui.fragments;

import java.io.File;
import java.util.UUID;

import android.media.CameraProfile;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.AndLib.ui.views.CameraView;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;

/**
 * CameraView sample.
 * @author vshcryabets@gmail.com
 *
 */
public class DemoCamera 
extends BaseFragment<DemoApplication, DemoAppSettings>  {
    private CameraView mCameraView;
    
    public static Fragment newInstance() {
        return new DemoCamera();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_camera, null);
        mCameraView = (CameraView) view;
        return view;
    }
    
    @Override
    public void onClick(View v) {
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_camera, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ( item.getItemId() == R.id.menu_take_video ) {
            try {
                if ( mCameraView.isRecording() ) {
                    mCameraView.stopRecording();
                } else {
                    mCameraView.getSupportedVideoProfiles();
                    mCameraView.startVideoRecording(
                            getActivity().getExternalCacheDir()+File.separator+UUID.randomUUID().toString()+".mp4",
                            CameraProfile.QUALITY_LOW);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if ( item.getItemId() == R.id.menu_take_photo ) {
            //mCameraView.takePicture(path, portrait, flash_disable);
        }
        return super.onOptionsItemSelected(item);
    }
}
