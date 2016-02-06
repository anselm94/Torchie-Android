/*
 *     Copyright (C) 2016  Merbin J Anselm <merbinjanselm@gmail.com>
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package in.blogspot.anselmbros.torchie.utils;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.util.List;

import in.blogspot.anselmbros.torchie.listeners.FlashListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

/**
 * Created by anselm94 on 8/11/15.
 */
public class Flashlight {

    public String TAG = TorchieConstants.INFO;
    private int REAR_FLASH = 0;
    private int FRONT_FLASH = 1;
    private boolean isFlashOn = false;
    private boolean flashsupported = false;
    private Camera currentCamera = null;
    private Camera.Parameters currentCameraParams;
    private int currentCameraID = 0;
    private int noOfCameras = 0;

    private FlashListener mListener;

    public Flashlight() {
        TAG = this.getClass().getName();
        noOfCameras = Camera.getNumberOfCameras();
    }

    public void setFlashStateListener(FlashListener listener) {
        this.mListener = listener;
    }

//    public boolean setCurrentCamera(int flashID) {
//        if (flashID >= noOfCameras) {
//            return false;
//        }
//        switch (flashID) {
//            case REAR_FLASH:
//                currentCameraID = REAR_FLASH;
//                return true;
//            case FRONT_FLASH:
//                currentCameraID = FRONT_FLASH;
//                return true;
//            default:
//                return false;
//        }
//    }
//
//    public ArrayList<Integer> getFlashSupportedCameraIDList() {
//        ArrayList<Integer> flashSupportedCameraIDList = new ArrayList<>();
//        Camera camera = null;
//        for (int i = 0; i < noOfCameras; i++) {
//            try {
//                camera = Camera.open(i);
//            } catch (Exception e) {
//                break;
//            }
//            if (camera != null) {
//                if (camera.getParameters().getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_TORCH) ||
//                        camera.getParameters().getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_ON)) {
//                    flashSupportedCameraIDList.add(i);
//                }
//                camera.release();
//                camera = null;
//            }
//        }
//        return flashSupportedCameraIDList;//May be null if camera cannot be opened
//    }

    public boolean ready() {
        if (currentCamera == null) {
            try {
                currentCamera = Camera.open(currentCameraID);
            } catch (Exception e) {
                if (mListener != null)
                    mListener.onFlashError(TorchieConstants.ERR_CAMERA_IMPERATIVE);
                return false;
            }
        }

        if (currentCamera != null) {
            try {
                currentCameraParams = currentCamera.getParameters();
            } catch (Exception e) {
                if (mListener != null)
                    mListener.onFlashError(TorchieConstants.ERR_CAMERA_VOID);
            }

            if (currentCameraParams != null) {
                List<String> supportedFlashModes = currentCameraParams.getSupportedFlashModes();

                if (supportedFlashModes != null) {
                    if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                        flashsupported = true;
                        currentCameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        Log.d(TAG, "FLASH_MODE_TORCH");
                    } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                        flashsupported = true;
                        currentCameraParams.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        Log.d(TAG, "FLASH_MODE_ON");
                    } else {
                        flashsupported = false;
                    }
                } else {
                    flashsupported = false;
                }
                if (flashsupported) {
//                    currentCameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                    try {
                        currentCamera.setParameters(currentCameraParams);
                    } catch (Exception e) {
                        if (mListener != null)
                            mListener.onFlashError(TorchieConstants.ERR_CAMERA_UNAVAILABLE);
                    }
                }
            }
            return true;
        } else {
            if (mListener != null)
                mListener.onFlashError(TorchieConstants.ERR_CAMERA_VOID);
            return false;
        }
    }

    public void turnOn() {
        if (!isFlashOn && flashsupported) {
            if (currentCamera != null) {
                try {
                    currentCamera.setPreviewTexture(new SurfaceTexture(0));//Thanks to http://github.com/bleeding182/minimalist-flashlight
                    currentCamera.startPreview();
//                currentCamera.autoFocus(null); //Causing error in Oxygen OS 2.2.0 (based on Android 5.1.1) marked by Piotr Zaborowski
                    isFlashOn = true;
                    if (mListener != null) mListener.onFlashStateChanged(true);
                } catch (Exception e) {
                    Log.e(TAG, "Exception occurred while starting Preview! Please report at anselmbros@gmail.com!");
                    isFlashOn = false;
                    if (mListener != null)
                        mListener.onFlashError(TorchieConstants.ERR_CAMERA_PREVIEW);
                    if (currentCamera != null) {
                        try {
                            currentCamera.release();
                            currentCamera = null;
                        } catch (Exception ex) {
                        }
                    }
                }
            } else {
                if (mListener != null) mListener.onFlashError(TorchieConstants.ERR_CAMERA_VOID);
            }
        }
    }

    public void turnOff() {
        if (isFlashOn && currentCamera != null && flashsupported) {
            try {
                currentCamera.stopPreview();
                if (mListener != null) mListener.onFlashStateChanged(false);
                isFlashOn = false;
            } catch (Exception e) {
                if (mListener != null) mListener.onFlashError(TorchieConstants.ERR_CAMERA_OFF);
            }
            try {
                currentCamera.release();
                currentCamera = null;
            } catch (Exception ex) {
            }
        }
    }

    public boolean isFlashOn() {
        return isFlashOn;
    }
}
