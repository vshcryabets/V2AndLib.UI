/*
 * Copyright (C) 2012 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.v2soft.AndLib.ui.R;
import com.v2soft.AndLib.ui.activities.BaseActivity;
import com.v2soft.AndLib.ui.fonts.FontManager;

/**
 * View extender that hides it content after specified time
 * @author vshcryabets@2vsoft.com
 *
 */
public class TextViewWithFont extends TextView {
    private static final String LOG_TAG = TextViewWithFont.class.getSimpleName();
    //----------------------------------------------------------------------------------------------
    // Constructor	
    //----------------------------------------------------------------------------------------------
    public TextViewWithFont(Context context) {
        this(context, null);
    }
    public TextViewWithFont(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public TextViewWithFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if ( getContext() instanceof BaseActivity ) {
            // we can get font manager
            final FontManager fm = ((BaseActivity)getContext()).getFontManager();
            final TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.TextViewWithFont,
                    0, 0);
            final String fontName = arr.getString(R.styleable.TextViewWithFont_fontName);
            if ( fontName != null ) {
                try {
                    setTypeface(fm.getFont(fontName));
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString(), e);
                }
            }
            arr.recycle();

        }
    }
}