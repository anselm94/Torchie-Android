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
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

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
        TorchieManager.getInstance(this).setScreenEvent(currentScreenState);
    }

    public void setVolumeValues(int prevValue, int currentValue) {
        TorchieManager.getInstance(this).setVolumeValues(prevValue, currentValue);
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
            TorchieManager.getInstance(this).setVolumeKeyEvent(new VolumeKeyEvent(event));
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
}
