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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.v2soft.AndLib.ui.fragments.DropboxUploadFragment;
import com.v2soft.V2AndLib.demoapp.R;

/**
 * Activity taht upload to Dropbox test file
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class DropBoxUpload extends Activity implements OnClickListener {
    ///////////////////////////////////////////////////////////////////////////
    //                      Your app-specific settings.                      //
    ///////////////////////////////////////////////////////////////////////////
    // Replace this with your app key and secret assigned by Dropbox.
    // Note that this is a really insecure way to do this, and you shouldn't
    // ship code which contains your key & secret in such an obvious way.
    // Obfuscation is good.
    final static private String APP_KEY = "01ljvjlj0pbrow6";
    final static private String APP_SECRET = "yknmjmfogs868ad";
    private static final String ACTION_DROPBOX_UPLOAD = "com.v2soft.AndLib.demoapp.ACTION_DROPBOX_UPLOAD";
    ///////////////////////////////////////////////////////////////////////////
    //                      End app-specific settings.                       //
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_button);
        findViewById(R.id.btnStart).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            DropboxUploadFragment frag;
            frag = DropboxUploadFragment.newInstance(this, "/sdcard/res1.wav", "cloc",
                    APP_KEY, APP_SECRET,
                    ACTION_DROPBOX_UPLOAD);
            frag.show(getFragmentManager(), "dialog");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Return sample display name
     * @return
     */
    public static String getSampleName() {
        return "Upload to dropbox";
    }
}