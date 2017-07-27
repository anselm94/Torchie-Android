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

package in.blogspot.anselmbros.torchie.main.manager.device.input.proximity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.InputEvent;

import in.blogspot.anselmbros.torchie.main.manager.device.input.InputDevice;
import in.blogspot.anselmbros.torchie.main.manager.timer.CountTimer;
import in.blogspot.anselmbros.torchie.main.manager.timer.CountTimerListener;
import in.blogspot.anselmbros.torchie.utils.Constants;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by I327891 on 04-Feb-17.
 */

public class ProximitySensor extends InputDevice implements SensorEventListener, CountTimerListener {
    public static final String TYPE = Constants.ID_DEVICE_INPUT_PROXIMITY;
    private static ProximitySensor mInstance;
    private CountTimer mCountTimer;

    private int signal;

    private ProximitySensor(Context context) {
        super(context);
        this.deviceType = TYPE;
    }

    public static ProximitySensor getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ProximitySensor(context);
        }
        return mInstance;
    }

    @Override
    protected boolean setEvent(InputEvent event) {
        return false;
    }

    @Override
    public void getStatusRequest() {
        final SensorManager mSensorManager = (SensorManager) this.mContext.getSystemService(SENSOR_SERVICE);
        final Sensor proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        signal = (event.values[0] == 0) ? INP_HIGH : INP_LOW;
        if (signal == INP_HIGH) {
            if (mCountTimer != null) {
                mCountTimer.cancel();
            }
            final SensorManager mSensorManager = (SensorManager) this.mContext.getSystemService(SENSOR_SERVICE);
            mSensorManager.unregisterListener(this);
            this.updateCurrentSignal(signal);
        } else {
            mCountTimer = new CountTimer("ProximitySensorResponse", 0.3f, this);
            mCountTimer.start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onCountEnd(String id) {
        final SensorManager mSensorManager = (SensorManager) this.mContext.getSystemService(SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);
        this.updateCurrentSignal(signal);
    }
}
