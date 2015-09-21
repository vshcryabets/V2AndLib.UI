/*
 * Copyright (C) 2011-2014 V.Shcryabets (vshcryabets@gmail.com)
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.v2soft.AndLib.media.DefaultCameraInitializer;

/**
 * Camera view.
 *
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public class CameraView extends SurfaceView {
    public interface Initialize {
        boolean enableShutterSound();
        Camera getCamera();
        int getCameraId();
        public CamcorderProfile initCameraProfile(Camera camera, int width, int height);
        public CamcorderProfile getProfile();
    }
    private static final String LOG_TAG = CameraView.class.getSimpleName();
    private Camera mCamera;
    private SurfaceHolder mPreviewHolder;
    private boolean isPreviewRunning = false;
    private MediaRecorder mRecorder;
    protected Initialize mInitializer;

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
        mInitializer = new DefaultCameraInitializer();
    }

    public void setInitialize(Initialize inititalizer) {
        mInitializer = inititalizer;
    }

    /**
     * Check does this device has a camera.
     * @return true if this device has at least one camera
     */
    public static boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    protected SurfaceHolder.Callback mSurfaceHolderListener = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera = mInitializer.getCamera();
                mCamera.setPreviewDisplay(mPreviewHolder);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            try {
                if (isPreviewRunning) {
                    mCamera.stopPreview();
                }
                mInitializer.initCameraProfile(mCamera, width, height);

//                mCamera.setPreviewTexture(m);
                mCamera.setPreviewDisplay(mPreviewHolder);
                mCamera.startPreview();
                isPreviewRunning = true;
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
                // TODO do something!!!
            }
        }

        public void surfaceDestroyed(SurfaceHolder arg0) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            isPreviewRunning = false;
        }
    };

    /**
     * Take photo.
     * @param path path to output photo file.
     */
    public void takePhoto(File path) {
        takePhoto(new DefaultPhotoHandler(getContext(), path, mCamera));
    }
    /**
     * Take photo.
     * @param callback picture handler.
     */
    public void takePhoto(PictureCallback callback) {
        takePhoto(false, false, callback);
    }

    /**
     * Take photo.
     * @param portrait
     * @param flash_disable
     * @param callback
     */
    public void takePhoto(boolean portrait, boolean flash_disable, PictureCallback callback) {
        if ( mCamera == null ) {
            return;
        }
        Parameters parameters =  mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        if ( portrait ) {
            parameters.set("rotation", 90);
        }
        if ( flash_disable )
            parameters.set("flash-mode", "off");
        mCamera.setParameters(parameters);
        mCamera.takePicture(null, null, callback);
    }

    public Camera getCamera() {
        return mCamera;
    }

    public static class DefaultPhotoHandler implements PictureCallback {
        protected File mOutputFilePath;
        protected Context mContext;
        protected Camera mCamera;

        public DefaultPhotoHandler(Context context, File outputPath, Camera camera) {
            mOutputFilePath = outputPath;
            mContext = context;
            mCamera = camera;
        }

        public void onPictureTaken(byte[] data, Camera camera) {
            if ( data == null) return;
            if ( mOutputFilePath == null ) return;
            FileOutputStream out = null;
            try
            {
                out = new FileOutputStream(mOutputFilePath);
                out.write(data);
                // register in MediaStore
                MediaStore.Images.Media.insertImage(
                        mContext.getContentResolver(),
                        mOutputFilePath.getAbsolutePath(),
                        null, null);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString(), e);
            } finally {
                if ( out != null )
                    try {out.close();}catch (IOException e){}
                // restart preview
                mCamera.stopPreview();
                mCamera.startPreview();
            }
        }
    }

    /**
     * Start video recording to specified file.
     * @throws IOException 
     *
     */
    public synchronized void startVideoRecorder(File outputFile) throws IOException {
        if ( mCamera == null ) {
            throw new IllegalStateException("Camera wasn't initialized");
        }
        if ( outputFile == null ) {
            throw new IllegalStateException("Output file is null");
        }
        mCamera.unlock();
        mRecorder = new MediaRecorder();
        mRecorder.setCamera(mCamera);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        /**
         *     
         *     Set the video output format and encoding. For Android 2.2 (API Level 8) and higher, use the MediaRecorder.setProfile method, 
         *     and get a profile instance using CamcorderProfile.get(). 
         *     setOutputFile() - Set the output file, use getOutputMediaFile(MEDIA_TYPE_VIDEO).toString() from the example method in the Saving Media Files section.
         *     setPreviewDisplay() - Specify the SurfaceView preview layout element for your application. Use the same object you specified for Connect Preview.
         *     Caution: You must call these MediaRecorder configuration methods in this order, otherwise your application will encounter errors and the recording will fail.
         *     Prepare MediaRecorder - Prepare the MediaRecorder with provided configuration settings by calling MediaRecorder.prepare().
         *     Start MediaRecorder - Start recording video by calling MediaRecorder.start().
         */
        mRecorder.setProfile( mInitializer.getProfile() );
        mRecorder.setOutputFile(outputFile.getAbsolutePath());
        mRecorder.setPreviewDisplay(mPreviewHolder.getSurface());
        mRecorder.prepare();
        mRecorder.start();
    }
    
    /**
     * Stop video recording.
     */
    public synchronized void stopRecording() {
        if ( mRecorder == null ) {
            throw new IllegalStateException("Recording wasn't started.");
        }
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        mCamera.lock();
    }
    
    /**
     * Return true if recording was started.
     */
    public synchronized boolean isRecording() {
        return mRecorder != null;
    }
}
