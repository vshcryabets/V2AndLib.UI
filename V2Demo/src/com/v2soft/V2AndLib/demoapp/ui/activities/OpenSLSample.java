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

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.v2soft.AndLib.ui.R;

/**
 * 
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class OpenSLSample 
extends Activity implements OnClickListener {
    private static final String TAG = OpenSLSample.class.getSimpleName();
    
    static {
        System.loadLibrary("native-audio-jni");
    }
    
    /** Native methods, implemented in jni folder */
    public static native void createEngine();
    public static native boolean createAudioRecorder();
    public static native void startRecording();
    public static native void shutdown();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_button);
        findViewById(R.id.btnStart).setOnClickListener(this);
        createEngine();
        createAudioRecorder();
    }

    @Override
    public void onClick(View v) {
        startRecording();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        shutdown();
    }
}
