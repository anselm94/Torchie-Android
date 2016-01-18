package in.blogspot.anselmbros.torchie.manager;

import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;

import in.blogspot.anselmbros.torchie.listeners.VolumeKeyComboListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.utils.TorchieWakelock;
import in.blogspot.anselmbros.torchie.utils.VolumeKeyManager;
import in.blogspot.anselmbros.torchie.utils.VolumeRockerManager;

/**
 * Created by anselm94 on 2/12/15.
 */
public class TorchieActionManager implements VolumeKeyComboListener {

    public String TAG = TorchieConstants.INFO;
    TorchieWakelock wakeLock;
    private boolean flagScreenOff;      //User Settings
    private boolean flagScreenLock;     //User Settings
    private boolean flagScreenUnlocked; //User Settings

    private TorchieConstants.ScreenState currentScreenState = TorchieConstants.ScreenState.KrULoCdw; //Current State of screen
    private KeyComboMode currentKeyComboMode;

    private VolumeKeyComboListener mListener;
    private VolumeKeyManager mKeyComboManager;
    private VolumeRockerManager mKeyComboCompatManager;
    private Context mContext;

    public TorchieActionManager(Context context) {
        TAG = this.getClass().getName();
        this.mContext = context;
        wakeLock = new TorchieWakelock();
    }

    public void setListener(VolumeKeyComboListener listener) {
        this.mListener = listener;
    }

    public void setFlagScreenOff(boolean flagScreenOff) {
        this.flagScreenOff = flagScreenOff;
    }

    public void setFlagScreenLock(boolean flagScreenLock) {
        this.flagScreenLock = flagScreenLock;
    }

    public void setFlagScreenUnlocked(boolean flagScreenUnlocked) {
        this.flagScreenUnlocked = flagScreenUnlocked;
    }

    public void notifyScreenState(TorchieConstants.ScreenState screenState) {
        this.currentScreenState = screenState;
        notifyWakelock();
    }

    public void setKeyComboMode(KeyComboMode mode) {
        switch (mode) {
            case AUTO:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    this.currentKeyComboMode = KeyComboMode.KEYCOMBO;
                    initKeyComboManager();
                } else {
                    this.currentKeyComboMode = KeyComboMode.KEYCOMBO_COMPAT;
                    initKeyComboCompatManager();
                }
                break;
            case KEYCOMBO:
                this.currentKeyComboMode = KeyComboMode.KEYCOMBO;
                initKeyComboManager();
                break;
            case KEYCOMBO_COMPAT:
                this.currentKeyComboMode = KeyComboMode.KEYCOMBO_COMPAT;
                initKeyComboCompatManager();
                break;
        }
    }

    public void handleVolumeValues(int prevValue, int currentValue) {
        if ((currentKeyComboMode == KeyComboMode.KEYCOMBO_COMPAT) || ((currentScreenState == TorchieConstants.ScreenState.AhOFjLt) && flagScreenOff)) {
            mKeyComboCompatManager.pushVolumeToBuffer(prevValue, currentValue);
        }
    }

    public void handleVolumeKeyEvents(KeyEvent event) {
        if (currentKeyComboMode == KeyComboMode.KEYCOMBO) {
            mKeyComboManager.handleVolumeKeyEvent(event);
        }
    }

    private void notifyWakelock() {
        if (this.currentKeyComboMode == KeyComboMode.KEYCOMBO) {
            if (flagScreenOff && (currentScreenState == TorchieConstants.ScreenState.AhOFjLt)) {
                wakeLock.acquire(mContext);
                if (mKeyComboCompatManager == null)
                    initKeyComboCompatManager();
            } else if (currentScreenState == TorchieConstants.ScreenState.XgLOCtk) {
                wakeLock.release();
                mKeyComboCompatManager = null;
            }
        } else if (this.currentKeyComboMode == KeyComboMode.KEYCOMBO_COMPAT) {
            if (currentScreenState == TorchieConstants.ScreenState.AhOFjLt) {
                if (flagScreenOff || flagScreenLock) {
                    wakeLock.acquire(mContext);
                }
            } else if (currentScreenState == TorchieConstants.ScreenState.KrULoCdw) {
                wakeLock.release();
            }
        }
    }

    private void initKeyComboManager() {
        mKeyComboManager = new VolumeKeyManager();
        mKeyComboManager.setVolumeKeyListener(this);
    }

    private void initKeyComboCompatManager() {
        mKeyComboCompatManager = new VolumeRockerManager();
        mKeyComboCompatManager.setVolumeRockerListener(this);
    }

    private boolean isAccessProvided() {
        return (flagScreenOff && (currentScreenState == TorchieConstants.ScreenState.AhOFjLt)) || (flagScreenLock && (currentScreenState == TorchieConstants.ScreenState.XgLOCtk)) || (flagScreenUnlocked && (currentScreenState == TorchieConstants.ScreenState.KrULoCdw));
    }

    @Override
    public void onKeyComboPerformed() {
        if (isAccessProvided()) {
            if (mListener != null) mListener.onKeyComboPerformed();
        }
    }

    public enum KeyComboMode {
        AUTO,           //For Auto setup
        KEYCOMBO,       //Android 4.3+ as onKeyEvent() is accessible
        KEYCOMBO_COMPAT //Android < 4.3 for Torchie Trick (C)
    }
}
