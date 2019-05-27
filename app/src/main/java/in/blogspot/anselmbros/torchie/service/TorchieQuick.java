/*
 *     Copyright (C) 2017 Merbin J Anselm <merbinjanselm@gmail.com>
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
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.media.VolumeProviderCompat;

import in.blogspot.anselmbros.torchie.main.ScreenState;
import in.blogspot.anselmbros.torchie.main.TorchieManager;
import in.blogspot.anselmbros.torchie.main.TorchieManagerListener;
import in.blogspot.anselmbros.torchie.main.manager.device.input.event.VolumeKeyEvent;

/**
 * Created by Merbin J Anselm on 19-Feb-17.
 */

public class TorchieQuick extends AccessibilityService implements TorchieManagerListener {

    private static TorchieQuick mInstance;
    private ScreenStateReceiver mScreenStateReceiver;

    private TorchieManagerListener mListener;

    public TorchieQuick() {
        super();
    }

    @Nullable
    public static TorchieQuick getInstance() {
        return mInstance;
    }

    public void setScreenState(ScreenState currentScreenState) {
        if (currentScreenState == ScreenState.SCREEN_OFF) {
            TorchieManager.getInstance(this).setVolumeProvider(this.getVolumeChangeProvider());
        }
        TorchieManager.getInstance(this).setScreenEvent(currentScreenState);
    }

    public void setVolumeValues(int volumeDirection) {
        TorchieManager.getInstance(this).setVolumeValue(volumeDirection);
    }

    private void registerScreenStateReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mScreenStateReceiver = new ScreenStateReceiver();
        registerReceiver(mScreenStateReceiver, filter);
    }

    private void unregisterScreenStateReceiver() {
        unregisterReceiver(mScreenStateReceiver);
    }

    private VolumeChangeProvider getVolumeChangeProvider() {
        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int STREAM_TYPE = AudioManager.STREAM_MUSIC;
        int currentVolume = audio.getStreamVolume(STREAM_TYPE);
        int maxVolume = audio.getStreamMaxVolume(STREAM_TYPE);
        return new VolumeChangeProvider(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, maxVolume, currentVolume);
    }

    public void registerTorchieManagerListener(TorchieManagerListener listener) {
        this.mListener = listener;
    }

    public void unregisterTorchieManagerListener() {
        this.mListener = null;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mInstance = this;
        registerScreenStateReceiver();
        TorchieManager.getInstance(this).setListener(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        TorchieManager.getInstance(this).destroy();
        unregisterScreenStateReceiver();
        mInstance = null;
        return super.onUnbind(intent);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        super.onKeyEvent(event);
        if ((event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP)) { //Filters ONLY the Volume Button key events
            return TorchieManager.getInstance(this).setVolumeKeyEvent(new VolumeKeyEvent(event));
        }
        return false;
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTorchStatusChanged(boolean status) {
        if (this.mListener != null) {
            this.mListener.onTorchStatusChanged(status);
        }
    }

    public void toggleTorch() {
        TorchieManager.getInstance(this).toggleTorch();
    }

    public boolean getTorchStatus() {
        return TorchieManager.getInstance(this).getTorchStatus();
    }

    public class ScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ScreenState currentScreenState = ScreenState.SCREEN_ON;
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                currentScreenState = ScreenState.SCREEN_OFF;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                currentScreenState = ScreenState.SCREEN_LOCK;
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                currentScreenState = ScreenState.SCREEN_ON;
            }
            setScreenState(currentScreenState);
        }
    }

    public class VolumeChangeProvider extends VolumeProviderCompat {

        /**
         * Create a new volume provider for handling volume events. You must specify
         * the type of volume control and the maximum volume that can be used.
         *
         * @param volumeControl The method for controlling volume that is used by
         *                      this provider.
         * @param maxVolume     The maximum allowed volume.
         * @param currentVolume The current volume.
         */
        public VolumeChangeProvider(int volumeControl, int maxVolume, int currentVolume) {
            super(volumeControl, maxVolume, currentVolume);
        }

        @Override
        public void onAdjustVolume(int direction) {
            // Up = 1, Down = -1, Release = 0
            setVolumeValues(direction);
            Log.d("torchie", String.valueOf(direction));
        }
    }
}
