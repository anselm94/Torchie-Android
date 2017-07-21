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

package in.blogspot.anselmbros.torchie.ui.activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.Locale;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.listeners.FlashListener;
import in.blogspot.anselmbros.torchie.listeners.TorchieQuickListener;
import in.blogspot.anselmbros.torchie.manager.FlashManager;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.service.TorchieQuick;
import in.blogspot.anselmbros.torchie.ui.dialog.AboutDialog;
import in.blogspot.anselmbros.torchie.ui.dialog.DonateDialog;
import in.blogspot.anselmbros.torchie.ui.dialog.DonateFailDialog;
import in.blogspot.anselmbros.torchie.ui.dialog.DonateSuccessDialog;
import in.blogspot.anselmbros.torchie.ui.dialog.PermissionDialog;
import in.blogspot.anselmbros.torchie.ui.dialog.WelcomeDialog;
import in.blogspot.anselmbros.torchie.utils.Notifier;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, FlashListener, TorchieQuickListener {

    public String TAG = TorchieConstants.INFO;

    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    Toolbar mToolbar;
    FloatingActionButton mFAB;
    ImageButton but_flash;
    SwitchCompat sw_func_toggle;

    LinearLayout ll_func_toggle;
    FragmentManager mFragmentManager;

    TransitionDrawable transAnimButFlash;

    TorchieQuick torchieQuickService;
    FlashManager flashManager;

    DonateDialog donateDialog;

    boolean isFlashOn = false;
    boolean flashButtonStatus = false;
    int flashButAnimTime = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TAG = this.getClass().getName();
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(TorchieConstants.PREF_KEY_APP, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        setLocale(preferences.getString(TorchieConstants.PREF_LOCALE,TorchieConstants.DEFAULT_LANG));
        setContentView(R.layout.activity_main);

        if (!isTorchieQuickRunning()) {
            Notifier notifier = new Notifier(this);
            notifier.show();
            initFlashManager();
        }
        initUI();

        if (preferences.getBoolean(TorchieConstants.PREF_FIRST_TIME, true)) {
            showDialogWelcome();
            if(isFlashAvailable(this)){
                prefEditor.putInt(TorchieConstants.PREF_FLASH_SOURCE, TorchieConstants.SOURCE_FLASH_CAMERA).apply();
            }else{
                prefEditor.putInt(TorchieConstants.PREF_FLASH_SOURCE, TorchieConstants.SOURCE_FLASH_SCREEN).apply();
            }
        }
        findViewById(R.id.sw_func_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        if (isTorchieQuickRunning()) {
            sw_func_toggle.setChecked(true);
            isFlashOn = torchieQuickService.isFlashOn();
        } else {
            sw_func_toggle.setChecked(false);
            if (flashManager != null) {
                isFlashOn = flashManager.isFlashOn();
            } else {
                isFlashOn = false;
            }
        }
        transAnimButFlash.resetTransition();
        if (isFlashOn) {
            setFlashButtonStatus(isFlashOn);
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (flashManager != null) {
            if (flashManager.isFlashOn())
                flashManager.toggleFlash();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_donate:
                showDialogDonate();
                break;
            case R.id.menu_tell_friend:
                Intent int_tell = new Intent(Intent.ACTION_SEND);
                int_tell.setType("text/plain");
                int_tell.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_info) + TorchieConstants.PLAY_URI);
                startActivity(Intent.createChooser(int_tell, getResources().getString(R.string.share_via)));
                break;
            case R.id.menu_rate:
                Intent int_rate = new Intent(Intent.ACTION_VIEW);
                int_rate.setData(Uri.parse(TorchieConstants.PLAY_URI));
                startActivity(int_rate);
                break;
            case R.id.menu_about:
                showDialogAbout();
                break;
            case R.id.menu_settings:
                Intent int_settings = new Intent(this, SettingsActivity.class);
                startActivity(int_settings);
                break;
            case R.id.menu_help_feedback:
                Intent int_help = new Intent(Intent.ACTION_VIEW);
                int_help.setData(Uri.parse(TorchieConstants.WEB_URI + "/contact"));
                startActivity(int_help);
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (v == sw_func_toggle) {
                if (!isTorchieQuickRunning()) {
                    showDialogPermission();
                } else {
                    openAccessibilitySettings();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == but_flash) {
            toggleFlash();
        } else if (v == mFAB) {
            showDialogDonate();
        } else if (v == ll_func_toggle) {
            if (!isTorchieQuickRunning()) {
                showDialogPermission();
            } else {
                openAccessibilitySettings();
            }
        }
    }

    private void openAccessibilitySettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    private boolean isTorchieQuickRunning() {
        boolean running;
        torchieQuickService = TorchieQuick.getSharedInstance();
        if (torchieQuickService != null) {
            torchieQuickService.setTorchieQuickListener(this);
            running = true;
        } else {
            running = false;
        }
        return running;
    }

    private void toggleFlash() {
        if (isTorchieQuickRunning()) {
            torchieQuickService.toggleFlash();
        } else {if(flashManager != null){
            flashManager.toggleFlash();}else{
            initFlashManager();
            flashManager.toggleFlash();
        }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//For animation to start early in Android version less than 6.0
            setFlashButtonStatus(!isFlashOn);
        }
    }

    private void initFlashManager(){
        flashManager = new FlashManager(this);
        flashManager.setFlashlightListener(this);
        flashManager.setFlashSource(preferences.getInt(TorchieConstants.PREF_FLASH_SOURCE, TorchieConstants.SOURCE_FLASH_CAMERA));
    }

    private boolean isFlashAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void initUI() {

        mFragmentManager = getFragmentManager();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        ll_func_toggle = (LinearLayout) findViewById(R.id.ll_func_toggle);
        ll_func_toggle.setOnClickListener(this);

        mFAB = (FloatingActionButton) findViewById(R.id.fab_donate);
        mFAB.setOnClickListener(this);

        but_flash = (ImageButton) findViewById(R.id.but_flash_pto);
        but_flash.setOnClickListener(this);

        sw_func_toggle = (SwitchCompat) findViewById(R.id.sw_func_toggle);
        sw_func_toggle.setOnTouchListener(this);

        transAnimButFlash = (TransitionDrawable) but_flash.getBackground();
        transAnimButFlash.resetTransition();
    }

    private void showDialogWelcome() {
        WelcomeDialog welcomeDialog = new WelcomeDialog();
        welcomeDialog.show(mFragmentManager, "Welcome Dialog");
    }

    private void showDialogPermission() {
        PermissionDialog permissionDialog = new PermissionDialog();
        permissionDialog.show(mFragmentManager, "Permission Dialog");
    }

    private void showDialogDonate() {
        donateDialog = new DonateDialog();
        donateDialog.show(mFragmentManager, "Donate Dialog");
    }

    public void showDialogDonateSuccess() {
        DonateSuccessDialog donateSuccessDialog = new DonateSuccessDialog();
        donateSuccessDialog.show(mFragmentManager, "Donate Success Dialog");
    }

    public void showDialogDonateFailure() {
        DonateFailDialog donateFailDialog = new DonateFailDialog();
        donateFailDialog.show(mFragmentManager, "Donate Fail Dialog");
    }

    private void setFlashButtonStatus(boolean enabled) {
        flashButtonStatus = enabled;
        if (flashButtonStatus) {
            transAnimButFlash.startTransition(flashButAnimTime);
        } else {
            transAnimButFlash.reverseTransition(flashButAnimTime);
        }
    }

    private void showDialogAbout() {
        AboutDialog aboutDialog = new AboutDialog();
        aboutDialog.show(mFragmentManager, "About Dialog");
    }

    public void setLocale(String lang) {
        if(!lang.equals(TorchieConstants.DEFAULT_LANG)) {
            Locale mLocale = new Locale(lang);
            Locale.setDefault(mLocale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            if (!config.locale.equals(mLocale)) {
                config.locale = mLocale;
                getBaseContext().getResources().updateConfiguration(config, null);
            }
        }else{
            Locale mLocale = Locale.getDefault();
            Locale.setDefault(mLocale);
            Configuration config = getBaseContext().getResources().getConfiguration();
            if (!config.locale.equals(mLocale)) {
                config.locale = mLocale;
                getBaseContext().getResources().updateConfiguration(config, null);
            }
        }
    }

    @Override
    public void onFlashStateChanged(final boolean enabled) {
        isFlashOn = enabled;
        if (isFlashOn != flashButtonStatus) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setFlashButtonStatus(enabled);
                }
            });
        }
    }

    @Override
    public void onFlashError(String error) {
        isFlashOn = false;
        if (flashButtonStatus) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setFlashButtonStatus(false);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (donateDialog == null) {
            return;
        }

        donateDialog.handleActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }
}
