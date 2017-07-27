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

package in.blogspot.anselmbros.torchie.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import in.blogspot.anselmbros.torchie.R;

/**
 * Created by I327891 on 02-Feb-17.
 */

public class SettingsUtils {

    public static final String PREF_TORCH_TIMEOUT = "pref_torch_timeout";
    public static final String PREF_TORCH_SOURCE = "pref_torch_source";
    public static final String PREF_VIBRATE = "pref_vibrate";
    public static final String PREF_LANGUAGE = "pref_language";
    private static final String PREF_FIRST_TIME = "pref_first_time";
    private static final String PREF_SCREEN_ON = "pref_screen_on";
    private static final String PREF_SCREEN_LOCK = "pref_screen_lock";
    private static final String PREF_SCREEN_OFF = "pref_screen_off_timeout";
    private static final String PREF_PROXIMITY = "pref_proximity";

    public static boolean isFirstTime(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SettingsUtils.PREF_FIRST_TIME, true);
    }

    public static void setFirstTime(final Context context, final boolean newValue) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(SettingsUtils.PREF_FIRST_TIME, newValue).apply();
    }

    public static boolean isScreenOnEnabled(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SettingsUtils.PREF_SCREEN_ON, context.getResources().getBoolean(R.bool.pref_default_screen_on));
    }

    public static boolean isScreenLockEnabled(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SettingsUtils.PREF_SCREEN_LOCK, context.getResources().getBoolean(R.bool.pref_default_screen_lock));
    }

    public static boolean isScreenOffEnabled(final Context context) {
        int value = SettingsUtils.getScreenOffTimeoutSec(context);
        return value != 0;
    }

    public static boolean isScreenOffIndefinite(final Context context) {
        int value = SettingsUtils.getScreenOffTimeoutSec(context);
        return value == -1;
    }

    public static int getScreenOffTimeoutSec(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sp.getString(SettingsUtils.PREF_SCREEN_OFF, context.getResources().getString(R.string.pref_default_screen_off_timeout)));
    }

    public static boolean isTorchTimeoutIndefinite(final Context context) {
        int value = SettingsUtils.getTorchTimeout(context);
        return value == -1;
    }

    public static int getTorchTimeout(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sp.getString(SettingsUtils.PREF_TORCH_TIMEOUT, context.getResources().getString(R.string.pref_default_torch_timeout)));
    }

    public static String getTorchSource(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(SettingsUtils.PREF_TORCH_SOURCE, context.getResources().getString(R.string.pref_default_torch_source));
    }

    public static boolean isProximityEnabled(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SettingsUtils.PREF_PROXIMITY, context.getResources().getBoolean(R.bool.pref_default_proximity));
    }

    public static boolean isVibrateEnabled(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SettingsUtils.PREF_VIBRATE, context.getResources().getBoolean(R.bool.pref_default_vibrate));
    }

    public static String getLanguage(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(SettingsUtils.PREF_LANGUAGE, context.getResources().getString(R.string.pref_default_language));
    }
}
