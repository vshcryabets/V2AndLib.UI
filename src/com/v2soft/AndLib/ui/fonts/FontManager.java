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
