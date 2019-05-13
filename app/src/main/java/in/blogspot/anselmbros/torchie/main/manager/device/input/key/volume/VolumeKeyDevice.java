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

package in.blogspot.anselmbros.torchie.main.manager.device.input.key.volume;

import android.content.Context;

import in.blogspot.anselmbros.torchie.main.manager.device.input.InputDevice;
import in.blogspot.anselmbros.torchie.utils.Constants;

/**
 * Created by Merbin J Anselm on 05-Feb-17.
 */

public abstract class VolumeKeyDevice extends InputDevice {
    public static final String TYPE = Constants.ID_DEVICE_INPUT_VOLUMEKEY;

    public static final int MODE_VOLUME_COMBO = 0;

    private int mMode;

    public VolumeKeyDevice(Context context) {
        super(context);
        this.mMode = MODE_VOLUME_COMBO;
        this.deviceType = TYPE;
    }

    public final int getMode() {
        return this.mMode;
    }

    public final void setMode(int mode) {
        this.mMode = mode;
    }

    @Override
    public void getStatusRequest() {
        this.isActionModePerformed();
    }

    protected final boolean isActionModePerformed() {
        switch (this.mMode) {
            case MODE_VOLUME_COMBO:
                return this.isKeyComboPerformed();
            default:
                return this.isKeyComboPerformed();
        }
    }

    protected abstract boolean isKeyComboPerformed();
}
