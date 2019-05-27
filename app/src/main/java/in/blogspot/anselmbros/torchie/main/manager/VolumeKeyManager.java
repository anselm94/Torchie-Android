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

package in.blogspot.anselmbros.torchie.main.manager;

import android.content.Context;
import android.os.Build;

import in.blogspot.anselmbros.torchie.main.manager.device.input.InputDeviceListener;
import in.blogspot.anselmbros.torchie.main.manager.device.input.event.VolumeKeyEvent;
import in.blogspot.anselmbros.torchie.main.manager.device.input.key.volume.VolumeKeyDevice;
import in.blogspot.anselmbros.torchie.main.manager.device.input.key.volume.nativve.VolumeKeyNative;
import in.blogspot.anselmbros.torchie.main.manager.device.input.key.volume.rocker.VolumeKeyRocker;

/**
 * Created by Merbin J Anselm on 06-Feb-17.
 */

class VolumeKeyManager {

    private static VolumeKeyManager mInstance;

    private VolumeKeyDevice volumeKeyDevice;
    private Context mContext;

    private boolean isEnabled;
    private InputDeviceListener mListener;

    private VolumeKeyManager(Context context, String volumeKeyType, boolean enable) {
        this.isEnabled = enable;
        this.mContext = context;
        this.setType(volumeKeyType);
    }

    public static VolumeKeyManager getInstance(Context context, String volumeKeyType, boolean enable) {
        if (mInstance == null) {
            mInstance = new VolumeKeyManager(context, volumeKeyType, enable);
        }
        return mInstance;
    }

    public void setEnabled(boolean enable) {
        this.isEnabled = enable;
        if (this.volumeKeyDevice != null) {
            this.volumeKeyDevice.setEnabled(this.isEnabled);
        }
    }

    public void setType(String volumeKeyType) {
        volumeKeyType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) ? volumeKeyType : VolumeKeyRocker.TYPE;
        if (this.volumeKeyDevice != null && VolumeKeyDevice.TYPE.equals(volumeKeyType)) {
            return;
        }
        this.volumeKeyDevice = null;
        if (volumeKeyType.equals(VolumeKeyNative.TYPE)) {
            this.volumeKeyDevice = new VolumeKeyNative(this.mContext);
            this.volumeKeyDevice.setListener(this.mListener);
        } else if (volumeKeyType.equals(VolumeKeyRocker.TYPE)) {
            this.volumeKeyDevice = new VolumeKeyRocker(this.mContext);
            this.volumeKeyDevice.setListener(this.mListener);
        }
        this.volumeKeyDevice.setEnabled(this.isEnabled);
    }

    public boolean setVolumeKeyEvent(VolumeKeyEvent keyEvent) {
        return this.volumeKeyDevice.setInputEvent(keyEvent);
    }

    public void setListener(InputDeviceListener listener) {
        this.mListener = listener;
        if (this.volumeKeyDevice != null) {
            this.volumeKeyDevice.setListener(this.mListener);
        }
    }
}
