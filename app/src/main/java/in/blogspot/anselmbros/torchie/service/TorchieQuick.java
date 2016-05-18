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

package in.blogspot.anselmbros.torchie.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import in.blogspot.anselmbros.torchie.listeners.FlashListener;
import in.blogspot.anselmbros.torchie.listeners.TorchieQuickListener;
import in.blogspot.anselmbros.torchie.listeners.VolumeKeyComboListener;
import in.blogspot.anselmbros.torchie.manager.FlashManager;
import in.blogspot.anselmbros.torchie.manager.TorchieActionManager;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.utils.Notifier;

/**
 * The Accessibility Service which controls flashlight and responds to key events
 */
public class TorchieQuick extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener, FlashListener, VolumeKeyComboListener, SensorEventListener {

    private static TorchieQuick sharedInstance;
    public String TAG = TorchieConstants.INFO;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    TorchieQuickListener mListener;
    BroadcastReceiver mReceiver;
    private FlashManager mFlashManager;

    private SensorManager mSensorManager;
    private Sensor proximitySensor;

    private TorchieActionManager mTorchieActionManager;
    private Notifier notifier;

    private boolean isVibrateEnabled;

    public TorchieQuick() {
        TAG = this.getClass().getName();
    }

    public static TorchieQuick getSharedInstance() {
        return sharedInstance;
    }

    public void toggleFlash() {
        mFlashManager.toggleFlash();
    }

    public boolean isFlashOn() {
        return mFlashManager.isFlashOn();
    }

    public void setTorchieQuickListener(TorchieQuickListener listener) {
        this.mListener = listener;
    }

    public void notifyScreenlightStatus(boolean status){
        mFlashManager.notifyScreenlightStatus(status);
    }

    private void initIntentReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }

    private void initFlashManager(){
        mFlashManager = new FlashManager(TorchieQuick.this);
        mFlashManager.setFlashlightListener(this);
        mFlashManager.setFlashSource(preferences.getInt(TorchieConstants.PREF_FLASH_SOURCE, TorchieConstants.SOURCE_FLASH_CAMERA));
        mFlashManager.setFlashTimeIndefinite(preferences.getBoolean(TorchieConstants.PREF_FUNC_FLASH_OFF_INDEFINITE, true));
        mFlashManager.setFlashTimeOut(preferences.getLong(TorchieConstants.PREF_FUNC_FLASH_OFF_TIME, TorchieConstants.DEFAULT_FLASHOFF_TIME));
    }

    private void initTorchieActionManager(){
        mTorchieActionManager = new TorchieActionManager(TorchieQuick.this);
        mTorchieActionManager.setListener(this);
        mTorchieActionManager.setKeyComboMode(TorchieActionManager.KeyComboMode.AUTO);
        mTorchieActionManager.setSettingScreenLock(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_LOCKED, true));
        mTorchieActionManager.setSettingScreenOff(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
        mTorchieActionManager.setSettingScreenUnlocked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED, true));
        mTorchieActionManager.setSettingsScreenOffIndefinite(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE, false));
        mTorchieActionManager.setSettingsScreenOffTime(preferences.getLong(TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME, TorchieConstants.DEFAULT_SCREENOFF_TIME));
        mTorchieActionManager.setProximityEnabled(preferences.getBoolean(TorchieConstants.PREF_FUNC_PROXIMITY,false));
    }

    @Override
    protected void onServiceConnected() {
        sharedInstance = this;
        preferences = getSharedPreferences(TorchieConstants.PREF_KEY_APP, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        preferences.registerOnSharedPreferenceChangeListener(this);

        initIntentReceiver();
        initFlashManager();
        initTorchieActionManager();

        isVibrateEnabled = preferences.getBoolean(TorchieConstants.PREF_FUNC_VIBRATE, false);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        notifier = new Notifier(this);
        super.onServiceConnected();
    }

    public void handleVolumeChangeEvent(int prevVol, int currentVol) {
        mTorchieActionManager.handleVolumeValues(prevVol, currentVol);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case TorchieConstants.PREF_FUNC_SCREEN_OFF:
                mTorchieActionManager.setSettingScreenOff(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_LOCKED:
                mTorchieActionManager.setSettingScreenLock(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_LOCKED, true));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED:
                mTorchieActionManager.setSettingScreenUnlocked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED, true));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE:
                mTorchieActionManager.setSettingsScreenOffIndefinite(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE, false));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME:
                mTorchieActionManager.setSettingsScreenOffTime(preferences.getLong(TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME, TorchieConstants.DEFAULT_SCREENOFF_TIME));
                break;
            case TorchieConstants.PREF_FUNC_VIBRATE:
                isVibrateEnabled = preferences.getBoolean(TorchieConstants.PREF_FUNC_VIBRATE, false);
                break;
            case TorchieConstants.PREF_FLASH_SOURCE:
                mFlashManager.setFlashSource(preferences.getInt(TorchieConstants.PREF_FLASH_SOURCE, TorchieConstants.SOURCE_FLASH_CAMERA));
                break;
            case TorchieConstants.PREF_FUNC_FLASH_OFF_INDEFINITE:
                mFlashManager.setFlashTimeIndefinite(preferences.getBoolean(TorchieConstants.PREF_FUNC_FLASH_OFF_INDEFINITE, true));
                break;
            case TorchieConstants.PREF_FUNC_FLASH_OFF_TIME:
                mFlashManager.setFlashTimeOut(preferences.getLong(TorchieConstants.PREF_FUNC_FLASH_OFF_TIME, TorchieConstants.DEFAULT_FLASHOFF_TIME));
                break;
            case TorchieConstants.PREF_FUNC_PROXIMITY:
                mTorchieActionManager.setProximityEnabled(preferences.getBoolean(TorchieConstants.PREF_FUNC_PROXIMITY,false));
                break;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sharedInstance = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (mFlashManager.isFlashOn()) {
            mFlashManager.toggleFlash();
        }
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        mSensorManager.unregisterListener(this);
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        super.onKeyEvent(event);
        if (event != null) {
            if ((event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP)) { //Filters ONLY the Volume Button key events
                mTorchieActionManager.handleVolumeKeyEvents(event);
            }
        }
        return false;
    }

    @Override
    public void onFlashStateChanged(boolean enabled) {
        mTorchieActionManager.notifyFlashStatus(enabled);
        if (mListener != null) {
            mListener.onFlashStateChanged(enabled);
        }
        if (isVibrateEnabled) {
            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vib.hasVibrator())
                vib.vibrate(TorchieConstants.DEFAULT_VIBRATOR_TIME);
        }
    }

    @Override
    public void onFlashError(String error) {
        switch (error) {
            case TorchieConstants.ERR_FLASH_UNAVAILABLE:
                Toast.makeText(TorchieQuick.this, "Flash Unavailable!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onKeyComboPerformed() {
        mFlashManager.toggleFlash();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.values[0] == 0){
            mTorchieActionManager.setInPocket(true);
        }else{
            mTorchieActionManager.setInPocket(false);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class ScreenReceiver extends BroadcastReceiver {

        TorchieConstants.ScreenState screenState = TorchieConstants.ScreenState.SCREEN_UNLOCK;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                screenState = TorchieConstants.ScreenState.SCREEN_OFF;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                screenState = TorchieConstants.ScreenState.SCREEN_LOCK;
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                screenState = TorchieConstants.ScreenState.SCREEN_UNLOCK;
            }
            mTorchieActionManager.notifyScreenState(screenState);
        }

    }
}
