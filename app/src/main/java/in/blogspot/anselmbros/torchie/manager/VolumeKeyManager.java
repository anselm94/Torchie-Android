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

import android.view.KeyEvent;

import in.blogspot.anselmbros.torchie.listeners.VolumeKeyComboListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

/**
 * Manager for Volume Key presses
 * Used only for Android 4.3+
 * Created by anselm94 on 26/11/15.
 */
public class VolumeKeyManager {

    private static boolean volumeDownPressed = false;
    private static boolean volumeUpPressed = false;
    public String TAG = TorchieConstants.INFO;
    private VolumeKeyComboListener mListener = null;

    public VolumeKeyManager() {
        TAG = this.getClass().getName();
    }

    public void setVolumeKeyListener(VolumeKeyComboListener listener) {
        this.mListener = listener;
    }

    /**
     * Parses Volume Key Event
     * @param event Hardware Key Event
     */
    public void handleVolumeKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                volumeDownPressed = true;
                keyComboPerformed();
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                volumeUpPressed = true;
                keyComboPerformed();
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                volumeDownPressed = false;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                volumeUpPressed = false;
            }
        }
    }

    /**
     * checks if both volume keys are pressed
     */
    private void keyComboPerformed() {
        if (volumeDownPressed && volumeUpPressed) {
            if (mListener != null) {
                mListener.onKeyComboPerformed();
            }
        }
    }
}
