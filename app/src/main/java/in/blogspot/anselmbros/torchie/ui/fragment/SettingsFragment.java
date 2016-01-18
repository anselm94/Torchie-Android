package in.blogspot.anselmbros.torchie.ui.fragment;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define the settings file to use by this settings fragment
        getPreferenceManager().setSharedPreferencesName(TorchieConstants.PREF_KEY_APP);
        addPreferencesFromResource(R.xml.preferences);
    }
}
