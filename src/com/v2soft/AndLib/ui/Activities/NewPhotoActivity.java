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
//  2V Software (vshcryabets@2vsoft.com).
// Portions created by the Initial Developer are Copyright (C) 2010
// the Initial Developer. All Rights Reserved.
// 
// 
// ***** END LICENSE BLOCK *****
package com.v2soft.AndLib.ui.Activities;

import com.v2soft.AndLib.ui.views.CameraView;

import android.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.FrameLayout.LayoutParams;

public class NewPhotoActivity extends Activity implements OnClickListener
{
	//----------------------------------------------------------------------------------------------
	// Constants	
	//----------------------------------------------------------------------------------------------
	public static final String ACTIVITY_RESULT_FILENAME = "RESULT";
	public static final String ACTIVITY_PARAM_FILENAME = "FILENAME";
    private static final String LOG_TAG = NewPhotoActivity.class.getSimpleName();
	//----------------------------------------------------------------------------------------------
	// Variables	
	//----------------------------------------------------------------------------------------------	
	private CameraView mCameraView;
	private ImageButton mTakePhotoUI;
	private String mPhotoFileName;
	//----------------------------------------------------------------------------------------------
	// Activity routines	
	//----------------------------------------------------------------------------------------------	

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		try
		{
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);

			Bundle extras = this.getIntent().getExtras();
			mPhotoFileName = extras.getString("FILENAME");

			mCameraView = new CameraView(this.getApplicationContext());
			FrameLayout rl = new FrameLayout(this.getApplicationContext());
			setContentView(rl);
			rl.addView(mCameraView);           
			mCameraView.setOnClickListener(this);
			mTakePhotoUI = new ImageButton(this);
			mTakePhotoUI.setImageResource(R.drawable.ic_menu_camera);
			mTakePhotoUI.setOnClickListener(this);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.RIGHT;
			rl.addView(mTakePhotoUI, params);
		} 
		catch(Exception e) {
		    Log.e(LOG_TAG, e.toString(), e);
		}
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) 
	{
		if ( ( keyCode == KeyEvent.KEYCODE_DPAD_CENTER ) ||
				( keyCode == KeyEvent.KEYCODE_CAMERA ) )
		{
			takePhoto();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void onClick(View v) 
	{
		if ( v.equals(mCameraView) )
		{
			// return result and close activity
			Intent resultIntent = new Intent();
			resultIntent.putExtra(ACTIVITY_RESULT_FILENAME, mCameraView.getFileName());
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}
		if ( v.equals(mTakePhotoUI))
		{
			takePhoto();
		}
	}

	private void takePhoto() 
	{
//		String path = Config.getInstance().getCacheDir()+
//		File.separator+UUID.randomUUID()+".jpg";
//		boolean portrait = false;
//		if ( ( current_orientation < 360 ) && ( current_orientation > 310 ))
//			portrait = true;
//		if ( ( current_orientation < 230 ) && ( current_orientation > 130 ))
//			portrait = true;
//		if ( ( current_orientation < 50 ) && ( current_orientation > -1 ))
//			portrait = true;
		mCameraView.takePicture(mPhotoFileName, false, false);	
	}
}
