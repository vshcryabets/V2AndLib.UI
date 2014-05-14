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

import android.app.Fragment;
import android.graphics.Canvas;
import android.location.LocationManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.v2soft.AndLib.sketches.CheckHardware;
import com.v2soft.AndLib.tricks.DataConnection;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;

/**
 * System tricks demo fragment.
 * @author vshcryabets@gmail.com
 *
 */
public class DemoPDF
extends BaseFragment<DemoApplication, DemoAppSettings>  {
    private static final String LOG_TAG = DemoPDF.class.getSimpleName();

    protected PDFView mPDFView;

    public static Fragment newInstance() {
        return new DemoPDF();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_pdf, null);
        mPDFView = (PDFView) view.findViewById(R.id.pdfview);
        mPDFView.fromAsset("BT139_SERIES.pdf")
                .defaultPage(0)
                .showMinimap(false)
                .enableSwipe(true)
                .onDraw(onDrawListener)
                .onLoad(onLoadCompleteListener)
                .onPageChange(onPageChangeListener)
                .load();

        return view;
    }

    /**
     * Return sample display name
     * @return
     */
    public static String getSampleName() {
        return "PDF viewer";
    }


    @Override
    public void onClick(View v) {
        switch ( v.getId() ) {
        default:
            break;
        }
    }

    private OnDrawListener onDrawListener = new OnDrawListener() {
        @Override
        public void onLayerDrawn(Canvas canvas, float v, float v2, int i) {

        }
    };

    private OnLoadCompleteListener onLoadCompleteListener = new OnLoadCompleteListener() {
        @Override
        public void loadComplete(int i) {

        }
    };

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageChanged(int i, int i2) {

        }
    };

}
