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

package in.blogspot.anselmbros.torchie.manager;

import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import in.blogspot.anselmbros.torchie.listeners.FlashListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.utils.Flashlight;
import in.blogspot.anselmbros.torchie.utils.Flashlight2;
import in.blogspot.anselmbros.torchie.utils.Screenflash;
import in.blogspot.anselmbros.torchie.utils.TorchieWakelock;

/**
 * Created by anselm94 on 2/12/15.
 *
 * Controls flash - Flashlight & Screenlight
 */
public class FlashManager implements FlashListener {

    public String TAG = TorchieConstants.INFO;

    private FlashListener mListener;
    private Flashlight flashlight1; //API <  23
    private Flashlight2 flashlight2;//API >= 23
    private Screenflash screenflash;

    private Context mContext;

    private boolean isFlashOn = false;
    private Mode currentFlashAPIMode;
    private int currentFlashSource;
    private long flashTimeOut = TorchieConstants.DEFAULT_FLASHOFF_TIME;

    private FlashTimer flashTimer;

    public void setFlashTimeOut(long flashTimeOut) {
        this.flashTimeOut = flashTimeOut;
    }

    public void setFlashTimeIndefinite(boolean flashTimeIndefinite) {
        this.flashTimeIndefinite = flashTimeIndefinite;
    }

    private boolean flashTimeIndefinite = true;

    public FlashManager(Context context) {
        TAG = this.getClass().getName();
        this.mContext = context;
        setFlashSource(TorchieConstants.SOURCE_FLASH_CAMERA);
    }

    public void setFlashlightListener(FlashListener listener) {
        this.mListener = listener;
    }

    /**
     * Sets the source for flash - Flashlight or Screenlight
     */
    public void setFlashSource(int mode) {
        currentFlashSource = mode;
        switch (mode) {
            case TorchieConstants.SOURCE_FLASH_CAMERA:
                initFlashCamera();
                break;
            case TorchieConstants.SOURCE_FLASH_SCREEN:
                initFlashScreen();
                break;
        }
    }

    /**
     * toggles flash state
     */
    public void toggleFlash() {
        if (isFlashOn) turnOffFlash();
        else turnOnFlash();
    }

    /**
     * turns on flash
     */
    private void turnOnFlash() {
        if (currentFlashSource == TorchieConstants.SOURCE_FLASH_CAMERA) {
            if (currentFlashAPIMode == Mode.STD_CAMERA2_API) {
                if (flashlight2.ready(mContext)) {
                    flashlight2.turnOn();
                }
            } else if (currentFlashAPIMode == Mode.STD_CAMERA_API) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (flashlight1.ready()) {
                            flashlight1.turnOn();
                        }
                    }
                }).start();
            }
        } else if (currentFlashSource == TorchieConstants.SOURCE_FLASH_SCREEN) {
            screenflash.turnOn();
        }
        if(!flashTimeIndefinite) {
            flashTimer = new FlashTimer(this.flashTimeOut, 1000);
            flashTimer.start();
        }
    }

    /**
     * turns off flash
     */
    private void turnOffFlash() {
        if (currentFlashSource == TorchieConstants.SOURCE_FLASH_CAMERA) {
            if (currentFlashAPIMode == Mode.STD_CAMERA2_API) {
                flashlight2.turnOff();
            } else if (currentFlashAPIMode == Mode.STD_CAMERA_API) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        flashlight1.turnOff();
                    }
                }).start();
            }
        } else if (currentFlashSource == TorchieConstants.SOURCE_FLASH_SCREEN) {
            screenflash.turnOff();
        }
        if(flashTimer != null){
            flashTimer.cancel();
            flashTimer = null;
        }
    }


    /**
     * @return flash state
     */
    public boolean isFlashOn() {
        return isFlashOn;
    }

    /**
     * Uses android.hardware.camera till Android 5.1 (API 22)
     * android.hardware.camera2 from Android 6.0 onwards
     * @return returns the Camera API
     */
    private Mode getCurrentFlashAPIMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Mode.STD_CAMERA2_API;
        } else {
            return Mode.STD_CAMERA_API;
        }
    }

    /**
     * initialises camera based on API
     */
    private void initFlashCamera() {
        cleanMemory();
        currentFlashAPIMode = getCurrentFlashAPIMode();
        if (currentFlashAPIMode == Mode.STD_CAMERA2_API) {
            flashlight2 = new Flashlight2();
            flashlight2.setFlash2StateListener(this);
        } else if (currentFlashAPIMode == Mode.STD_CAMERA_API) {
            flashlight1 = new Flashlight();
            flashlight1.setFlashStateListener(this);
        }
    }

    /**
     * initialises screen flash
     */
    private void initFlashScreen() {
        cleanMemory();
        screenflash = new Screenflash(mContext);
        screenflash.setFlashStatelistener(this);
    }

    /**
     * notifies the screen-flash status to the listener when activity force killed
     * @param status state of screen
     */
    public void notifyScreenlightStatus(boolean status){
        if(screenflash!= null){
            screenflash.notifyScreenflashStatus(status);
        }
    }

    /**
     * cleans unused components
     */
    private void cleanMemory() {
        flashlight1 = null;
        flashlight2 = null;
        screenflash = null;
    }

    @Override
    public void onFlashStateChanged(boolean enabled) {
        isFlashOn = enabled;
        if (mListener != null) mListener.onFlashStateChanged(enabled);
    }

    @Override
    public void onFlashError(String error) {
        if (mListener != null) mListener.onFlashError(error);
    }

    private enum Mode {
        STD_CAMERA_API, //For Android version < 6.0  Uses android.hardware.camera
        STD_CAMERA2_API //For Android version >= 6.0 Uses android.hardware.camera2
    }

    private class FlashTimer extends CountDownTimer {
        public FlashTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            if(isFlashOn())
                turnOffFlash();
        }
    }
}
