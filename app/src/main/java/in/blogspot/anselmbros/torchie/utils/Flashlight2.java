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

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.Log;

import in.blogspot.anselmbros.torchie.listeners.FlashListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

/**
 * Created by anselm94 on 24/11/15.
 */
public class Flashlight2 {

    public String TAG = TorchieConstants.INFO;

    private boolean flashSupported = false;
    private boolean isFlashOn = false;
    private CameraManager mCameraManager;
    private String[] mCameraIDList;

    private FlashListener mListener;

    public Flashlight2() {
        TAG = this.getClass().getName();
    }

    @SuppressLint("NewApi")
    public boolean ready(Context ctx) {
        mCameraManager = (CameraManager) ctx.getSystemService(Context.CAMERA_SERVICE);
        if (mCameraManager != null) {
            try {
                mCameraIDList = mCameraManager.getCameraIdList();
                if (mCameraManager.getCameraCharacteristics(mCameraIDList[0]).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                    flashSupported = true;
                    return true;
                } else {
                    if (mListener != null)
                        mListener.onFlashError(TorchieConstants.ERR_FLASH_UNAVAILABLE);
                    return false;
                }
            } catch (Exception e) {
                if (mListener != null)
                    mListener.onFlashError(TorchieConstants.ERR_CAMERA_UNAVAILABLE);
                return false;
            }
        } else {
            return false;
        }
    }

    public void setFlash2StateListener(FlashListener listener) {
        this.mListener = listener;
    }

    public boolean isFlashOn() {
        return isFlashOn;
    }

    @SuppressLint("NewApi")
    public void turnOn() {
        if (!isFlashOn && flashSupported) {
            if (mCameraManager != null) {
                try {
                    mCameraManager.setTorchMode(mCameraIDList[0], true);
                    isFlashOn = true;
                    if (mListener != null) mListener.onFlashStateChanged(isFlashOn);
                } catch (Exception e) {
                    Log.e(TAG, "Turning On posed some problems!");
                    if (mListener != null)
                        mListener.onFlashError(TorchieConstants.ERR_CAMERA_OFF);
                }
            } else {
                Log.d(TAG, "Camera Manager is null!");
                if (mListener != null) mListener.onFlashError(TorchieConstants.ERR_CAMERA_VOID);
            }
        }
    }

    @SuppressLint("NewApi")
    public void turnOff() {
        if (isFlashOn && flashSupported) {
            if (mCameraManager != null) {
                try {
                    mCameraManager.setTorchMode(mCameraIDList[0], false);
                    isFlashOn = false;
                    if (mListener != null) mListener.onFlashStateChanged(isFlashOn);
                } catch (CameraAccessException e) {
                    Log.e(TAG, "Turning Off posed some problems!");
                    if (mListener != null) mListener.onFlashError(TorchieConstants.ERR_CAMERA_OFF);
                }
            } else {
                Log.d(TAG, "Camera Manager is null!");
                if (mListener != null) mListener.onFlashError(TorchieConstants.ERR_CAMERA_VOID);
            }
            mCameraManager = null;
        }
    }

}
