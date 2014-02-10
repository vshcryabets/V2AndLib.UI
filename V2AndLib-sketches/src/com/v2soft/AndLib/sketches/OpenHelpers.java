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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Open different types of files via intents.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 *
 */
public class OpenHelpers {
	/**
	 * Open local PDF file.
	 */
	public static void openLocalPDFFile(Context context, Uri uri, int chooserTitle, int noActivityError){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "application/pdf");
		intent = Intent.createChooser(intent, context.getString(chooserTitle));
		try {
			if ( intent != null ) {
				context.startActivity(intent);
			}
		}
		catch (ActivityNotFoundException e) {
			Toast.makeText(context,	noActivityError, Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * Open remote PDF file.
	 */
	public static void openRemotePDFFile(Context context, Uri uri, int chooserTitle, int noActivityError){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "application/pdf");
		intent = Intent.createChooser(intent, context.getString(chooserTitle));
		try {
			if ( intent != null ) {
				context.startActivity(intent);
			}
		}
		catch (ActivityNotFoundException e) {
			Toast.makeText(context,	noActivityError, Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * Open any PDF file.
	 */
	public static void openAnyPDFFile(Context context){

	}

	public static void openAdobeReader(Context context, Uri uri, int chooserTitle, int noActivityError) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "application/pdf");
		intent.setPackage("com.adobe.reader");
		context.startActivity(intent);
	}
}

