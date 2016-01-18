package in.blogspot.anselmbros.torchie.utils;

import android.view.KeyEvent;

import in.blogspot.anselmbros.torchie.listeners.VolumeKeyComboListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

/**
 * Created by anselm94 on 26/11/15.
 */
public class VolumeKeyManager {

    private static boolean volumeDownPressed = false;
    private static boolean volumeUpPressed = false;
    public String TAG = TorchieConstants.INFO;
    private VolumeKeyComboListener mListener = null;

    public VolumeKeyManager() {
        TAG = this.getClass().getName();
    }

    public void setVolumeKeyListener(VolumeKeyComboListener listener) {
        this.mListener = listener;
    }

    public void handleVolumeKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                volumeDownPressed = true;
                keyComboPerformed();
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                volumeUpPressed = true;
                keyComboPerformed();
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                volumeDownPressed = false;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
                volumeUpPressed = false;
            }
        }
    }

    private void keyComboPerformed() {
        if (volumeDownPressed && volumeUpPressed) {
            if (mListener != null) {
                mListener.onKeyComboPerformed();
            }
        }
    }
}
