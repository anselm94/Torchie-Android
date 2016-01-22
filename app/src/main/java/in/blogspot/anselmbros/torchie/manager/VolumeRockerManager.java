package in.blogspot.anselmbros.torchie.manager;

import android.util.Log;

import in.blogspot.anselmbros.torchie.listeners.VolumeKeyComboListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

/**
 * Created by anselm94 on 26/11/15.
 */
public class VolumeRockerManager {

    private static int MAX_BUFFER_SIZE = 20;
    public String TAG = TorchieConstants.INFO;
    private int current_ptr = 0;
    private int[] buffer = new int[MAX_BUFFER_SIZE];

    private VolumeKeyComboListener mListener = null;


    public VolumeRockerManager() {
        TAG = this.getClass().getName();
    }

    public void setVolumeRockerListener(VolumeKeyComboListener listener) {
        this.mListener = listener;
    }

    private void logBufferAsString() {
        String logstr = new String();
        for (int j = 0; j < MAX_BUFFER_SIZE; j++) {
            logstr = logstr + " " + String.valueOf(buffer[j]);
        }
        Log.d(TAG, logstr + "\n");
    }

    public void pushVolumeToBuffer(int prev_value, int current_value) {
        buffer[current_ptr] = prev_value;
        current_ptr++;
        buffer[current_ptr] = current_value;
        current_ptr++;
        if (current_ptr == MAX_BUFFER_SIZE) {
            current_ptr = 0;
        }
        hasRocked();
    }

    private void clearBuffer() {
        buffer = new int[MAX_BUFFER_SIZE];
        current_ptr = 0;
    }

    private boolean hasRocked() {
        for (int i = 0; i < current_ptr; i = i + 2) {
//            if((buffer[i] == buffer[i+1])&&buffer[i] == 0)
//            {
//                break;
//            }
            if (buffer[i] != buffer[i + 1]) {
                if (i < (MAX_BUFFER_SIZE - 2)) {
                    if ((buffer[i] == buffer[i + 3]) && (buffer[i + 1] == buffer[i + 2])) {
//                        logBufferAsString();
                        clearBuffer();
                        if (mListener != null) {
                            mListener.onKeyComboPerformed();
                        }
                        return true;
                    }
                } else {
                    if ((buffer[MAX_BUFFER_SIZE - 2] == buffer[1]) && (buffer[MAX_BUFFER_SIZE - 1] == buffer[0])) {
//                        logBufferAsString();
                        clearBuffer();
                        if (mListener != null) mListener.onKeyComboPerformed();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
