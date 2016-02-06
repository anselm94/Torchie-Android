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

package in.blogspot.anselmbros.torchie.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.service.TorchieQuick;

/**
 * Broadcast receiver for intent - android.media.VOLUME_CHANGED_ACTION
 */
public class RockerReceiver extends BroadcastReceiver {

    public String TAG = TorchieConstants.INFO;

    TorchieQuick torchieQuickService;

    public RockerReceiver() {
        TAG = this.getClass().getName();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.media.VOLUME_CHANGED_ACTION")) {
            int prev_volume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);
            int current_volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);

            torchieQuickService = TorchieQuick.getSharedInstance();
            if(torchieQuickService != null){
                torchieQuickService.handleVolumeChangeEvent(prev_volume,current_volume);
            }
        }
    }
}
