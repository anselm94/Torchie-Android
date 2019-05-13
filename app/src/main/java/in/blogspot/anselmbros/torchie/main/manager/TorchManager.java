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
import android.os.Build;

import in.blogspot.anselmbros.torchie.main.manager.device.output.OutputDeviceListener;
import in.blogspot.anselmbros.torchie.main.manager.device.output.torch.Torch;
import in.blogspot.anselmbros.torchie.main.manager.device.output.torch.flashlight.Flashlight;
import in.blogspot.anselmbros.torchie.main.manager.device.output.torch.flashlight.Flashlight1;
import in.blogspot.anselmbros.torchie.main.manager.device.output.torch.flashlight.Flashlight2;
import in.blogspot.anselmbros.torchie.main.manager.device.output.torch.screenlight.Screenlight;
import in.blogspot.anselmbros.torchie.main.manager.timer.CountTimer;
import in.blogspot.anselmbros.torchie.main.manager.timer.CountTimerListener;

/**
 * Created by Merbin J Anselm on 06-Feb-17.
 */

public class TorchManager implements CountTimerListener {

    private static TorchManager mInstance;
    private final String flashType;
    private Torch mTorch;
    private OutputDeviceListener mListener;
    private String torchType;
    private boolean isEnabled;
    private CountTimer torchTimer;
    private int torchTimeout;

    private TorchManager(String torchType, boolean enable) {
        this.flashType = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? Flashlight2.TYPE : Flashlight1.TYPE;
        this.setTorchType(torchType);
        this.isEnabled = enable;

        torchTimer = null;
        torchTimeout = -1;
    }

    public static TorchManager getInstance(String torchType, boolean enable) {
        if (mInstance == null) {
            mInstance = new TorchManager(torchType, enable);
        }
        return mInstance;
    }

    public void setEnabled(boolean enable) {
        if (this.mTorch != null) {
            this.isEnabled = enable;
            this.mTorch.setEnabled(this.isEnabled);
        }
    }

    public String getTorchType() {
        return this.torchType;
    }

    public void setTorchType(String torchType) {
        this.torchType = torchType;
    }

    private void turnOn(Context context) {
        if (this.mTorch == null) {
            if (this.torchType.equals(Flashlight.TYPE)) {
                if (this.flashType.equals(Flashlight1.TYPE)) {
                    this.mTorch = new Flashlight1(context);
                } else if (this.flashType.equals(Flashlight2.TYPE)) {
                    this.mTorch = new Flashlight2(context);
                }
            } else if (this.torchType.equals(Screenlight.TYPE)) {
                this.mTorch = new Screenlight(context);
            }
            if (this.mListener != null) {
                this.mTorch.setListener(this.mListener);
            }
        }
        this.mTorch.setEnabled(this.isEnabled);
        this.mTorch.start(true);

        if (this.torchTimer == null) {
            if (this.torchTimeout > 0) {
                this.torchTimer = new CountTimer(Torch.TYPE, this.torchTimeout, this);
                this.torchTimer.start();
            }
        }
    }

    private void turnOff() {
        if (this.mTorch != null) {
            this.mTorch.start(false);
            this.mTorch = null;
        }
    }

    public void toggle(Context context) {
        if (this.mTorch == null) {
            this.turnOn(context);
        } else {
            if (this.mTorch.getStatus()) {
                this.turnOff();
            } else {
                this.turnOn(context);
            }
        }
    }

    public void setTimeout(int timeoutSec) {
        this.torchTimeout = timeoutSec;
    }

    public boolean getStatus() {
        return this.mTorch != null && this.mTorch.getStatus();
    }

    public void setListener(OutputDeviceListener listener) {
        this.mListener = listener;
        if (this.mTorch != null) {
            this.mTorch.setListener(listener);
        }
    }

    @Override
    public void onCountEnd(String id) {
        if (id.equals(Torch.TYPE)) {
            TorchManager.getInstance(Flashlight.TYPE, true).turnOff();
            this.torchTimer = null;
        }
    }
}
