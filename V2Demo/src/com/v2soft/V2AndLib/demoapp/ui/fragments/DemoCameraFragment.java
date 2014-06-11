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
import java.io.IOException;
import java.util.UUID;

import android.app.Fragment;
import android.hardware.Camera;
import android.media.CameraProfile;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.v2soft.AndLib.filecache.AndroidFileCache;
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
public class DemoCameraFragment
extends BaseFragment<DemoApplication, DemoAppSettings>  {
    private static final String TAG = DemoCameraFragment.class.getSimpleName();
    private CameraView mCameraView;
    protected boolean mVideoMode = false;
    protected AndroidFileCache mCache;
    protected boolean isVideoRecording;

    public static Fragment newInstance() {
        return new DemoCameraFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        isVideoRecording = false;
        final View view = inflater.inflate(R.layout.fragment_camera, null);
        mCameraView = (CameraView) view.findViewById(R.id.cameraView);
        view.findViewById(R.id.btnShoot).setOnClickListener(this);
        ((CheckBox)view.findViewById(R.id.video)).setOnCheckedChangeListener(mCheckListener);
        mCache = new AndroidFileCache.Builder(getActivity()).useExternalCacheFolder("CAMERA").build();
        return view;
    }
    
    @Override
    public void onClick(View v) {
        if ( !mVideoMode ) {
            takePhoto();
        } else {
            if ( isVideoRecording ) {
                stopVideoRecorder();
            } else {
                startVideoRecorder();
            }
        }
    }

    private void stopVideoRecorder() {
        if ( mCameraView.isRecording() ) {
            mCameraView.stopRecording();
        }
        isVideoRecording = false;
    }

    private void startVideoRecorder() {
        stopVideoRecorder();
        isVideoRecording = true;
        UUID uuid = UUID.randomUUID();
        File path = new File(mCache.getCacheFolder(), uuid.toString()+".mp4");
        try {
            mCameraView.startVideoRecorder(path);
        } catch (IOException e) {
            Log.d(TAG, e.toString(), e);
        }

    }

    private void takePhoto() {
        UUID uuid = UUID.randomUUID();
        File path = new File(mCache.getCacheFolder(), uuid.toString()+".jpeg");
        mCameraView.takePhoto(new CameraView.DefaultPhotoHandler(getActivity(), path, mCameraView.getCamera()){
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                super.onPictureTaken(data, camera);
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(getActivity(), notification);
                    r.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private CompoundButton.OnCheckedChangeListener mCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mVideoMode = isChecked;
        }
    };
}
