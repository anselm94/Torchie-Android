package in.blogspot.anselmbros.torchie.manager;

import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.KeyEvent;

import in.blogspot.anselmbros.torchie.listeners.VolumeKeyComboListener;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.utils.TorchieWakelock;

/**
 * Created by anselm94 on 2/12/15.
 */
public class TorchieActionManager implements VolumeKeyComboListener {

    public String TAG = TorchieConstants.INFO;

    TorchieWakelock wakeLock;

    private boolean settingScreenOff;            //User Settings
    private boolean settingScreenLock;           //User Settings
    private boolean settingScreenUnlocked;       //User Settings
    private boolean settingsScreenOffIndefinite; //User Settings
    private long settingsScreenOffTime;           //User Settings

    private boolean flagScreenOff;         //Changes when screen off time runs out

    private TorchieConstants.ScreenState currentScreenState = TorchieConstants.ScreenState.SCREEN_UNLOCK; //Current State of screen
    private KeyComboMode currentKeyComboMode;

    private VolumeKeyComboListener mListener;
    private VolumeKeyManager mKeyComboManager;
    private VolumeRockerManager mKeyComboCompatManager;
    private Context mContext;
    private ScreenOffTimer screenOffTimer;

    public TorchieActionManager(Context context) {
        TAG = this.getClass().getName();
        this.mContext = context;
        wakeLock = new TorchieWakelock();
    }

    public void setListener(VolumeKeyComboListener listener) {
        this.mListener = listener;
    }

    public void setSettingScreenOff(boolean settingScreenOff) {
        this.settingScreenOff = settingScreenOff;
    }

    public void setSettingScreenLock(boolean settingScreenLock) {
        this.settingScreenLock = settingScreenLock;
    }

    public void setSettingScreenUnlocked(boolean settingScreenUnlocked) {
        this.settingScreenUnlocked = settingScreenUnlocked;
    }

    public void setSettingsScreenOffIndefinite(boolean settingsScreenOffIndefinite) {
        this.settingsScreenOffIndefinite = settingsScreenOffIndefinite;
    }

    public void setSettingsScreenOffTime(long settingsScreenOffTime) {
        this.settingsScreenOffTime = settingsScreenOffTime;
    }

    public void setFlagScreenOff(boolean flagScreenOff) {
        this.flagScreenOff = flagScreenOff;
    }

    public void notifyScreenState(TorchieConstants.ScreenState screenState) {
        this.currentScreenState = screenState;
        notifyWakelock();
        if (this.currentScreenState == TorchieConstants.ScreenState.SCREEN_OFF) {
            setFlagScreenOff(true); //Initialise Screen off timer
            if(!settingsScreenOffIndefinite) {
                screenOffTimer = new ScreenOffTimer(this.settingsScreenOffTime, 1000);
                screenOffTimer.start();
            }
        }else if(this.currentScreenState == TorchieConstants.ScreenState.SCREEN_LOCK){
            if(screenOffTimer != null){
                screenOffTimer.cancel();
            }
        }
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
        if ((currentKeyComboMode == KeyComboMode.KEYCOMBO_COMPAT) || ((currentScreenState == TorchieConstants.ScreenState.SCREEN_OFF) && settingScreenOff)) {
            mKeyComboCompatManager.pushVolumeToBuffer(prevValue, currentValue);
        }
    }

    public void handleVolumeKeyEvents(KeyEvent event) {
        if (currentKeyComboMode == KeyComboMode.KEYCOMBO) {
            mKeyComboManager.handleVolumeKeyEvent(event);
        }
    }

    private void notifyWakelock() {
        if (settingScreenOff && (currentScreenState == TorchieConstants.ScreenState.SCREEN_OFF)) {
            wakeLock.acquire(mContext);
            if (this.currentKeyComboMode == KeyComboMode.KEYCOMBO) {
                if (mKeyComboCompatManager == null)
                    initKeyComboCompatManager();
            }
        } else if (currentScreenState == TorchieConstants.ScreenState.SCREEN_LOCK) {
            wakeLock.release();
            if (this.currentKeyComboMode == KeyComboMode.KEYCOMBO) {
                mKeyComboCompatManager = null;
            }
        }
        //To optimize battery in Android 4.2.2 or lesser
        if (this.currentKeyComboMode == KeyComboMode.KEYCOMBO_COMPAT) {
            if (currentScreenState == TorchieConstants.ScreenState.SCREEN_LOCK) {
                wakeLock.acquire(mContext);
            } else if (currentScreenState == TorchieConstants.ScreenState.SCREEN_UNLOCK) {
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
        return (settingScreenOff && (currentScreenState == TorchieConstants.ScreenState.SCREEN_OFF)) && flagScreenOff || (settingScreenLock && (currentScreenState == TorchieConstants.ScreenState.SCREEN_LOCK)) || (settingScreenUnlocked && (currentScreenState == TorchieConstants.ScreenState.SCREEN_UNLOCK));
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

    private class ScreenOffTimer extends CountDownTimer{
        public ScreenOffTimer(long startTime, long interval){
            super(startTime, interval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            setFlagScreenOff(false);
            wakeLock.release();
        }
    }
}
