// ***** BEGIN LICENSE BLOCK *****
// Version: MPL 1.1
// 
// The contents of this file are subject to the Mozilla Public License Version 
// 1.1 (the "License"); you may not use this file except in compliance with 
// the License. You may obtain a copy of the License at 
// http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
// for the specific language governing rights and limitations under the
// License.
// 
// The Initial Developer of the Original Code is 
//	2V Software (vshcryabets@2vsoft.com).
// Portions created by the Initial Developer are Copyright (C) 2010
// the Initial Developer. All Rights Reserved.
// 
// 
// ***** END LICENSE BLOCK *****
package com.v2soft.AndLib.UI;

import com.v2soft.AndLib.Timers.SpecialTimer;
import com.v2soft.AndLib.Timers.SpecialTimerListener;
import com.v2soft.AndLib.UI.Animation.DeleteAnimationListener;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * View extender that hides it content after specified time
 * @author vshcryabets@2vsoft.com
 *
 */
public class AutoHideView implements OnTouchListener, SpecialTimerListener
{	
	private static final String LOG_TAG = AutoHideView.class.getSimpleName();
	//----------------------------------------------------------------------------------------------
	// Variables	
	//----------------------------------------------------------------------------------------------
	private Animation anim_fadein, anim_fadeout;
	private View view;
	private SpecialTimer animationTimer;
	private int mMaxTimer = 20;
	//----------------------------------------------------------------------------------------------
	// Constructor	
	//----------------------------------------------------------------------------------------------
	public AutoHideView(View view, int max)
	{
		mMaxTimer = max;
		anim_fadein = AnimationUtils.loadAnimation(view.getContext(), R.anim.andlib_fadein);
		anim_fadein.setAnimationListener(
				new DeleteAnimationListener(view,true));

		anim_fadeout = AnimationUtils.loadAnimation(view.getContext(), R.anim.andlib_fadeout);
		anim_fadeout.setAnimationListener(
				new DeleteAnimationListener(view,false));
		view.setOnTouchListener(this);
		view.setVisibility(View.GONE);
		this.view = view;
	}

	public void show() {
		if ( view.getVisibility() == View.VISIBLE )	{
			if ( (view.getAnimation()!=null) && ( view.getAnimation().equals(anim_fadeout)))
			{
				view.setAnimation(null);
				anim_fadeout.reset();
				return;
			} else {
				// continue timer
				animationTimer.prolongTimer();
				return;
			}
		} else {
			startShowAnimation();
			animationTimer = new SpecialTimer(100, this, "For view "+view.toString());
			animationTimer.setTimerMax(mMaxTimer);
		}
	}
	
	public void hide() {
		Log.d(LOG_TAG, "hide");
		if ( view.getVisibility() == View.VISIBLE )
		{
			Log.d(LOG_TAG, "hide2");
			animationTimer.start();
		}
	}
	
	public void startShowAnimation()
	{
		view.post(new Runnable() {
			public void run() {
				if ( anim_fadeout.hasStarted() )
				{
					anim_fadeout.reset();
				}
				view.setVisibility(View.VISIBLE);
    			view.startAnimation(anim_fadein);    			
			}
		});
	}
	
	public void startHideAnimation() {
		Log.d(LOG_TAG, "startHideAnimation");
		view.post(new Runnable() {
			public void run() {
    			view.startAnimation(anim_fadeout);
			}
		});
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) 
	{
    	if ( event.getAction() == MotionEvent.ACTION_DOWN )	{
    		show();
    		return true;
    	} else if ( event.getAction() == MotionEvent.ACTION_UP ) {
    		hide();
    		return true;
    	}
    	return view.onTouchEvent(event);
	}
	//----------------------------------------------------------------------------------------------
	// Timer listener	
	//----------------------------------------------------------------------------------------------
	@Override
	public void onTimer() {
		startHideAnimation();
	}

}
