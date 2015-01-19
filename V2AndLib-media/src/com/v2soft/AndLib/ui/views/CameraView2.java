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

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
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
import android.view.TextureView;

import com.v2soft.AndLib.media.DefaultCameraInitializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Camera view.
 *
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public class CameraView2 extends TextureView {

    private static final String LOG_TAG = CameraView2.class.getSimpleName();
    private Camera mCamera;
    private boolean isPreviewRunning = false;
    private MediaRecorder mRecorder;
    protected CameraView.Initialize mInitializer;

    public CameraView2(Context context) {
        this(context,null);
        init();
    }
    public CameraView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }
    public CameraView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        mInitializer = new DefaultCameraInitializer();
        setSurfaceTextureListener(getListener());
    }

    private SurfaceTextureListener getListener() {
        return new SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    mCamera = mInitializer.getCamera();
                    mInitializer.initCameraProfile(mCamera, width, height);
                    mCamera.setPreviewTexture(surface);
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString(), e);
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                try {
                    if (isPreviewRunning) {
                        mCamera.stopPreview();
                    }
                    mInitializer.initCameraProfile(mCamera, width, height);
                    mCamera.setPreviewTexture(surface);
                    mCamera.startPreview();
                    isPreviewRunning = true;
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.toString(), e);
                    // TODO do something!!!
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
                isPreviewRunning = false;
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        };
    }

    public void setInitialize(CameraView.Initialize inititalizer) {
        mInitializer = inititalizer;
    }

    /**
     * Check does this device has a camera.
     * @return true if this device has at least one camera
     */
    public static boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

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

    public void usePreviewSize(Camera.Size size) {
        mInitializer.usePreviewSize(mCamera, size);
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
                    try {
                        out.close();
                    }catch (IOException e){}
                // restart preview
                mCamera.stopPreview();
                mCamera.startPreview();
            }
        }
    }

    /**
     * Start video recording to specified file.
     * @throws java.io.IOException
     *
     */
    public synchronized void startVideoRecorder(File outputFile) throws IOException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Stop video recording.
     */
    public synchronized void stopRecording() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Return true if recording was started.
     */
    public synchronized boolean isRecording() {
        return mRecorder != null;
    }
}
