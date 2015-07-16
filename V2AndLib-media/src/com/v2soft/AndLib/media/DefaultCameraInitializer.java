/*
 * Copyright (C) 2014 V.Shcryabets (vshcryabets@gmail.com)
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
package com.v2soft.AndLib.media;

import android.hardware.Camera;
import android.media.CamcorderProfile;

import com.v2soft.AndLib.ui.views.CameraView;

import java.util.List;

/**
 * Created by V.Shcryabets on 6/10/14.
 *
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public class DefaultCameraInitializer implements CameraView.Initialize {
    protected Camera.Parameters mParameters;
    protected CamcorderProfile mProfile;

    @Override
    public boolean enableShutterSound() {
        return true;
    }

    @Override
    public Camera getCamera() {
        Camera camera = Camera.open(getCameraId());
        mParameters = camera.getParameters();
        return camera;
    }

    @Override
    public int getCameraId() {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public CamcorderProfile initCameraProfile(Camera camera, int width, int height) {
        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        List<Camera.Size> mSupportedPreviewSizes = mParameters.getSupportedPreviewSizes();
        Camera.Size optimalSize = getOptimalPreviewSize(mSupportedPreviewSizes,
                width, height);

        // Use the same size for recording profile.
        CamcorderProfile profile = CamcorderProfile.get( getCameraId(), CamcorderProfile.QUALITY_HIGH);
        profile.videoFrameWidth = optimalSize.width;
        profile.videoFrameHeight = optimalSize.height;

        // likewise for the camera object itself.
        mParameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        camera.setParameters(mParameters);
        mProfile = profile;
        return profile;
    }

    @Override
    public CamcorderProfile getProfile() {
        return mProfile;
    }

    /**
     * Iterate over supported camera preview sizes to see which one best fits the
     * dimensions of the given view while maintaining the aspect ratio. If none can,
     * be lenient with the aspect ratio.
     *
     * @param sizes Supported camera preview sizes.
     * @param w The width of the view.
     * @param h The height of the view.
     * @return Best match camera preview size to fit in the view.
     */
    public Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;

        // Start with max value and refine as we iterate over available preview sizes. This is the
        // minimum difference between view and camera height.
        double minDiff = Double.MAX_VALUE;

        // Target view height
        int targetHeight = h;

        // Try to find a preview size that matches aspect ratio and the target view size.
        // Iterate over all available sizes and pick the largest size that can fit in the view and
        // still maintain the aspect ratio.
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find preview size that matches the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
