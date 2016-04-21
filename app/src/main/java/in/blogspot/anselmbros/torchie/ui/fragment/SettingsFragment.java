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

package in.blogspot.anselmbros.torchie.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.ui.activity.SettingsActivity;
import in.blogspot.anselmbros.torchie.ui.dialog.LangSelectDialog;

public class SettingsFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    View rootView;

    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    SwitchCompat sw_screen_on, sw_lock_screen, sw_screen_off;
    RadioGroup rg_screen_off_options, rg_flash_source, rg_flash_time_options;
    EditText et_screen_off_mins, et_screen_off_sec;
    EditText et_flash_off_mins, et_flash_off_sec;
    AppCompatCheckBox cb_vibrate;
    AppCompatCheckBox cb_proximity;
    LinearLayout ll_choose_lang;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        initUI();
        loadPreferences();

        return rootView;
    }

    @Override
    public void onPause() {
        saveScreenOffPref();
        saveFlashTimeOutPref();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(sw_screen_on)) {
            prefEditor.putBoolean(TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED, isChecked).commit();
        } else if (buttonView.equals(sw_lock_screen)) {
            prefEditor.putBoolean(TorchieConstants.PREF_FUNC_SCREEN_LOCKED, isChecked).commit();
        } else if (buttonView.equals(sw_screen_off)) {
            prefEditor.putBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, isChecked).commit();
        } else if (buttonView.equals(cb_vibrate)) {
            prefEditor.putBoolean(TorchieConstants.PREF_FUNC_VIBRATE, isChecked).commit();
        } else if (buttonView.equals(cb_proximity)) {
            prefEditor.putBoolean(TorchieConstants.PREF_FUNC_PROXIMITY, isChecked).commit();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.equals(rg_screen_off_options)) {
            if (checkedId == R.id.rb_settings_screen_off_indefinite) {
                prefEditor.putBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE, true).commit();
            } else if (checkedId == R.id.rb_settings_screen_off_timeout) {
                prefEditor.putBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE, false).commit();
            }
        } else if (group.equals(rg_flash_source)){
            if(checkedId == R.id.rb_settings_flash_camera){
                prefEditor.putInt(TorchieConstants.PREF_FLASH_SOURCE, TorchieConstants.SOURCE_FLASH_CAMERA).commit();
            }else if (checkedId == R.id.rb_settings_flash_screen){
                prefEditor.putInt(TorchieConstants.PREF_FLASH_SOURCE, TorchieConstants.SOURCE_FLASH_SCREEN).commit();
            }
        }
        else if (group.equals(rg_flash_time_options)){
            if(checkedId == R.id.rb_flash_off_time_indefinite){
                prefEditor.putBoolean(TorchieConstants.PREF_FUNC_FLASH_OFF_INDEFINITE, true).commit();
            }else if (checkedId == R.id.rb_flash_off_time){
                prefEditor.putBoolean(TorchieConstants.PREF_FUNC_FLASH_OFF_INDEFINITE, false).commit();
            }
        }
    }

    private void initUI() {
        sw_screen_on = (SwitchCompat) rootView.findViewById(R.id.sw_settings_screen_on);
        sw_lock_screen = (SwitchCompat) rootView.findViewById(R.id.sw_settings_screen_lock);
        sw_screen_off = (SwitchCompat) rootView.findViewById(R.id.sw_settings_screen_off);
        rg_screen_off_options = (RadioGroup) rootView.findViewById(R.id.rg_screen_off_options);
        et_screen_off_mins = (EditText) rootView.findViewById(R.id.et_settings_screen_off_minutes);
        et_screen_off_sec = (EditText) rootView.findViewById(R.id.et_settings_screen_off_seconds);
        cb_vibrate = (AppCompatCheckBox) rootView.findViewById(R.id.cb_vibrate);
        rg_flash_source = (RadioGroup) rootView.findViewById(R.id.rg_flash_source);
        rg_flash_time_options = (RadioGroup) rootView.findViewById(R.id.rg_flash_off_time_option);
        et_flash_off_mins = (EditText) rootView.findViewById(R.id.et_settings_flash_off_minutes);
        et_flash_off_sec = (EditText) rootView.findViewById(R.id.et_settings_flash_off_seconds);
        cb_proximity = (AppCompatCheckBox) rootView.findViewById(R.id.cb_proximity);
        ll_choose_lang = (LinearLayout) rootView.findViewById(R.id.ll_choose_lang);

        sw_screen_on.setOnCheckedChangeListener(this);
        sw_lock_screen.setOnCheckedChangeListener(this);
        sw_screen_off.setOnCheckedChangeListener(this);
        rg_screen_off_options.setOnCheckedChangeListener(this);
        cb_vibrate.setOnCheckedChangeListener(this);
        rg_flash_source.setOnCheckedChangeListener(this);
        rg_flash_time_options.setOnCheckedChangeListener(this);
        cb_proximity.setOnCheckedChangeListener(this);
        ll_choose_lang.setOnClickListener(this);
    }

    private void loadPreferences() {
        preferences = getActivity().getSharedPreferences(TorchieConstants.PREF_KEY_APP, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        preferences.registerOnSharedPreferenceChangeListener(this);

        sw_screen_on.setChecked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED, true));
        sw_lock_screen.setChecked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_LOCKED, true));
        sw_screen_off.setChecked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
        setScreenOffOptionsUI(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
        cb_vibrate.setChecked(preferences.getBoolean(TorchieConstants.PREF_FUNC_VIBRATE, false));
        if (preferences.getInt(TorchieConstants.PREF_FLASH_SOURCE, TorchieConstants.SOURCE_FLASH_CAMERA) == TorchieConstants.SOURCE_FLASH_CAMERA) {
            rg_flash_source.check(R.id.rb_settings_flash_camera);
        } else if(preferences.getInt(TorchieConstants.PREF_FLASH_SOURCE, TorchieConstants.SOURCE_FLASH_CAMERA) == TorchieConstants.SOURCE_FLASH_SCREEN){
            rg_flash_source.check(R.id.rb_settings_flash_screen);
        }
        if (preferences.getBoolean(TorchieConstants.PREF_FUNC_FLASH_OFF_INDEFINITE, true)) {
            rg_flash_time_options.check(R.id.rb_flash_off_time_indefinite);
        } else if(preferences.getInt(TorchieConstants.PREF_FLASH_SOURCE, TorchieConstants.SOURCE_FLASH_CAMERA) == TorchieConstants.SOURCE_FLASH_SCREEN){
            rg_flash_source.check(R.id.rb_flash_off_time);
        }
        setFlashOffOptionsUI(preferences.getBoolean(TorchieConstants.PREF_FUNC_FLASH_OFF_INDEFINITE, true));
        cb_proximity.setChecked(preferences.getBoolean(TorchieConstants.PREF_FUNC_PROXIMITY, false));
    }

    private void saveScreenOffPref() {
        long min, sec;
        String st_min, st_sec;

        st_min = et_screen_off_mins.getText().toString();
        st_sec = et_screen_off_sec.getText().toString();

        try{
            min = TextUtils.isEmpty(st_min) ? 0 : Long.parseLong(st_min);
            sec = TextUtils.isEmpty(st_sec) ? 0 : Long.parseLong(st_sec);
        }catch (Exception e){
            min = 0;
            sec = TorchieConstants.DEFAULT_SCREENOFF_TIME;
        }

        if(((min<=0)&&(sec<1))||(min<0)){
            min = 0;
            sec = TorchieConstants.DEFAULT_SCREENOFF_TIME;
        }

        long timeOut = ((min * 60) + sec) * 1000;
        prefEditor.putLong(TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME, timeOut).commit();
    }

    private void saveFlashTimeOutPref(){
        long min, sec;
        String st_min, st_sec;

        st_min = et_flash_off_mins.getText().toString();
        st_sec = et_flash_off_sec.getText().toString();

        try{
            min = TextUtils.isEmpty(st_min) ? 0 : Long.parseLong(st_min);
            sec = TextUtils.isEmpty(st_sec) ? 0 : Long.parseLong(st_sec);
        }catch (Exception e){
            min = 0;
            sec = TorchieConstants.DEFAULT_FLASHOFF_TIME;
        }

        if(((min<=0)&&(sec<1))||(min<0)){
            min = TorchieConstants.DEFAULT_FLASHOFF_TIME / 60000;
            sec = (TorchieConstants.DEFAULT_FLASHOFF_TIME % 60000) / 1000;
        }

        long timeOut = ((min * 60) + sec) * 1000;
        prefEditor.putLong(TorchieConstants.PREF_FUNC_FLASH_OFF_TIME, timeOut).commit();
    }

    private void setScreenOffOptionsUI(boolean screenOffEnabled) {
        et_screen_off_sec.setText(String.valueOf((preferences.getLong(TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME, TorchieConstants.DEFAULT_SCREENOFF_TIME) % 60000) / 1000), TextView.BufferType.EDITABLE);
        et_screen_off_mins.setText(String.valueOf(preferences.getLong(TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME, TorchieConstants.DEFAULT_SCREENOFF_TIME) / 60000), TextView.BufferType.EDITABLE);
        for (int i = 0; i < rg_screen_off_options.getChildCount(); i++) {
            rg_screen_off_options.getChildAt(i).setEnabled(screenOffEnabled);
        }
        if (preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE, false)) {
            rg_screen_off_options.check(R.id.rb_settings_screen_off_indefinite);
        } else {
            rg_screen_off_options.check(R.id.rb_settings_screen_off_timeout);
        }
        boolean etEnableFlag = preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false) && !preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE, false);
        et_screen_off_mins.setEnabled(etEnableFlag);
        et_screen_off_sec.setEnabled(etEnableFlag);
    }

    private void setFlashOffOptionsUI(boolean flashInfiniteEnabled) {
        et_flash_off_sec.setText(String.valueOf((preferences.getLong(TorchieConstants.PREF_FUNC_FLASH_OFF_TIME, TorchieConstants.DEFAULT_FLASHOFF_TIME) % 60000) / 1000), TextView.BufferType.EDITABLE);
        et_flash_off_mins.setText(String.valueOf(preferences.getLong(TorchieConstants.PREF_FUNC_FLASH_OFF_TIME, TorchieConstants.DEFAULT_FLASHOFF_TIME) / 60000), TextView.BufferType.EDITABLE);
        et_flash_off_mins.setEnabled(!flashInfiniteEnabled);
        et_flash_off_sec.setEnabled(!flashInfiniteEnabled);
    }

    private void vibrate(long time) {
        Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (vib.hasVibrator())
            vib.vibrate(time);
    }

    private void showDialogLangSelect() {
        LangSelectDialog langSelDialog = new LangSelectDialog();
        langSelDialog.show(getActivity().getFragmentManager(), "Welcome Dialog");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case TorchieConstants.PREF_FUNC_SCREEN_OFF:
                setScreenOffOptionsUI(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE:
                setScreenOffOptionsUI(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
                break;
            case TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME:
                setScreenOffOptionsUI(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
                break;
            case TorchieConstants.PREF_FUNC_VIBRATE:
                if (preferences.getBoolean(TorchieConstants.PREF_FUNC_VIBRATE, false)) {
                    vibrate(TorchieConstants.DEFAULT_VIBRATOR_TIME);
                }
                break;
            case TorchieConstants.PREF_FUNC_FLASH_OFF_INDEFINITE:
                setFlashOffOptionsUI(preferences.getBoolean(TorchieConstants.PREF_FUNC_FLASH_OFF_INDEFINITE, true));
                break;
            case TorchieConstants.PREF_LOCALE:

        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(ll_choose_lang)){
            showDialogLangSelect();
        }
    }
}
