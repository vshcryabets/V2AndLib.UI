// ***** BEGIN LICENSE BLOCK *****
// Version: MPL 1.1
// 
// The contents of this file are subject to the Mozilla Public License Version 
// 1.1 (the "License"); you may not use this file except in compliance with 
// the License. You may obtain a copy of the License at 
// http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
// for the specific language governing rights and limitations under the
// License.
// 
// The Initial Developer of the Original Code is 
//	2V Software (vshcryabets@2vsoft.com).
// Portions created by the Initial Developer are Copyright (C) 2010
// the Initial Developer. All Rights Reserved.
// 
// 
// ***** END LICENSE BLOCK *****
package com.v2soft.AndLib.ui.views;

import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements PictureCallback
{
    private static final String LOG_TAG = CameraView.class.getSimpleName();
    private String out_file_name = null;
    private Camera mCamera;
    private SurfaceHolder mPreviewHolder;
    private boolean preview_running = false;
    private OnClickListener listener = null;

    public CameraView(Context context) {
        this(context,null);
    }
    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mPreviewHolder = this.getHolder();
        mPreviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mPreviewHolder.addCallback(surfaceHolderListener);
    }

    public void setOnClickListener(OnClickListener listener)
    {
        this.listener = listener;
    }

    private void updatePreviewState(int width, int height)
    {
        try
        {
            if ( preview_running )
            {
                mCamera.stopPreview();
            }
            Parameters params = mCamera.getParameters();
            params.setPreviewSize(width, height);
            params.setPictureFormat(PixelFormat.JPEG);
            mCamera.setParameters(params);
            mCamera.setPreviewDisplay(mPreviewHolder);
            mCamera.startPreview();
            preview_running = true;
        }
        catch (Exception e) 
        {
            Log.e(LOG_TAG, e.toString(), e);
        }
    }

    SurfaceHolder.Callback surfaceHolderListener = new SurfaceHolder.Callback() 
    {
        public void surfaceCreated(SurfaceHolder holder) 
        {
            try {
                Log.d(LOG_TAG, "open camera");
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(mPreviewHolder);
            }
            catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
            }           
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height)	{
            try	{
                updatePreviewState(width, height);
            }
            catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
            }			
        }

        public void surfaceDestroyed(SurfaceHolder arg0) {
            mCamera.stopPreview();
            preview_running = false;
            mCamera.release();
            mCamera = null;
        }
    };

    public void takePicture(String path, boolean portrait, boolean flash_disable ) 
    {
        if ( mCamera == null ) return;
        out_file_name = path;
        Parameters parameters =  mCamera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        if ( portrait )
        {
            parameters.set("rotation", 90);
        }
        if ( flash_disable )
            parameters.set("flash-mode", "off");
        mCamera.setParameters(parameters);
        mCamera.takePicture(null,null,this);
    }

    public void onPictureTaken(byte[] data, Camera camera) 
    {
        if ( data == null) return;
        if ( out_file_name == null ) return;
        FileOutputStream out = null;
        try 
        {
            out = new FileOutputStream(out_file_name);
            out.write(data);
            // register in MediaStore
            MediaStore.Images.Media.insertImage(
                    getContext().getContentResolver(), 
                    out_file_name, null, null);

            if ( listener!=null )
                listener.onClick(this);
        } catch (Exception e) 
        {
            Log.e(LOG_TAG, e.toString(), e);
        }
        finally
        {
            if ( out != null )
                try {out.close();}catch (IOException e){}
            if ( preview_running )
                mCamera.stopPreview();
            mCamera.startPreview();
        }
    }

    public String getFileName() 
    {
        return out_file_name;
    }
}
