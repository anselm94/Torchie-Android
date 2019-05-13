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

package in.blogspot.anselmbros.torchie.main.manager.device.output.torch.flashlight;

import android.content.Context;

import in.blogspot.anselmbros.torchie.main.manager.device.output.torch.Torch;
import in.blogspot.anselmbros.torchie.utils.Constants;

/**
 * Created by Merbin J Anselm on 06-Feb-17.
 */

public abstract class Flashlight extends Torch {

    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_TORCH_FLASH;

    public Flashlight(Context context) {
        super(context);
        this.deviceType = TYPE;
    }
}
