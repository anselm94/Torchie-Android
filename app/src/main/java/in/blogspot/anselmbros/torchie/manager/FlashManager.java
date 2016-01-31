package in.blogspot.anselmbros.torchie.manager;

import android.content.Context;
import android.os.Build;

import in.blogspot.anselmbros.torchie.listeners.FlashListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.utils.Flashlight;
import in.blogspot.anselmbros.torchie.utils.Flashlight2;
import in.blogspot.anselmbros.torchie.utils.Screenflash;

/**
 * Created by anselm94 on 2/12/15.
 */
public class FlashManager implements FlashListener {

    public String TAG = TorchieConstants.INFO;

    private FlashListener mListener;
    private Flashlight flashlight1; //API <  23
    private Flashlight2 flashlight2;//API >= 23
    private Screenflash screenflash;

    private Context mContext;

    private boolean isFlashOn = false;
    private Mode currentFlashAPIMode;
    private int currentFlashSource;

    public FlashManager(Context context) {
        TAG = this.getClass().getName();
        this.mContext = context;
        setFlashMode(TorchieConstants.SOURCE_FLASH_CAMERA);
    }

    public void setFlashlightListener(FlashListener listener) {
        this.mListener = listener;
    }

    public void setFlashMode(int mode) {
        currentFlashSource = mode;
        switch (mode) {
            case TorchieConstants.SOURCE_FLASH_CAMERA:
                initFlashCamera();
                break;
            case TorchieConstants.SOURCE_FLASH_SCREEN:
                initFlashScreen();
                break;
        }
    }

    public void toggleFlash() {
        if (isFlashOn) turnOffFlash();
        else turnOnFlash();
    }

    private void turnOnFlash() {
        if (currentFlashSource == TorchieConstants.SOURCE_FLASH_CAMERA) {
            if (currentFlashAPIMode == Mode.STD_CAMERA2_API) {
                if (flashlight2.ready(mContext)) {
                    flashlight2.turnOn();
                }
            } else if (currentFlashAPIMode == Mode.STD_CAMERA_API) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (flashlight1.ready()) {
                            flashlight1.turnOn();
                        }
                    }
                }).start();
            }
        } else if (currentFlashSource == TorchieConstants.SOURCE_FLASH_SCREEN) {
            screenflash.turnOn();
        }
    }

    private void turnOffFlash() {
        if (currentFlashSource == TorchieConstants.SOURCE_FLASH_CAMERA) {
            if (currentFlashAPIMode == Mode.STD_CAMERA2_API) {
                flashlight2.turnOff();
            } else if (currentFlashAPIMode == Mode.STD_CAMERA_API) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        flashlight1.turnOff();
                    }
                }).start();
            }
        } else if (currentFlashSource == TorchieConstants.SOURCE_FLASH_SCREEN) {
            screenflash.turnOff();
        }
    }

    public boolean isFlashOn() {
        return isFlashOn;
    }

    private Mode getCurrentFlashAPIMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Mode.STD_CAMERA2_API;
        } else {
            return Mode.STD_CAMERA_API;
        }
    }

    private void initFlashCamera() {
        cleanMemory();
        currentFlashAPIMode = getCurrentFlashAPIMode();
        if (currentFlashAPIMode == Mode.STD_CAMERA2_API) {
            flashlight2 = new Flashlight2();
            flashlight2.setFlash2StateListener(this);
        } else if (currentFlashAPIMode == Mode.STD_CAMERA_API) {
            flashlight1 = new Flashlight();
            flashlight1.setFlashStateListener(this);
        }
    }

    private void initFlashScreen() {
        cleanMemory();
        screenflash = new Screenflash(mContext);
        screenflash.setFlashStatelistener(this);
    }

    public void notifyScreenlightStatus(boolean status){
        if(screenflash!= null){
            screenflash.notifyScreenflashStatus(status);
        }
    }

    private void cleanMemory() {
        flashlight1 = null;
        flashlight2 = null;
        screenflash = null;
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
