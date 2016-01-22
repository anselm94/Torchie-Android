package in.blogspot.anselmbros.torchie.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.listeners.FlashlightListener;
import in.blogspot.anselmbros.torchie.listeners.TorchieQuickListener;
import in.blogspot.anselmbros.torchie.listeners.VolumeKeyComboListener;
import in.blogspot.anselmbros.torchie.manager.FlashlightManager;
import in.blogspot.anselmbros.torchie.manager.TorchieActionManager;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

public class TorchieQuick extends AccessibilityService implements SharedPreferences.OnSharedPreferenceChangeListener, FlashlightListener, VolumeKeyComboListener {

    private static TorchieQuick sharedInstance;
    public String TAG = TorchieConstants.INFO;
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    TorchieQuickListener mListener;
    BroadcastReceiver mReceiver;
    private FlashlightManager mFlashlightManager;
    private TorchieActionManager mTorchieActionManager;

    private boolean isVibrateEnabled;

    public TorchieQuick() {
        TAG = this.getClass().getName();
    }

    public static TorchieQuick getSharedInstance() {
        return sharedInstance;
    }

    public void toggleFlash() {
        mFlashlightManager.toggleFlash();
    }

    public boolean isFlashOn() {
        return mFlashlightManager.isFlashOn();
    }

    public void setTorchieQuickListener(TorchieQuickListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onServiceConnected() {
        sharedInstance = this;
        preferences = getSharedPreferences(TorchieConstants.PREF_KEY_APP, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        preferences.registerOnSharedPreferenceChangeListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        mFlashlightManager = new FlashlightManager(TorchieQuick.this);
        mFlashlightManager.setFlashlightListener(this);
        mTorchieActionManager = new TorchieActionManager(TorchieQuick.this);
        mTorchieActionManager.setListener(this);
        mTorchieActionManager.setKeyComboMode(TorchieActionManager.KeyComboMode.AUTO);
        mTorchieActionManager.setSettingScreenLock(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_LOCKED, true));
        mTorchieActionManager.setSettingScreenOff(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
        mTorchieActionManager.setSettingScreenUnlocked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED, true));
        mTorchieActionManager.setSettingsScreenOffIndefinite(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE, false));
        mTorchieActionManager.setSettingsScreenOffTime(preferences.getLong(TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME, TorchieConstants.DEFAULT_SCREENOFF_TIME));
        isVibrateEnabled = preferences.getBoolean(TorchieConstants.PREF_FUNC_VIBRATE, false);
        super.onServiceConnected();
    }

    public void handleVolumeChangeEvent(int prevVol, int currentVol) {
        mTorchieActionManager.handleVolumeValues(prevVol, currentVol);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case TorchieConstants.PREF_FUNC_SCREEN_OFF:
                mTorchieActionManager.setSettingScreenOff(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_LOCKED:
                mTorchieActionManager.setSettingScreenLock(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_LOCKED, true));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED:
                mTorchieActionManager.setSettingScreenUnlocked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED, true));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE:
                mTorchieActionManager.setSettingsScreenOffIndefinite(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE, false));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME:
                mTorchieActionManager.setSettingsScreenOffTime(preferences.getLong(TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME, TorchieConstants.DEFAULT_SCREENOFF_TIME));
                break;
            case TorchieConstants.PREF_FUNC_VIBRATE:
                isVibrateEnabled = preferences.getBoolean(TorchieConstants.PREF_FUNC_VIBRATE, false);
                break;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        sharedInstance = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (mFlashlightManager.isFlashOn()) {
            mFlashlightManager.toggleFlash();
        }
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        super.onKeyEvent(event);
        if (event != null) {
            if ((event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP)) {
                mTorchieActionManager.handleVolumeKeyEvents(event);
            }
        }
        return false;
    }

    @Override
    public void onFlashStateChanged(boolean enabled) {
        if (mListener != null) {
            mListener.onFlashStateChanged(enabled);
        }
        if (isVibrateEnabled) {
            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vib.hasVibrator())
                vib.vibrate(TorchieConstants.DEFAULT_VIBRATOR_TIME);
        }
    }

    @Override
    public void onFlashError(String error) {
        switch (error) {
            case TorchieConstants.ERR_FLASH_UNAVAILABLE:
                Toast.makeText(TorchieQuick.this, "Flash Unavailable!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onKeyComboPerformed() {
        mFlashlightManager.toggleFlash();
    }

    public class ScreenReceiver extends BroadcastReceiver {

        TorchieConstants.ScreenState screenState = TorchieConstants.ScreenState.SCREEN_UNLOCK;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                screenState = TorchieConstants.ScreenState.SCREEN_OFF;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                screenState = TorchieConstants.ScreenState.SCREEN_LOCK;
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                screenState = TorchieConstants.ScreenState.SCREEN_UNLOCK;
            }
            mTorchieActionManager.notifyScreenState(screenState);
        }

    }
}
