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

package in.blogspot.anselmbros.torchie.utils;

import android.content.Context;
import android.content.Intent;

import in.blogspot.anselmbros.torchie.listeners.FlashListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.ui.activity.ScreenflashActivity;

/**
 * Created by anselm94 on 27/1/16.
 */
public class Screenflash {

    private Context mContext;
    private boolean isFlashOn;
    private FlashListener mListener;

    public Screenflash(Context context) {
        this.mContext = context;
        isFlashOn = false;
    }

    public void setFlashStatelistener(FlashListener listener) {
        this.mListener = listener;
    }

    public void turnOn() {
        Intent intent = new Intent(this.mContext, ScreenflashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mContext.startActivity(intent);
        isFlashOn = true;
        if (mListener != null) {
            mListener.onFlashStateChanged(isFlashOn);
        }
    }

    public void turnOff() {
        this.mContext.sendBroadcast(new Intent(TorchieConstants.BROADCAST_CLOSE_ACTIVITY));
        isFlashOn = false;
        //listener will be notified from ScreenflashActivity.java in OnDestroy()
    }

    public void notifyScreenflashStatus(boolean status) {
        this.isFlashOn = status;
        if (mListener != null) {
            mListener.onFlashStateChanged(isFlashOn);
        }
    }

    public boolean isFlashOn() {
        return isFlashOn();
    }
}
