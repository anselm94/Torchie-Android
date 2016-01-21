package in.blogspot.anselmbros.torchie.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

/**
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
