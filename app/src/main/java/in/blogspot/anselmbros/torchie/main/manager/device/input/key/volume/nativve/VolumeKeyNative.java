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

package in.blogspot.anselmbros.torchie.main.manager.device.input.key.volume.nativve;

import android.content.Context;
import android.view.InputEvent;
import android.view.KeyEvent;

import in.blogspot.anselmbros.torchie.main.manager.device.input.event.VolumeKeyEvent;
import in.blogspot.anselmbros.torchie.main.manager.device.input.key.volume.VolumeKeyDevice;
import in.blogspot.anselmbros.torchie.utils.Constants;

/**
 * Created by Merbin J Anselm on 04-Feb-17.
 */

public class VolumeKeyNative extends VolumeKeyDevice {
    public static final String TYPE = Constants.ID_DEVICE_INPUT_VOLUMEKEY_NATIVE;

    private boolean volumeDownPressed;
    private boolean volumeUpPressed;

    public VolumeKeyNative(Context context) {
        super(context);
        this.deviceType = TYPE;
        volumeDownPressed = false;
        volumeUpPressed = false;
    }

    @Override
    public boolean setEvent(InputEvent event) {
        VolumeKeyEvent volumeKeyEvent = (VolumeKeyEvent) event;
        if (volumeKeyEvent.getVolumeKeyEventType() == VolumeKeyEvent.VOLUME_KEY_EVENT_NATIVE && volumeKeyEvent.isVolumeKeyEvent()) {
            if (volumeKeyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (volumeKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    volumeDownPressed = true;
                } else if (volumeKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                    volumeUpPressed = true;
                }
            } else if (volumeKeyEvent.getAction() == KeyEvent.ACTION_UP) {
                if (volumeKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    volumeDownPressed = false;
                } else if (volumeKeyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                    volumeUpPressed = false;
                }
            }
            this.isActionModePerformed();
            return true;
        }
        return false;
    }


    protected boolean isKeyComboPerformed() {
        boolean keyComboPerformed = volumeDownPressed && volumeUpPressed;
        if (keyComboPerformed) {
            this.updateCurrentSignal(INP_TRIGGER);
        }
        return keyComboPerformed;
    }

}
