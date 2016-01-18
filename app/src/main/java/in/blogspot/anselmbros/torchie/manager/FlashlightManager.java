package in.blogspot.anselmbros.torchie.manager;

import android.content.Context;
import android.os.Build;

import in.blogspot.anselmbros.torchie.listeners.FlashlightListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.utils.Flashlight;
import in.blogspot.anselmbros.torchie.utils.Flashlight2;

/**
 * Created by anselm94 on 2/12/15.
 */
public class FlashlightManager implements FlashlightListener {

    public String TAG = TorchieConstants.INFO;

    private FlashlightListener mListener;
    private Flashlight flashlight1; //API <  23
    private Flashlight2 flashlight2;//API >= 23

    private Context mContext;

    private boolean isFlashOn = false;
    private Mode currentMode;

    public FlashlightManager(Context context) {
        TAG = this.getClass().getName();
        this.mContext = context;
        currentMode = getCurrentMode();
        if (currentMode == Mode.STD_CAMERA2_API) {
            flashlight2 = new Flashlight2();
            flashlight2.setFlash2StateListener(this);
        } else if (currentMode == Mode.STD_CAMERA_API) {
            flashlight1 = new Flashlight();
            flashlight1.setFlashStateListener(this);
        }
    }

    public void setFlashlightListener(FlashlightListener listener) {
        this.mListener = listener;
    }

    public void toggleFlash() {
        if (isFlashOn) turnOffFlash();
        else turnOnFlash();
    }

    private void turnOnFlash() {
        if (currentMode == Mode.STD_CAMERA2_API) {
            if (flashlight2.ready(mContext)) {
                flashlight2.turnOn();
            }
        } else if (currentMode == Mode.STD_CAMERA_API) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (flashlight1.ready()) {
                        flashlight1.turnOn();
                    }
                }
            }).start();
        }
    }

    private void turnOffFlash() {
        if (currentMode == Mode.STD_CAMERA2_API) {
            flashlight2.turnOff();
        } else if (currentMode == Mode.STD_CAMERA_API) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    flashlight1.turnOff();
                }
            }).start();
        }
    }

    public boolean isFlashOn() {
        return isFlashOn;
    }

    private Mode getCurrentMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Mode.STD_CAMERA2_API;
        } else {
            return Mode.STD_CAMERA_API;
        }
    }

    @Override
    public void onFlashStateChanged(boolean enabled) {
        isFlashOn = enabled;
        if (mListener != null) mListener.onFlashStateChanged(enabled);
    }

    @Override
    public void onFlashError(String error) {
        if (mListener != null) mListener.onFlashError(error);
    }

    private enum Mode {
        STD_CAMERA_API, //For Android version < 6.0
        STD_CAMERA2_API //For Android version >= 6.0
    }
}
