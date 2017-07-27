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

package in.blogspot.anselmbros.torchie.main.manager.timer;

import android.os.CountDownTimer;

/**
 * Created by I327891 on 10-Feb-17.
 */

public class CountTimer extends CountDownTimer {

    private CountTimerListener mListener;
    private String id;

    public CountTimer(String id, double seconds, CountTimerListener listener) {
        super((long) (seconds * 1000), (long) (seconds * 1000));
        this.id = id;
        this.mListener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {
        if (this.mListener != null) {
            this.mListener.onCountEnd(this.id);
        }
    }
}
