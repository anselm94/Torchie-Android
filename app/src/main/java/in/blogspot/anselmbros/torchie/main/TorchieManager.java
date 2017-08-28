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

package in.blogspot.anselmbros.torchie.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.main.manager.DeviceManager;
import in.blogspot.anselmbros.torchie.main.manager.DeviceManagerListener;
import in.blogspot.anselmbros.torchie.main.manager.WakeLockManager;
import in.blogspot.anselmbros.torchie.main.manager.device.input.event.VolumeKeyEvent;
import in.blogspot.anselmbros.torchie.main.manager.device.input.key.volume.nativve.VolumeKeyNative;
import in.blogspot.anselmbros.torchie.main.manager.device.input.key.volume.rocker.VolumeKeyRocker;
import in.blogspot.anselmbros.torchie.main.manager.timer.CountTimerListener;
import in.blogspot.anselmbros.torchie.main.manager.wakelock.WakeLock;
import in.blogspot.anselmbros.torchie.utils.SettingsUtils;

/**
 * Created by I327891 on 14-Feb-17.
 */

public class TorchieManager implements DeviceManagerListener, CountTimerListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static TorchieManager mInstance;
    private final Context mContext;
    private TorchieManagerListener mListener;
    private boolean toggleTorchIssued = false;
    private ScreenState currentScreenState = ScreenState.SCREEN_ON;

    private TorchieManager(Context context) {
        super();
        this.mContext = context;

        DeviceManager.getInstance(this.mContext).setListener(this);

        DeviceManager.getInstance(this.mContext).setTorchType(SettingsUtils.getTorchSource(this.mContext));
        DeviceManager.getInstance(this.mContext).setTorchTimeout(SettingsUtils.getTorchTimeout(this.mContext));

        PreferenceManager.getDefaultSharedPreferences(this.mContext).registerOnSharedPreferenceChangeListener(this);
    }

    public static TorchieManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TorchieManager(context);
        }
        return mInstance;
    }

    private static boolean isLegacyDevice() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    public void destroy() {
        PreferenceManager.getDefaultSharedPreferences(this.mContext).unregisterOnSharedPreferenceChangeListener(this);
        mInstance = null;
    }

    public void setListener(TorchieManagerListener listener) {
        this.mListener = listener;
    }

    public void setVolumeKeyEvent(VolumeKeyEvent keyEvent) {
        DeviceManager.getInstance(this.mContext).setVolumeKeyEvent(keyEvent);
    }

    public void setVolumeValues(int prevVolume, int currentVolume) {
        int[] volumeArr = {prevVolume, currentVolume};
        DeviceManager.getInstance(this.mContext).setVolumeKeyEvent(new VolumeKeyEvent(volumeArr));
    }

    public void toggleTorch() {
        if (SettingsUtils.isProximityEnabled(this.mContext)) {
            DeviceManager.getInstance(this.mContext).getProximityValue();
            this.toggleTorchIssued = true;
        } else {
            DeviceManager.getInstance(this.mContext).toggleTorch();
        }
    }

    public boolean getTorchStatus() {
        return DeviceManager.getInstance(this.mContext).getTorchStatus();
    }

    public void setScreenEvent(ScreenState state) {
        this.currentScreenState = state;
        switch (state) {
            case SCREEN_OFF:
                this.onScreenOff();
                break;
            case SCREEN_LOCK:
                this.onScreenLock();
                break;
            case SCREEN_ON:
                this.onScreenOn();
                break;
        }
    }

    private void onScreenOff() {
        DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceType(VolumeKeyRocker.TYPE);
        if (SettingsUtils.isScreenOffEnabled(this.mContext)) {
            WakeLockManager.getInstance().acquire(this.mContext);
            WakeLockManager.getInstance().setTimeout(SettingsUtils.getScreenOffTimeoutSec(this.mContext), this);
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(true);
        } else {
            if (this.getTorchStatus()) {
                WakeLockManager.getInstance().acquire(this.mContext);
                WakeLockManager.getInstance().setHeldOnDemand();
                DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(true);
            } else {
                WakeLockManager.getInstance().release();
                DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(false);
            }
        }
    }

    private void onScreenLock() {
        DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(SettingsUtils.isScreenLockEnabled(this.mContext));
        if (isLegacyDevice()) {
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceType(VolumeKeyRocker.TYPE);
            if (SettingsUtils.isScreenLockEnabled(this.mContext)) {
                WakeLockManager.getInstance().acquire(this.mContext);
            }
        } else {
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceType(VolumeKeyNative.TYPE);
        }
    }

    private void onScreenOn() {
        DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(SettingsUtils.isScreenOnEnabled(this.mContext));
        WakeLockManager.getInstance().release();
        if (isLegacyDevice()) {
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceType(VolumeKeyRocker.TYPE);
        } else {
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceType(VolumeKeyNative.TYPE);
        }
    }

    @Override
    public void onKeyActionPerformed() {
        boolean screenOffFlag = (currentScreenState == ScreenState.SCREEN_OFF) && SettingsUtils.isScreenOffEnabled(this.mContext);
        boolean screenLockFlag = (currentScreenState == ScreenState.SCREEN_LOCK) && SettingsUtils.isScreenLockEnabled(this.mContext);
        boolean screenOnfFlag = (currentScreenState == ScreenState.SCREEN_ON) && SettingsUtils.isScreenOnEnabled(this.mContext);
        if (this.getTorchStatus() || screenOffFlag || screenLockFlag || screenOnfFlag) {
            this.toggleTorch();
        }
    }

    @Override
    public void onProximityChanged(boolean status) {
        if (this.toggleTorchIssued) {
            if (status) {
                DeviceManager.getInstance(this.mContext).toggleTorch();
            } else {
                this.mListener.onError(this.mContext.getResources().getString(R.string.proximity_error));
            }
            this.toggleTorchIssued = false;
        }
    }

    @Override
    public void onTorchStatusChanged(boolean status) {
        if (SettingsUtils.isVibrateEnabled(this.mContext)) {
            DeviceManager.getInstance(this.mContext).vibrate();
        }
        if (WakeLockManager.getInstance().isHeldOnDemand() && !status) {
            WakeLockManager.getInstance().release();
            DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(false);
        }
        if (this.mListener != null) {
            this.mListener.onTorchStatusChanged(status);
        }
    }

    @Override
    public void onError(String error) {
        if (this.mListener != null) {
            this.mListener.onError(error);
        }
    }

    @Override
    public void onCountEnd(String id) {
        if (id.equals(WakeLock.TYPE)) {
            if (this.getTorchStatus()) {
                WakeLockManager.getInstance().setHeldOnDemand();
                DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(true);
            } else {
                WakeLockManager.getInstance().release();
                DeviceManager.getInstance(this.mContext).setVolumeKeyDeviceEnabled(false);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case SettingsUtils.PREF_TORCH_SOURCE:
                DeviceManager.getInstance(this.mContext).setTorchType(SettingsUtils.getTorchSource(this.mContext));
                break;
            case SettingsUtils.PREF_TORCH_TIMEOUT:
                DeviceManager.getInstance(this.mContext).setTorchTimeout(SettingsUtils.getTorchTimeout(this.mContext));
                break;
        }
    }
}
