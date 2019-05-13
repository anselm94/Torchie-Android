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

package in.blogspot.anselmbros.torchie.main.manager.wakelock;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;

import in.blogspot.anselmbros.torchie.R;

/**
 * Created by Merbin J Anselm on 05-Feb-17.
 */

public class WakeLock {

    public static final String TYPE = "in.blogspot.anselmbros.torchie.Wakelock";

    private MediaPlayer mediaPlayer;
    private boolean isWakelockHeld;
    private boolean isEnabled;

    public WakeLock() {
        this.isWakelockHeld = false;
        this.isEnabled = true;
    }

    public void acquire(Context context) {
        if (!this.isWakelockHeld && this.isEnabled) {
            if (this.mediaPlayer == null) {
                this.mediaPlayer = MediaPlayer.create(context, R.raw.torchie);
            }
            this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            this.mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            this.mediaPlayer.setLooping(true);
            this.mediaPlayer.start();
            this.isWakelockHeld = true;
        }
    }

    public void release() {
        if (this.isWakelockHeld && this.isEnabled) {
            if (this.mediaPlayer != null) {
                this.mediaPlayer.release();
                this.mediaPlayer = null;
                this.isWakelockHeld = false;
            }
        }
    }

    public boolean isHeld() {
        return this.isWakelockHeld;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = true;
    }
}
