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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.main.manager.device.output.vibrator.Vibrator;
import in.blogspot.anselmbros.torchie.service.TorchieQuick;
import in.blogspot.anselmbros.torchie.ui.activity.MainActivity;
import in.blogspot.anselmbros.torchie.ui.widget.settings.CheckBoxDialogPreference;
import in.blogspot.anselmbros.torchie.utils.SettingsUtils;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by I327891 on 27-Jan-17.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.ableSettings();
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsUtils.PREF_VIBRATE)) {
            if (SettingsUtils.isVibrateEnabled(this.getActivity().getApplicationContext())) {
                Vibrator vibrator = new Vibrator(this.getActivity());
                vibrator.vibrate();
            }
        } else if (key.equals(SettingsUtils.PREF_LANGUAGE)) {
            this.restartApp(this.getActivity());
        }
    }

    public void restartApp(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
        Runtime.getRuntime().exit(0);
    }

    private void ableSettings() {
        SensorManager mSensorManager = (SensorManager) this.getActivity().getSystemService(SENSOR_SERVICE);
        Sensor proximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        boolean isTorchieServiceEnabled = TorchieQuick.getInstance() != null;
        boolean isProximitySensorAvailable = (proximitySensor != null);

        this.ableCheckboxPref((CheckBoxPreference) getPreferenceScreen().findPreference("pref_screen_on"), isTorchieServiceEnabled);
        this.ableCheckboxPref((CheckBoxPreference) getPreferenceScreen().findPreference("pref_screen_lock"), isTorchieServiceEnabled);
        this.ableListPref((CheckBoxDialogPreference) getPreferenceScreen().findPreference("pref_screen_off_timeout"), isTorchieServiceEnabled);
        this.ableCheckboxPref((CheckBoxPreference) getPreferenceScreen().findPreference("pref_proximity"), isTorchieServiceEnabled && isProximitySensorAvailable);
    }

    private void ableCheckboxPref(CheckBoxPreference checkBoxPreference, boolean enable) {
        checkBoxPreference.setChecked(enable);
        checkBoxPreference.setEnabled(enable);
    }

    private void ableListPref(CheckBoxDialogPreference checkBoxDialogPreference, boolean enable) {
        checkBoxDialogPreference.setEnabled(enable);
    }
}
