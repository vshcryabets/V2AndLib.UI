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
package com.v2soft.AndLib.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.v2soft.AndLib.ui.R;

/**
 * View that show some image after start and hide it in few moments
 * @author vshcryabets@2vsoft.com
 *
 */
public class SplashScreenView extends FrameLayout
{	
    //----------------------------------------------------------------------------------------------
    // Variables	
    //----------------------------------------------------------------------------------------------
    private ImageView mImage;
    //----------------------------------------------------------------------------------------------
    // Constructor	
    //----------------------------------------------------------------------------------------------
    public SplashScreenView(Context context) {
        this(context, null);
    }
    public SplashScreenView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashScreenView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mImage = new ImageView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 
                LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        this.addView(mImage, params);
        mImage.setScaleType(ScaleType.FIT_XY);
        // parse attributes
        final TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.SplashScreenView,
                0, 0);
        setImageResource(arr.getDrawable(R.styleable.SplashScreenView_src),
                arr.getInt(R.styleable.SplashScreenView_delay, 1000));
        arr.recycle();
    }
    //-----------------------------------------------------------------------------------------------
    // Getters&Setters
    //-----------------------------------------------------------------------------------------------
    public void setImageResource(Drawable drawable, int delay)
    {
        if ( drawable != null ) {
            mImage.setImageDrawable(drawable);
            mImage.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SplashScreenView.this.setVisibility(View.INVISIBLE);
                    mImage.setImageBitmap(null);
                }
            }, delay);
        }
    }
}
