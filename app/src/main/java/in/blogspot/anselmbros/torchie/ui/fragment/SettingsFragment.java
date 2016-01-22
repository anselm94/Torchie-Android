package in.blogspot.anselmbros.torchie.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    View rootView;

    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;

    SwitchCompat sw_screen_on, sw_lock_screen, sw_screen_off;
    RadioGroup rg_screen_off_options;
    EditText et_screen_off_mins, et_screen_off_sec;
    AppCompatCheckBox cb_vibrate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        preferences = getActivity().getSharedPreferences(TorchieConstants.PREF_KEY_APP, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

        sw_screen_on = (SwitchCompat) rootView.findViewById(R.id.sw_settings_screen_on);
        sw_lock_screen = (SwitchCompat) rootView.findViewById(R.id.sw_settings_screen_lock);
        sw_screen_off = (SwitchCompat) rootView.findViewById(R.id.sw_settings_screen_off);
        rg_screen_off_options = (RadioGroup) rootView.findViewById(R.id.rg_screen_off_options);
        et_screen_off_mins = (EditText) rootView.findViewById(R.id.et_settings_screen_off_minutes);
        et_screen_off_sec = (EditText) rootView.findViewById(R.id.et_settings_screen_off_seconds);
        cb_vibrate = (AppCompatCheckBox) rootView.findViewById(R.id.cb_vibrate);

        sw_screen_on.setOnCheckedChangeListener(this);
        sw_lock_screen.setOnCheckedChangeListener(this);
        sw_screen_off.setOnCheckedChangeListener(this);
        rg_screen_off_options.setOnCheckedChangeListener(this);
        cb_vibrate.setOnCheckedChangeListener(this);

        loadPreferences();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define the settings file to use by this settings fragment
//        getPreferenceManager().setSharedPreferencesName(TorchieConstants.PREF_KEY_APP);
//        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onPause() {
        long min, sec;
        try {
            min = Long.valueOf(et_screen_off_mins.getText().toString());
        } catch (Exception e) {
            min = 0;
        }
        try {
            sec = Long.valueOf(et_screen_off_sec.getText().toString());
        } catch (Exception e) {
            sec = TorchieConstants.DEFAULT_SCREENOFF_TIME;
        }
        long timeOut = ((min * 60) + sec) * 1000;
        prefEditor.putLong(TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME, timeOut).commit();
        super.onPause();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(sw_screen_on)) {
            prefEditor.putBoolean(TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED, isChecked).commit();
        } else if (buttonView.equals(sw_lock_screen)) {
            prefEditor.putBoolean(TorchieConstants.PREF_FUNC_SCREEN_LOCKED, isChecked).commit();
        } else if (buttonView.equals(sw_screen_off)) {
            prefEditor.putBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, isChecked).commit();
            setScreenOffOptionsUI(isChecked);
        } else if (buttonView.equals(cb_vibrate)) {
            prefEditor.putBoolean(TorchieConstants.PREF_FUNC_VIBRATE, isChecked).commit();
            if (isChecked) {
                Vibrator vib = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (vib.hasVibrator())
                    vib.vibrate(TorchieConstants.DEFAULT_VIBRATOR_TIME);
            }
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
            setScreenOffOptionsUI(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
        }
    }

    private void loadPreferences() {
        sw_screen_on.setChecked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_UNLOCKED, true));
        sw_lock_screen.setChecked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_LOCKED, true));
        sw_screen_off.setChecked(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
        setScreenOffOptionsUI(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF, false));
        cb_vibrate.setChecked(preferences.getBoolean(TorchieConstants.PREF_FUNC_VIBRATE, false));
    }

    private void setScreenOffOptionsRadioUI(boolean indefiniteEnabled) {
        if (indefiniteEnabled) {
            rg_screen_off_options.check(R.id.rb_settings_screen_off_indefinite);
        } else {
            rg_screen_off_options.check(R.id.rb_settings_screen_off_timeout);
        }
    }

    private void setScreenOffOptionsUI(boolean screenOffEnabled) {
        et_screen_off_sec.setText(String.valueOf((preferences.getLong(TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME, TorchieConstants.DEFAULT_SCREENOFF_TIME) % 60000) / 1000), TextView.BufferType.EDITABLE);
        et_screen_off_mins.setText(String.valueOf(preferences.getLong(TorchieConstants.PREF_FUNC_SCREEN_OFF_TIME, TorchieConstants.DEFAULT_SCREENOFF_TIME) / 60000), TextView.BufferType.EDITABLE);
        setScreenOffOptionsRadioUI(preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE, false));
        if (screenOffEnabled) {
            for (int i = 0; i < rg_screen_off_options.getChildCount(); i++) {
                rg_screen_off_options.getChildAt(i).setEnabled(true);
            }
            if (preferences.getBoolean(TorchieConstants.PREF_FUNC_SCREEN_OFF_INDEFINITE, false)) {
                et_screen_off_mins.setEnabled(false);
                et_screen_off_sec.setEnabled(false);
            } else {
                et_screen_off_mins.setEnabled(true);
                et_screen_off_sec.setEnabled(true);
            }
        } else {
            for (int i = 0; i < rg_screen_off_options.getChildCount(); i++) {
                rg_screen_off_options.getChildAt(i).setEnabled(false);
            }
            et_screen_off_mins.setEnabled(false);
            et_screen_off_sec.setEnabled(false);
        }
    }
}
