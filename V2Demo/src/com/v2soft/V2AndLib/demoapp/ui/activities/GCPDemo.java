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

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.v2soft.V2AndLib.demoapp.R;

/**
 * Google Cloud printing sample
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class GCPDemo 
extends DemoBaseActivity implements OnClickListener {
    private static final String TAG = GCPDemo.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_button);
        findViewById(R.id.btnStart).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent printIntent = new Intent(this, PrintDialogActivity.class);
        printIntent.setDataAndType(Uri.fromFile(new File("/sdcard/test.jpeg")), "image/jpeg");
        printIntent.putExtra("title", "Photo test");
        startActivity(printIntent);
    }

    /**
     * Return sample display name
     * @return
     */
    public static String getSampleName() {
        return "Google Cloud Printing Demo";
    }
}
