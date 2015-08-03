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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaControlIntent;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.v2soft.V2AndLib.demoapp.R;

/**
 * Media Router usage sample.
 * @author vshcryabets@gmail.com
 *
 */
public class MediaRoutingFragment
		extends Fragment {

	private static final String TAG = MediaRoutingFragment.class.getSimpleName();
	private MediaRouter mMediaRouter;
	private MediaRouteSelector mSelector;


	public static Fragment newInstance() {
		return new MediaRoutingFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = new LinearLayout(getActivity());
		mMediaRouter = MediaRouter.getInstance(getActivity());
		mSelector =	new MediaRouteSelector.Builder()
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)
                .addControlCategory(MediaControlIntent.CATEGORY_LIVE_VIDEO)
                .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                .build();
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
	}

	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_mediarouter, menu);

		MenuItem mediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
		MediaRouteActionProvider provider =
				(MediaRouteActionProvider) MenuItemCompat.getActionProvider(mediaRouteMenuItem);
		provider.setRouteSelector(mSelector);
        super.onCreateOptionsMenu(menu, inflater);
    }

	@Override
	public void onResume() {
		super.onResume();
		mMediaRouter.addCallback(mSelector, mMediaRouterCallback,
				MediaRouter.CALLBACK_FLAG_PERFORM_ACTIVE_SCAN);
	}

	@Override
	public void onPause() {
		super.onPause();
        mMediaRouter.removeCallback(mMediaRouterCallback);
	}

	private MediaRouter.Callback mMediaRouterCallback = new MediaRouter.Callback() {
		@Override
		public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo route) {
			super.onRouteSelected(router, route);
			Log.d(TAG, "Connected to " + route.getName());
			Toast.makeText(getActivity().getApplicationContext(), "Connected to " + route.getName(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo route) {
			super.onRouteUnselected(router, route);
		}
	};

	/**
	 * Return sample display name
	 * @return
	 */
	public static String getSampleName() {
		return "Media Routing client demo";
	}

}
