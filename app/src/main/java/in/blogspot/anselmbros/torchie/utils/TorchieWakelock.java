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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

/**
 * This class is not the system wakelock but simulates the wakelock functionality with Media player wakelock
 *
 * Created by anselm94 on 3/12/15.
 */
public class TorchieWakelock {

    public String TAG = TorchieConstants.INFO;
    private MediaPlayer mediaPlayer;
    private boolean isWakelockHeld = false;

    public TorchieWakelock() {
        TAG = this.getClass().getName();
    }

    public boolean isHeld() {
        return isWakelockHeld;
    }

    /**
     *  * Once wakelock is acquired, a dummy zero-volume wave file starts playing in a loop.
     * @param context context
     */
    public void acquire(Context context) {
        if (!isWakelockHeld) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.torchie);
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            isWakelockHeld = true;
        }
    }

    public void release() {
        if (isWakelockHeld) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
                isWakelockHeld = false;
            }
        }
    }

}
