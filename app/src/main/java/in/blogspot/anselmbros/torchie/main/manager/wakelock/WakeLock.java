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

package in.blogspot.anselmbros.torchie.main.manager.wakelock;

import android.content.Context;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.media.VolumeProviderCompat;
/**
 * Created by Merbin J Anselm on 05-Feb-17.
 */

public class WakeLock {

    public static final String TYPE = "in.blogspot.anselmbros.torchie.Wakelock";

    private MediaSessionCompat mMediaSession;
    private boolean isWakelockHeld;
    private boolean isEnabled;

    public WakeLock() {
        this.isWakelockHeld = false;
        this.isEnabled = true;
    }

    public void acquire(Context context, VolumeProviderCompat volumeProvider) {
        if (!this.isWakelockHeld && this.isEnabled) {
            if (this.mMediaSession == null) {
                this.mMediaSession = new MediaSessionCompat(context, TYPE);

                this.mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
                this.mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0)
                        .build());
            }
            this.mMediaSession.setPlaybackToRemote(volumeProvider);
            this.mMediaSession.setActive(true);
            this.isWakelockHeld = true;
        }
    }

    public void release() {
        if (this.isWakelockHeld && this.isEnabled) {
            if (this.mMediaSession != null) {
                this.mMediaSession.release();
                this.mMediaSession = null;
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
