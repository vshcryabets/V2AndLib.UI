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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

/**
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class HorizontalProgressDialog extends ProgressDialog {
	public HorizontalProgressDialog(Context context, int theme, int title, int message, int maxValue,
									boolean showAfterCreate) {
		super(context, theme);
		setTitle(title);
		setMessage(context.getString(message));
		// change style
		setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// progress maximum
		if ( maxValue >0 ) {
			setMax(maxValue);
			setIndeterminate(false);
		} else {
			setIndeterminate(true);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				setProgressPercentFormat(null);
			}
		}
		if (showAfterCreate) {
			show();
		}
	}
}
