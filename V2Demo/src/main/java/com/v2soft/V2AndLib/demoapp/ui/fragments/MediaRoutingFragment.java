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
import android.app.MediaRouteActionProvider;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.v2soft.V2AndLib.demoapp.R;

/**
 * Wallpaper service usage sample.
 * @author vshcryabets@gmail.com
 *
 */
public class MediaRoutingFragment
		extends Fragment {

    public static Fragment newInstance() {
		return new MediaRoutingFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = new LinearLayout(getActivity());
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
		MediaRouteActionProvider mediaRouteActionProvider =
				(MediaRouteActionProvider)mediaRouteMenuItem.getActionProvider();
		mediaRouteActionProvider.setRouteTypes(
				android.media.MediaRouter.ROUTE_TYPE_LIVE_AUDIO
						| android.media.MediaRouter.ROUTE_TYPE_LIVE_VIDEO
						| android.media.MediaRouter.ROUTE_TYPE_USER);

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
	 * Return sample display name
	 * @return
	 */
	public static String getSampleName() {
		return "Media Routing client demo";
	}

}
