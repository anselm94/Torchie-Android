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
import android.view.KeyEvent;

import in.blogspot.anselmbros.torchie.listeners.VolumeKeyComboListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.utils.TorchieWakelock;

/**
 * Manages Torchie Action i.e Pressing Volume Buttons
 * Created by anselm94 on 2/12/15.
 */
public class TorchieActionManager implements VolumeKeyComboListener {

    public String TAG = TorchieConstants.INFO;

    TorchieWakelock wakeLock;

    private boolean settingScreenOff;            //User Settings
    private boolean settingScreenLock;           //User Settings
    private boolean settingScreenUnlocked;       //User Settings
    private boolean settingsScreenOffIndefinite; //User Settings
    private long settingsScreenOffTime;           //User Settings

    private boolean flagScreenOff;         //Changes when screen off time runs out

    private TorchieConstants.ScreenState currentScreenState = TorchieConstants.ScreenState.SCREEN_UNLOCK; //Current State of screen
    private KeyComboMode currentKeyComboMode;

    private VolumeKeyComboListener mListener;
    private VolumeKeyManager mKeyComboManager;
    private VolumeRockerManager mKeyComboCompatManager;
    private Context mContext;
    private ScreenOffTimer screenOffTimer;

    public TorchieActionManager(Context context) {
        TAG = this.getClass().getName();
        this.mContext = context;
        wakeLock = new TorchieWakelock();
    }

    public void setListener(VolumeKeyComboListener listener) {
        this.mListener = listener;
    }

    public void setSettingScreenOff(boolean settingScreenOff) {
        this.settingScreenOff = settingScreenOff;
    }

    public void setSettingScreenLock(boolean settingScreenLock) {
        this.settingScreenLock = settingScreenLock;
    }

    public void setSettingScreenUnlocked(boolean settingScreenUnlocked) {
        this.settingScreenUnlocked = settingScreenUnlocked;
    }

    public void setSettingsScreenOffIndefinite(boolean settingsScreenOffIndefinite) {
        this.settingsScreenOffIndefinite = settingsScreenOffIndefinite;
    }

    public void setSettingsScreenOffTime(long settingsScreenOffTime) {
        this.settingsScreenOffTime = settingsScreenOffTime;
    }

    public void setFlagScreenOff(boolean flagScreenOff) {
        this.flagScreenOff = flagScreenOff;
    }

    /**
     * to get notified of screen state and to initialise screen off timer
     * @param screenState current state of display
     */
    public void notifyScreenState(TorchieConstants.ScreenState screenState) {
        this.currentScreenState = screenState;
        notifyWakelock();
        if (this.currentScreenState == TorchieConstants.ScreenState.SCREEN_OFF) {
            setFlagScreenOff(true); //Initialise Screen off timer
            if(!settingsScreenOffIndefinite) {
                screenOffTimer = new ScreenOffTimer(this.settingsScreenOffTime, 1000);
                screenOffTimer.start();
            }
        }else if(this.currentScreenState == TorchieConstants.ScreenState.SCREEN_LOCK){
            if(screenOffTimer != null){
                screenOffTimer.cancel();
            }
        }
    }

    /**
     * Sets the key combo method
     * @param mode the key combo method
     */
    public void setKeyComboMode(KeyComboMode mode) {
        switch (mode) {
            case AUTO:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    this.currentKeyComboMode = KeyComboMode.KEYCOMBO;
                    initKeyComboManager();
                } else {
                    this.currentKeyComboMode = KeyComboMode.KEYCOMBO_COMPAT;
                    initKeyComboCompatManager();
                }
                break;
            case KEYCOMBO:
                this.currentKeyComboMode = KeyComboMode.KEYCOMBO;
                initKeyComboManager();
                break;
            case KEYCOMBO_COMPAT:
                this.currentKeyComboMode = KeyComboMode.KEYCOMBO_COMPAT;
                initKeyComboCompatManager();
                break;
        }
    }

    /**Used by KeyComboCompat manager
     * @param prevValue previous volume value
     * @param currentValue next volume value
     */
    public void handleVolumeValues(int prevValue, int currentValue) {
        if ((currentKeyComboMode == KeyComboMode.KEYCOMBO_COMPAT) || ((currentScreenState == TorchieConstants.ScreenState.SCREEN_OFF) && settingScreenOff)) {
            mKeyComboCompatManager.pushVolumeToBuffer(prevValue, currentValue);
        }
    }

    /**
     * Handles volume key presses <b>only</b>
     * @param event Hardware key event
     */
    public void handleVolumeKeyEvents(KeyEvent event) {
        if (currentKeyComboMode == KeyComboMode.KEYCOMBO) {
            mKeyComboManager.handleVolumeKeyEvent(event);
        }
    }

    /**
     * Acquires and releases wakelock
     */
    private void notifyWakelock() {
        if (settingScreenOff && (currentScreenState == TorchieConstants.ScreenState.SCREEN_OFF)) {
            wakeLock.acquire(mContext);
            if (this.currentKeyComboMode == KeyComboMode.KEYCOMBO) {
                if (mKeyComboCompatManager == null)
                    initKeyComboCompatManager();
            }
        } else if (currentScreenState == TorchieConstants.ScreenState.SCREEN_LOCK) {
            wakeLock.release();
            if (this.currentKeyComboMode == KeyComboMode.KEYCOMBO) {
                mKeyComboCompatManager = null;
            }
        }
        //To optimize battery in Android 4.2.2 or lesser
        if (this.currentKeyComboMode == KeyComboMode.KEYCOMBO_COMPAT) {
            if (currentScreenState == TorchieConstants.ScreenState.SCREEN_LOCK) {
                wakeLock.acquire(mContext);
            } else if (currentScreenState == TorchieConstants.ScreenState.SCREEN_UNLOCK) {
                wakeLock.release();
            }
        }
    }

    /**
     * Initialises KeyComboManager
     */
    private void initKeyComboManager() {
        mKeyComboManager = new VolumeKeyManager();
        mKeyComboManager.setVolumeKeyListener(this);
    }

    /**
     * Initialises KeyCombocompatManager
     */
    private void initKeyComboCompatManager() {
        mKeyComboCompatManager = new VolumeRockerManager();
        mKeyComboCompatManager.setVolumeRockerListener(this);
    }

    /**
     * Checks if Torchie functionality is allowed in particular screen state
     * @return true if functionality is enabled for particular display screen state
     */
    private boolean isAccessProvided() {
        return (settingScreenOff && (currentScreenState == TorchieConstants.ScreenState.SCREEN_OFF)) && flagScreenOff || (settingScreenLock && (currentScreenState == TorchieConstants.ScreenState.SCREEN_LOCK)) || (settingScreenUnlocked && (currentScreenState == TorchieConstants.ScreenState.SCREEN_UNLOCK));
    }

    @Override
    public void onKeyComboPerformed() {
        if (isAccessProvided()) {
            if (mListener != null) mListener.onKeyComboPerformed();
        }
    }

    /**
     * KeyComboMode.KEYCOMBO -  key events are captured - Used for Android 4.3+ only
     * KeyComboMode.KEYCOMBO_COMPAT -  volume changes are used - Used for Android 4.2.2 or lesser and used in Screen off for all Android version
     * KeyComboMode.AUTO - Selects automatically based on API level
     */
    public enum KeyComboMode {
        AUTO,           //For Auto setup
        KEYCOMBO,       //Android 4.3+ as onKeyEvent() is accessible
        KEYCOMBO_COMPAT //Android < 4.3 for Torchie Trick
    }

    /**
     * A timer for Screen-Off Torchie functionality timer
     */
    private class ScreenOffTimer extends CountDownTimer{
        public ScreenOffTimer(long startTime, long interval){
            super(startTime, interval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            setFlagScreenOff(false);
            wakeLock.release();
        }
    }
}
