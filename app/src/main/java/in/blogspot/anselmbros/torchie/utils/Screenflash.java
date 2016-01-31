package in.blogspot.anselmbros.torchie.utils;

import android.content.Context;
import android.content.Intent;

import in.blogspot.anselmbros.torchie.listeners.FlashListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.ui.activity.ScreenflashActivity;

/**
 * Created by anselm94 on 27/1/16.
 */
public class Screenflash {

    private Context mContext;
    private boolean isFlashOn;
    private FlashListener mListener;

    public Screenflash(Context context) {
        this.mContext = context;
        isFlashOn = false;
    }

    public void setFlashStatelistener(FlashListener listener) {
        this.mListener = listener;
    }

    public void turnOn() {
        Intent intent = new Intent(this.mContext, ScreenflashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mContext.startActivity(intent);
        isFlashOn = true;
        if (mListener != null) {
            mListener.onFlashStateChanged(isFlashOn);
        }
    }

    public void turnOff() {
        this.mContext.sendBroadcast(new Intent(TorchieConstants.BROADCAST_CLOSE_ACTIVITY));
        isFlashOn = false;
        //listener will be notified from ScreenflashActivity.java in OnDestroy()
    }

    public void notifyScreenflashStatus(boolean status) {
        this.isFlashOn = status;
        if (mListener != null) {
            mListener.onFlashStateChanged(isFlashOn);
        }
    }

    public boolean isFlashOn() {
        return isFlashOn();
    }
}
