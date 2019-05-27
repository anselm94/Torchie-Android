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

package in.blogspot.anselmbros.torchie.main.manager.device.input;

import android.content.Context;
import android.view.InputEvent;

import in.blogspot.anselmbros.torchie.main.manager.device.Device;
import in.blogspot.anselmbros.torchie.utils.Constants;

/**
 * Created by Merbin J Anselm on 04-Feb-17.
 */

public abstract class InputDevice extends Device {
    public static final String TYPE = Constants.ID_DEVICE_INPUT;
    public final static int INP_LOW = 0;
    public final static int INP_HIGH = 1;
    public final static int INP_TRIGGER = 2;
    private InputDeviceListener mListener;

    public InputDevice(Context context) {
        super(context);
        this.deviceType = TYPE;
    }

    public final void setListener(InputDeviceListener listener) {
        this.mListener = listener;
    }

    public final boolean setInputEvent(InputEvent event) {
        if (this.isEnabled) {
            return this.setEvent(event);
        }
        return false;
    }

    protected abstract boolean setEvent(InputEvent event);

    public abstract void getStatusRequest();

    protected final void updateCurrentSignal(int signal) {
        if (this.mListener != null) {
            this.mListener.onValueChanged(this.deviceType, signal);
        }
    }
}
