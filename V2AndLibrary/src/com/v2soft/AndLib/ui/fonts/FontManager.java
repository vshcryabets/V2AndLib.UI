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
package com.v2soft.AndLib.ui.fonts;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Class that responsible for a custom application fonts
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public class FontManager {
    private Map<String, Typeface> mFonts;
    private Context mContext;
    
    public FontManager(Context context) {
        mContext = context;
        mFonts = new HashMap<String, Typeface>();
    }
    
    public void addFontFromAssets(String fontName, String fontPath) throws IllegalStateException {
        if (mFonts.containsKey(fontName)) {
            throw new IllegalStateException("Fonts pull already contains this font");
        }
        final Typeface tf = Typeface.createFromAsset(mContext.getAssets(), fontPath);
        mFonts.put(fontName, tf);
    }
    
    /**
     * 
     * @param fontName
     * @return font registered with fontName, or throws NoSuchElementException exception
     */
    public Typeface getFont(String fontName) {
        if (mFonts.containsKey(fontName)) {
            return mFonts.get(fontName);
        } else {
            throw new NoSuchElementException("There is no such font with name "+fontName);
        }
        
    }
}
