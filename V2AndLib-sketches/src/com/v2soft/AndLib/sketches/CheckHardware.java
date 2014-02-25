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
package com.v2soft.AndLib.sketches;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;

/**
 * @author vshcryabets@gmail.com
 */
public class CheckHardware {
	protected Context mContext;

	public CheckHardware(Context context) {
		mContext = context;
	}

	/**
	 * Check that at least one provider from specified array are enabled.
	 * If all providers are disabled this method will show dialog to user.
	 * @param providers
	 */
	public void checkGPS(String [] providers) {
		checkGPS(providers, R.string.v2andlib_gps_disabled, R.string.v2andlib_gps_enable_message);
	}
	/**
	 * Check that at least one provider from specified array are enabled.
	 * If all providers are disabled this method will show dialog to user.
	 * @param providers providers array to check
	 * @param alertDialogTitle error dialog title resource
	 * @param alertDialogMessage error dialog message resource
	 */
	public void checkGPS(String [] providers, int alertDialogTitle, int alertDialogMessage) {
		LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		boolean enabled = false;
		for ( String provider : providers ) {
			enabled |= manager.isProviderEnabled(provider);
			if ( enabled ) {
				break;
			}
		}
		if ( !enabled ) {
			// show dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(alertDialogTitle);
			builder.setMessage(alertDialogMessage);
			builder.setNegativeButton(android.R.string.cancel, null);
			builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mContext.startActivity(
							new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				}
			});
			builder.create().show();
		}
	}
}
