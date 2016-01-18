package in.blogspot.anselmbros.torchie.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
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
        mTorchieActionManager.setFlagScreenLock(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_LOCKED, this.getResources().getBoolean(R.bool.func_screen_lock)));
        mTorchieActionManager.setFlagScreenOff(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
        mTorchieActionManager.setFlagScreenUnlocked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED, true));

        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() != null) {
            if (event.getPackageName().equals(this.getPackageName())) {
                if (event.getClassName() != null) {
                    if (event.getClassName().equals(TorchieConstants.ACC_VOLUME_CHANGE)) {
                        if (event.getText() != null) {
                            String par[] = event.getText().get(0).toString().split(",");
                            int prevVol = Integer.valueOf(par[0]);
                            int currentVol = Integer.valueOf(par[1]);
                            mTorchieActionManager.handleVolumeValues(prevVol, currentVol);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Interrupt occured!");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case TorchieConstants.PREF_FUNC_SCREEN_OFF:
                mTorchieActionManager.setFlagScreenOff(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_LOCKED:
                mTorchieActionManager.setFlagScreenLock(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_LOCKED, this.getResources().getBoolean(R.bool.func_screen_lock)));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED:
                mTorchieActionManager.setFlagScreenUnlocked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED, true));
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

        TorchieConstants.ScreenState screenState = TorchieConstants.ScreenState.KrULoCdw;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                screenState = TorchieConstants.ScreenState.AhOFjLt;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                screenState = TorchieConstants.ScreenState.XgLOCtk;
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                screenState = TorchieConstants.ScreenState.KrULoCdw;
            }
            mTorchieActionManager.notifyScreenState(screenState);
        }

    }
}
