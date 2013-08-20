package com.v2soft.AndLib.dao;

import android.graphics.Bitmap;


/**
 * Bitmap container class interface
 * @author V.Shcryabets<vshcryabets@gmail.com>
 *
 */
public interface IBitmapContainer {
    public boolean isBitmapLoaded(int id);
    public void prepareBitmap(int id);
    public void releaseBitmap(int id);
    public Bitmap getBitmap(int id);
}
