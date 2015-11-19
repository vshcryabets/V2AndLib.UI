/*
 * Copyright (C) 2015 V.Shcryabets (vshcryabets@gmail.com)
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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;

/**
 * Google Play Game service usage sample.
 *
 * @author vshcryabets@gmail.com
 */
public class GameServicesFragment
        extends BaseFragment<DemoApplication, DemoAppSettings>
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    public static Fragment newInstance() {
        return new GameServicesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_google_game, null, false);
        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .setViewForPopups(view.findViewById(R.id.popups))
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        // add other APIs and scopes here as needed
                .build();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        mGoogleApiClient.disconnect();
        super.onPause();
    }

    /**
     * Return sample display name
     *
     * @return
     */
    public static String getSampleName() {
        return "Google Play Game Service demo";
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getActivity(), "onConnected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "onConnectionSuspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "onConnectionFailed " + connectionResult.toString(), Toast.LENGTH_LONG).show();
    }
}
