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

package in.blogspot.anselmbros.torchie.main.manager;

import android.content.Context;
import android.util.Log;

import in.blogspot.anselmbros.torchie.main.manager.device.input.InputDevice;
import in.blogspot.anselmbros.torchie.main.manager.device.input.InputDeviceListener;
import in.blogspot.anselmbros.torchie.main.manager.device.input.event.VolumeKeyEvent;
import in.blogspot.anselmbros.torchie.main.manager.device.input.key.volume.nativve.VolumeKeyNative;
import in.blogspot.anselmbros.torchie.main.manager.device.input.key.volume.rocker.VolumeKeyRocker;
import in.blogspot.anselmbros.torchie.main.manager.device.input.proximity.ProximitySensor;
import in.blogspot.anselmbros.torchie.main.manager.device.output.OutputDeviceListener;
import in.blogspot.anselmbros.torchie.main.manager.device.output.torch.flashlight.Flashlight;
import in.blogspot.anselmbros.torchie.main.manager.device.output.vibrator.Vibrator;

/**
 * Created by I327891 on 05-Feb-17.
 */

public class DeviceManager implements OutputDeviceListener, InputDeviceListener {

    private static DeviceManager mInstance;
    private Context mContext;

    private DeviceManagerListener mListener;

    private DeviceManager(Context context) {
        this.mContext = context;

        TorchManager.getInstance(Flashlight.TYPE, true).setListener(this);
        VolumeKeyManager.getInstance(this.mContext, VolumeKeyNative.TYPE, true).setListener(this);
        ProximitySensor.getInstance(this.mContext).setListener(this);
    }

    public static DeviceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DeviceManager(context);
        }
        return mInstance;
    }

    public void vibrate() {
        new Vibrator(this.mContext).vibrate();
    }

    public void setTorchType(String torchType) {
        TorchManager.getInstance(Flashlight.TYPE, true).setTorchType(torchType);
    }

    public void setTorchTimeout(int timeoutSec) {
        TorchManager.getInstance(Flashlight.TYPE, true).setTimeout(timeoutSec);
    }

    public void toggleTorch() {
        TorchManager.getInstance(Flashlight.TYPE, true).toggle(this.mContext);
    }

    public boolean getTorchStatus() {
        return TorchManager.getInstance(Flashlight.TYPE, true).getStatus();
    }

    public void setVolumeKeyEvent(VolumeKeyEvent volumeKeyEvent) {
        VolumeKeyManager.getInstance(this.mContext, VolumeKeyNative.TYPE, true).setVolumeKeyEvent(volumeKeyEvent);
    }

    public void setVolumeKeyDeviceEnabled(boolean enabled) {
        Log.e("Torchie Volume Key", String.valueOf(enabled));
        VolumeKeyManager.getInstance(this.mContext, VolumeKeyNative.TYPE, true).setEnabled(enabled);
    }

    public void setVolumeKeyDeviceType(String volumeKeyType) {
        Log.e("Torchie Volume KeyType", volumeKeyType);
        VolumeKeyManager.getInstance(this.mContext, VolumeKeyNative.TYPE, true).setType(volumeKeyType);
    }

    public void getProximityValue() {
        ProximitySensor.getInstance(this.mContext).getStatusRequest();
    }

    public void setListener(DeviceManagerListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onValueChanged(String deviceType, int eventConstant) {
        if (deviceType.equals(VolumeKeyNative.TYPE) || deviceType.equals(VolumeKeyRocker.TYPE)) {
            if (eventConstant == InputDevice.INP_TRIGGER) {
                if (this.mListener != null) {
                    this.mListener.onKeyActionPerformed();
                }
            }
        } else if (deviceType.equals(ProximitySensor.TYPE)) {
            if (eventConstant == InputDevice.INP_HIGH || eventConstant == InputDevice.INP_LOW) {
                if (this.mListener != null) {
                    this.mListener.onProximityChanged(!(eventConstant == InputDevice.INP_HIGH));
                }
            }
        }
    }

    @Override
    public void onStatusChanged(String deviceType, boolean status) {
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
}
