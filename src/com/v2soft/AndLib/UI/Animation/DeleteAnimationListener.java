package com.v2soft.AndLib.UI.Animation;

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
