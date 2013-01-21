/*
 * Copyright (C) 2012-2013 V.Shcryabets (vshcryabets@gmail.com)
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
import android.view.ContextThemeWrapper;
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
        Context subcontext = context;
        BaseActivity<?, ?> activity = null;
        if ( subcontext instanceof BaseActivity ) {
            // may be this is dialog?
            activity = (BaseActivity<?, ?>) subcontext;
        } else {
            if ( context instanceof ContextThemeWrapper ) {
                subcontext = ((ContextThemeWrapper)context).getBaseContext();
            }
            if ( subcontext instanceof BaseActivity ) {
                // may be this is dialog?
                activity = (BaseActivity<?, ?>) subcontext;
            }
        }
        if ( activity != null ) {
            // we can get font manager
            final FontManager fm = activity.getFontManager();
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
