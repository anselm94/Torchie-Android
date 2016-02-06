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

package in.blogspot.anselmbros.torchie.manager;

import android.util.Log;

import in.blogspot.anselmbros.torchie.listeners.VolumeKeyComboListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

/**
 * Simulates 'holding both volume keys' using Volume changes
 * This is an interesting trick, which makes Torchie functionality feasible at all screen states
 *
 *
 * Android system broadcast an intent 'android.media.VOLUME_CHANGED_ACTION' with previous and current volume level
 * + For each press of Volume Key, 2 intents are broadcast
 *     For eg.
 *       if 'volume +' is pressed, intents with previous and current volume levels of (13, 14) and (14, 14) are broadcast.
 *     Then if 'volume -' is pressed, intents with values (14, 13) and (13, 13) are broadcast. So, the stream is (13, 14) (14, 14) (14, 13) (13, 13)
 * + However if both Volume keys are pressed, series of intents with values (13, 14) (14, 13) (13, 13) are broadcast
 *
 * Observe the pattern of stream. This pattern is kinda a hack to make the trick work. This explains why flash toggles while swiping to and fro the <i>volume thumb</i> in system Volume panel
 *
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

    /**
     * For debugging
     */
    private void logBufferAsString() {
        String logstr = new String();
        for (int j = 0; j < MAX_BUFFER_SIZE; j++) {
            logstr = logstr + " " + String.valueOf(buffer[j]);
        }
        Log.d(TAG, logstr + "\n");
    }

    /**
     * Saves values to buffer in a circular queue
     * @param prev_value previous volume level
     * @param current_value current volume level
     */
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

    /**
     * Clears the buffer queue
     */
    private void clearBuffer() {
        buffer = new int[MAX_BUFFER_SIZE];
        current_ptr = 0;
    }

    /**
     * Checks if the pattern has occurred
     * @return true if trigger pattern occurred
     */
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
