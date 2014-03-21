/*
 * Copyright (C) 2014 V.Shcryabets (vshcryabets@gmail.com)
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

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.v2soft.AndLib.media.CustomizableMediaPlayer;
import com.v2soft.AndLib.media.CustomizableMediaPlayerListener;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;

import java.io.IOException;

/**
 * Audio-Video streaming client sample.
 * @author vshcryabets@gmail.com
 *
 */
public class AudioStreamClientFragment
		extends BaseFragment<DemoApplication, DemoAppSettings> {
    private CustomizableMediaPlayer mPlayer;
    private TextView mPosition;

    public static Fragment newInstance() {
		return new AudioStreamClientFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.fragment_audio_streaming, null);
        mPosition = (TextView) view.findViewById(R.id.txtPosition);
		registerOnClickListener(new int[]{R.id.btnStartPlay, R.id.btnStopPlay}, view);
		return view;
	}

	@Override
	public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartPlay:
                mPlayer = new CustomizableMediaPlayer(getActivity(), new Handler());
                try {
                    mPlayer.setSourceUri(Uri.parse("http://142.4.200.46:8000/Trance3"), false);
                    mPlayer.play();
//                    mPlayer.setPositionListener(mPositionListener);
                } catch (Exception e) {
                    showError(e.toString());
                }
                break;
            case R.id.btnStopPlay:
                if (mPlayer != null ) {
                    mPlayer.stop();
                }
                break;
        }
	}

    @Override
    public void onPause() {
        super.onPause();
        // release player
        if ( mPlayer != null ) {
            mPlayer.close();
        }
    }

    /**
	 * Return sample display name
	 * @return
	 */
	public static String getSampleName() {
		return "Audio streaming client";
	}

    private CustomizableMediaPlayerListener mPositionListener = new CustomizableMediaPlayerListener() {
        @Override
        public void onUpdatePosition(int currentPosition) {
            mPosition.setText(String.valueOf(currentPosition/1000));
        }

        @Override
        public void onBeginPlay() {

        }

        @Override
        public void onPausePlay(boolean state) {

        }

        @Override
        public void onReadyStateChanged(boolean ready) {

        }

        @Override
        public void onFinishPlay() {

        }
    };
}
