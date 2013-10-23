/*
 * Copyright (C) 2013 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.text;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.v2soft.AndLib.other.R;

import android.content.Context;

/**
 * Message date fromatter
 * @author V.Shcriyabets (vshcryabets@gmail.com)
 *
 */
public class MessageDateString {
    public static String format(Context context, Date date, SimpleDateFormat defaultFormat) {
        String result = "";
        long diff = (System.currentTimeMillis() - date.getTime())/1000/60;
        if ( diff < 0 ) {
            result = defaultFormat.format(date);
        } else if ( diff < 3 ) {
            result = context.getString(R.string.v2andlib_date_format_just_now);
        } else {

            if ( diff < 60 ) {
                result = context.getString(R.string.v2andlib_date_format_minutes, diff);
            } else if ( diff < 12*60 ) {
                result = context.getString(R.string.v2andlib_date_format_hours, diff/60);
            } else {
                result = defaultFormat.format(date);
            }
        }
        return result;
    }
}
