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

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.v2soft.AndLib.ui.fragments.BaseFragment;
import com.v2soft.V2AndLib.demoapp.DemoAppSettings;
import com.v2soft.V2AndLib.demoapp.DemoApplication;
import com.v2soft.V2AndLib.demoapp.R;

import java.util.ArrayList;
import java.util.List;

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

    private static final String TAG = GameServicesFragment.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;
    private static final int RC_SELECT_PLAYERS = 2;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIsResolving = false;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Set to true to automatically start the sign in flow when the Activity starts.
    // Set to false to require the user to click the button in order to sign in.
    private boolean mAutoStartSignInFlow = true;
    private View signInButton;
    private View inviteButton;

    public static Fragment newInstance() {
        return new GameServicesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_game, null, false);
        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .setViewForPopups(view.findViewById(R.id.popups))
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        signInButton = view.findViewById(R.id.sign_in_button);
        inviteButton = view.findViewById(R.id.invite);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignInClicked = true;
                mGoogleApiClient.connect();
            }
        });
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch the player selection screen
                // minimum: 1 other player; maximum: 3 other players
                Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
                startActivityForResult(intent, RC_SELECT_PLAYERS);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            mAutoStartSignInFlow = true;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
//        mGoogleApiClient.disconnect();
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
        signInButton.setVisibility(View.GONE);
        inviteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "onConnectionSuspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mIsResolving) {
            // The application is attempting to resolve this connection failure already.
            Log.d(TAG, "onConnectionFailed: already resolving");
            return;
        }

        Toast.makeText(getActivity(), "onConnectionFailed " + connectionResult.toString(), Toast.LENGTH_LONG).show();
        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mSignInClicked = false;
            mAutoStartSignInFlow = false;

            // Attempt to resolve the connection failure.
            Log.d(TAG, "onConnectionFailed: begin resolution.");
            mIsResolving = BaseGameUtils.resolveConnectionFailure(getActivity(), mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                Log.d(TAG, "onActivityResult: RC_SIGN_IN, resultCode = " + resultCode);
                mSignInClicked = false;
                mIsResolving = false;

                if (resultCode == Activity.RESULT_OK) {
                    // Sign-in was successful, connect the API Client
                    Log.d(TAG, "onActivityResult: RC_SIGN_IN (OK)");
                    mGoogleApiClient.connect();
                } else {
                    // There was an error during sign-in, display a Dialog with the appropriate message
                    // to the user.
                    Log.d(TAG, "onActivityResult: RC_SIGN_IN (Error)");
                    BaseGameUtils.showActivityResultError(getActivity(), requestCode, resultCode, R.string.signin_other_error);
                }
                break;
            case RC_SELECT_PLAYERS:
                if (resultCode != Activity.RESULT_OK) {
                    // user canceled
                    return;
                }

                // get the invitee list
                Bundle extras = data.getExtras();
                final ArrayList<String> invitees =
                        data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

                // get auto-match criteria
                Bundle autoMatchCriteria = null;
                int minAutoMatchPlayers =
                        data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
                int maxAutoMatchPlayers =
                        data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

                if (minAutoMatchPlayers > 0) {
                    autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                            minAutoMatchPlayers, maxAutoMatchPlayers, 0);
                } else {
                    autoMatchCriteria = null;
                }

                // create the room and specify a variant if appropriate
                RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
                roomConfigBuilder.addPlayersToInvite(invitees);
                if (autoMatchCriteria != null) {
                    roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
                }
                RoomConfig roomConfig = roomConfigBuilder.build();
                Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

                // prevent screen from sleeping during handshake
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // create a RoomConfigBuilder that's appropriate for your implementation
    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(roomUpdateListener)
                .setMessageReceivedListener(messageListener)
                .setRoomStatusUpdateListener(roomStatusListener);
    }

    private RoomUpdateListener roomUpdateListener = new RoomUpdateListener() {
        @Override
        public void onRoomCreated(int statusCode, Room room) {
            if (statusCode != GamesStatusCodes.STATUS_OK) {
                // let screen go to sleep
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                // show error message, return to main screen.
            }
        }

        @Override
        public void onJoinedRoom(int statusCode, Room room) {
            if (statusCode != GamesStatusCodes.STATUS_OK) {
                // let screen go to sleep
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                // show error message, return to main screen.
            }
        }

        @Override
        public void onLeftRoom(int statusCode, String s) {

        }

        @Override
        public void onRoomConnected(int statusCode, Room room) {
            if (statusCode != GamesStatusCodes.STATUS_OK) {
                // let screen go to sleep
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                // show error message, return to main screen.
            }
        }
    };

    private RealTimeMessageReceivedListener messageListener = new RealTimeMessageReceivedListener() {
        @Override
        public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {

        }
    };
    private RoomStatusUpdateListener roomStatusListener = new RoomStatusUpdateListener() {
        @Override
        public void onRoomConnecting(Room room) {

        }

        @Override
        public void onRoomAutoMatching(Room room) {

        }

        @Override
        public void onPeerInvitedToRoom(Room room, List<String> list) {

        }

        @Override
        public void onPeerDeclined(Room room, List<String> list) {

        }

        @Override
        public void onPeerJoined(Room room, List<String> list) {

        }

        @Override
        public void onPeerLeft(Room room, List<String> list) {

        }

        @Override
        public void onConnectedToRoom(Room room) {

        }

        @Override
        public void onDisconnectedFromRoom(Room room) {

        }

        @Override
        public void onPeersConnected(Room room, List<String> list) {

        }

        @Override
        public void onPeersDisconnected(Room room, List<String> list) {

        }

        @Override
        public void onP2PConnected(String s) {

        }

        @Override
        public void onP2PDisconnected(String s) {

        }
    };

}
