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

package in.blogspot.anselmbros.torchie.main.manager.device.output.torch.screenlight;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import in.blogspot.anselmbros.torchie.main.manager.device.output.torch.Torch;
import in.blogspot.anselmbros.torchie.ui.activity.ScreenflashActivity;
import in.blogspot.anselmbros.torchie.utils.Constants;

/**
 * Created by I327891 on 04-Feb-17.
 */

public class Screenlight extends Torch {
    public static final String TYPE = Constants.ID_DEVICE_OUTPUT_TORCH_SCREEN;
    public final static String CLOSE_ACTIVITY_IDENTIFIER = "in.blogspot.anselmbros.torchie.CLOSE_ACTIVITY";

    private ScreenlightOffReceiver screenlightOffReceiver;

    public Screenlight(Context context) {
        super(context);
        this.deviceType = TYPE;
    }

    @Override
    protected void turnOn() {
        Intent intent = new Intent(this.mContext, ScreenflashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mContext.startActivity(intent);
        this.updateStatus(true);
    }

    @Override
    protected void turnOff() {
        this.mContext.sendBroadcast(new Intent(CLOSE_ACTIVITY_IDENTIFIER));
        screenlightOffReceiver = new ScreenlightOffReceiver();
        this.mContext.registerReceiver(screenlightOffReceiver, new IntentFilter(this.deviceType));
    }

    public class ScreenlightOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateStatus(false);
            if (intent.getAction().equals(deviceType)) {
                mContext.unregisterReceiver(screenlightOffReceiver);
                screenlightOffReceiver = null;
            }
        }
    }
}
