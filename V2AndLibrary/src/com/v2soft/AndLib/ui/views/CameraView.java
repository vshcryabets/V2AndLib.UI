/*
 * Copyright (C) 2011-2013 V.Shcryabets (vshcryabets@gmail.com)
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

import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.os.Build;
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
    private boolean isPreviewRunning = false;
    private OnClickListener listener = null;

    public CameraView(Context context) {
        this(context,null);
    }
    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPreviewHolder = getHolder();
        if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ) {
            mPreviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mPreviewHolder.addCallback(mSurfaceHolderListener);
    }

    /**
     * Check does this device has a camera.
     * @return true if this device has at least one camera
     */
    public static boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    SurfaceHolder.Callback mSurfaceHolderListener = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(mPreviewHolder);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
            }           
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height)	{
            try	{
                if ( isPreviewRunning ) {
                    mCamera.stopPreview();
                }
                Parameters params = mCamera.getParameters();
                params.setPreviewSize(width, height);
                params.setPictureFormat(ImageFormat.JPEG);

                mCamera.setParameters(params);
                mCamera.setPreviewDisplay(mPreviewHolder);
                mCamera.startPreview();
                isPreviewRunning = true;
            }
            catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
            }			
        }

        public void surfaceDestroyed(SurfaceHolder arg0) {
            if ( mCamera != null ) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            isPreviewRunning = false;
        }
    };

    public void takePicture(String path, boolean portrait, boolean flash_disable ) {
        if ( mCamera == null ) {
            return;
        }
        out_file_name = path;
        Parameters parameters =  mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
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
            if ( isPreviewRunning )
                mCamera.stopPreview();
            mCamera.startPreview();
        }
    }

    public String getFileName() 
    {
        return out_file_name;
    }
    
    /**
     * Start video recording.
     * 
     */
    public void startVideoRecording(String outputFilePath) {
        if ( mCamera == null ) {
            throw new IllegalStateException("Camera wasn't initialized");
        }
        mCamera.unlock();
        MediaRecorder mRecorder = new MediaRecorder();
        mRecorder.setCamera(mCamera);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        /**
         *     
         *     Set the video output format and encoding. For Android 2.2 (API Level 8) and higher, use the MediaRecorder.setProfile method, and get a profile instance using CamcorderProfile.get(). For versions of Android prior to 2.2, you must set the video output format and encoding parameters:
        setOutputFormat() - Set the output format, specify the default setting or MediaRecorder.OutputFormat.MPEG_4.
        setAudioEncoder() - Set the sound encoding type, specify the default setting or MediaRecorder.AudioEncoder.AMR_NB.
        setVideoEncoder() - Set the video encoding type, specify the default setting or MediaRecorder.VideoEncoder.MPEG_4_SP.
    setOutputFile() - Set the output file, use getOutputMediaFile(MEDIA_TYPE_VIDEO).toString() from the example method in the Saving Media Files section.
    setPreviewDisplay() - Specify the SurfaceView preview layout element for your application. Use the same object you specified for Connect Preview.

Caution: You must call these MediaRecorder configuration methods in this order, otherwise your application will encounter errors and the recording will fail.
Prepare MediaRecorder - Prepare the MediaRecorder with provided configuration settings by calling MediaRecorder.prepare().
Start MediaRecorder - Start recording video by calling MediaRecorder.start().
         */
    }
}
