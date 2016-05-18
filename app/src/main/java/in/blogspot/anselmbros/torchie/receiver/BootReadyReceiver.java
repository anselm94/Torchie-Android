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

import in.blogspot.anselmbros.torchie.service.TorchieQuick;
import in.blogspot.anselmbros.torchie.utils.Notifier;

/**
 * Created by anselm94 on 21/4/16.
 */
public class BootReadyReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    private TorchieQuick torchieQuickService;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION)) {
            if(!isTorchieQuickRunning()){
                Notifier notifier = new Notifier(context);
                notifier.show();
            }
        }
    }

    private boolean isTorchieQuickRunning() {
        boolean running;
        torchieQuickService = TorchieQuick.getSharedInstance();
        if (torchieQuickService != null) {
            running = true;
        } else {
            running = false;
        }
        return running;
    }

}
