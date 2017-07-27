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
import android.util.Log;

import in.blogspot.anselmbros.torchie.main.manager.timer.CountTimer;
import in.blogspot.anselmbros.torchie.main.manager.timer.CountTimerListener;
import in.blogspot.anselmbros.torchie.main.manager.wakelock.WakeLock;

/**
 * Created by I327891 on 05-Feb-17.
 */

public class WakeLockManager {

    private static WakeLockManager mInstance;
    private WakeLock wakeLock;
    private boolean isEnabled;
    private boolean isHeldOnDemand;

    private CountTimer wakeLockTimer;

    private WakeLockManager() {
        this.isEnabled = true;
        this.isHeldOnDemand = false;

        wakeLockTimer = null;
    }

    public static WakeLockManager getInstance() {
        if (mInstance == null) {
            mInstance = new WakeLockManager();
        }
        return mInstance;
    }

    public void acquire(Context context) {
        if (this.wakeLock == null) {
            this.wakeLock = new WakeLock();
        }
        this.wakeLock.setEnabled(this.isEnabled);
        this.wakeLock.acquire(context);
        Log.e("Torchie Wakelock", String.valueOf(this.wakeLock.isHeld()));
    }

    public void setTimeout(int timeoutSec, CountTimerListener listener) {
        if (timeoutSec > 0) {
            if (this.wakeLockTimer == null) {
                this.wakeLockTimer = new CountTimer(WakeLock.TYPE, timeoutSec, listener);
            } else {
                this.wakeLockTimer.cancel();
            }
            this.wakeLockTimer.start();
        }
    }

    public void setHeldOnDemand() {
        this.isHeldOnDemand = true;
    }

    public boolean isHeldOnDemand() {
        return this.isHeldOnDemand;
    }

    public void release() {
        if (this.wakeLock != null) {
            this.wakeLock.release();
            this.isHeldOnDemand = false;
            Log.e("Torchie Wakelock", String.valueOf(this.wakeLock.isHeld()));
        }
        this.wakeLock = null;

        if (this.wakeLockTimer != null) {
            this.wakeLockTimer.cancel();
            this.wakeLockTimer = null;
        }
    }

    public boolean isHeld() {
        return this.wakeLock != null && this.wakeLock.isHeld();
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        if (this.wakeLock != null) {
            this.wakeLock.setEnabled(this.isEnabled);
        }
    }
}
