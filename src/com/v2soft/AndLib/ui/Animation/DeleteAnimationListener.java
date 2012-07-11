/*
 * Copyright (C) 2010 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.ui.Animation;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class DeleteAnimationListener implements AnimationListener
{
	private static final String LOG_TAG = DeleteAnimationListener.class.getSimpleName();
	private View mView;
	private boolean to_visible;
	
	public DeleteAnimationListener(View item, boolean to_visible) 
	{
		this.mView = item;
		this.to_visible = to_visible;
	}
	
	public void onAnimationEnd(Animation animation) {
		if ( !to_visible ) {
			mView.setVisibility(View.GONE);
			if ( mView instanceof ViewGroup ) {
				ViewGroup vg = (ViewGroup) mView;
				for ( int i = 0; i < vg.getChildCount(); i++ ) {
					vg.getChildAt(i).setVisibility(View.GONE);
				}
			}
			Log.d(LOG_TAG, "View is gone");
		}
    }

	public void onAnimationRepeat(Animation animation){}

	public void onAnimationStart(Animation animation) {
		if ( to_visible ) {
			mView.setVisibility(View.VISIBLE);
			if ( mView instanceof ViewGroup ) {
				ViewGroup vg = (ViewGroup) mView;
				for ( int i = 0; i < vg.getChildCount(); i++ ) {
					vg.getChildAt(i).setVisibility(View.VISIBLE);
				}
			}
			Log.d(LOG_TAG, "View is visible");
		}
    }

}
