/*
 * Copyright (C) 2012 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.V2AndLib.demoapp.ui.activities;

import com.v2soft.V2AndLib.demoapp.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class OpenSLSample 
extends Activity implements OnClickListener, OnTouchListener {
    private static final String TAG = OpenSLSample.class.getSimpleName();

    static {
        System.loadLibrary("native-audio-jni");
    }

    /** Native methods, implemented in jni folder */
    public static native void createEngine();
    public static native boolean createAudioRecorder();
    public static native void startRecording();
    public static native void shutdown();

    public static native void createPlayerEngine();
    public static native void createPlayers();
    public static native void stopPlayer(int idx);
    public static native void shutdownPlayers();
    public static native boolean startPlayer(int idx);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_media_sample);
        findViewById(R.id.btnStartPlay).setOnClickListener(this);
        findViewById(R.id.btnStartRecord).setOnClickListener(this);
        findViewById(R.id.btnStopPlay).setOnClickListener(this);
        findViewById(R.id.btnStopRecord).setOnClickListener(this);
        findViewById(R.id.btnS1).setOnTouchListener(this);
        findViewById(R.id.btnS2).setOnTouchListener(this);
        findViewById(R.id.btnS3).setOnTouchListener(this);
        findViewById(R.id.btnS4).setOnTouchListener(this);
        createEngine();
        createPlayerEngine();
        createAudioRecorder();
        createPlayers();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.btnStartRecord:
            startRecording();
            break;
        case R.id.btnStopRecord:
            shutdown();
            break;
        case R.id.btnStartPlay:
            startPlayer(0);
            break;
        case R.id.btnStopPlay:
            stopPlayer(0);
            break;
        default:
            break;
        }
    }

    @Override
    protected void onDestroy() {
        shutdownPlayers();
        super.onDestroy();
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        int action = event.getAction();
        int idx = -1;
        switch (id) {
        case R.id.btnS1:
            idx= 0;
            break;
        case R.id.btnS2:
            idx = 1;
            break;
        case R.id.btnS3:
            idx = 2;
            break;
        case R.id.btnS4:
            idx = 3;
            break;
        default:
            break;
        }
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            startPlayer(idx);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_OUTSIDE:
        case MotionEvent.ACTION_CANCEL:
            stopPlayer(idx);
            break;
        default:
            break;
        }
        return true;
    }
    
    /**
     * Return sample display name
     * @return
     */
    public static String getSampleName() {
        return "OpenSL sample";
    }
}
