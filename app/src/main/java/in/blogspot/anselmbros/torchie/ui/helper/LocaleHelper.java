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

package in.blogspot.anselmbros.torchie.ui.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

import in.blogspot.anselmbros.torchie.utils.SettingsUtils;

/**
 * Created by I327891 on 19-Feb-17.
 */

public class LocaleHelper {

    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        return setLocale(context, lang);
    }

    private static Context setLocale(Context context, String languageCode) {
        String[] languageCodes = languageCode.split("-");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (languageCodes.length == 1) {
                return updateResources(context, languageCodes[0], "");
            } else {
                return updateResources(context, languageCodes[0], languageCodes[1]);
            }
        } else {
            if (languageCodes.length == 1) {
                return updateResourcesLegacy(context, languageCodes[0], "");
            } else {
                return updateResourcesLegacy(context, languageCodes[0], languageCodes[1]);
            }
        }
    }

    private static String getPersistedData(Context context, String defaultLanguage) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SettingsUtils.PREF_LANGUAGE, defaultLanguage);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language, String country) {
        Locale locale;
        if (country.equals("")) {
            locale = new Locale(language);
        } else {
            locale = new Locale(language, country);
        }
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language, String country) {
        Locale locale;
        if (country.equals("")) {
            locale = new Locale(language);
        } else {
            locale = new Locale(language, country);
        }
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}