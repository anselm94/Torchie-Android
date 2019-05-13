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

package in.blogspot.anselmbros.torchie.ui.widget.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.preference.ListPreference;
import android.util.AttributeSet;

import in.blogspot.anselmbros.torchie.R;

/**
 * Created by Merbin J Anselm on 27-Jan-17.
 */

public class TimeoutListPreference extends ListPreference {

    @TargetApi(21)
    public TimeoutListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setTimeoutValues(this.getEntryValues());
    }

    @TargetApi(21)
    public TimeoutListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTimeoutValues(this.getEntryValues());
    }

    public TimeoutListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTimeoutValues(this.getEntryValues());
    }

    public TimeoutListPreference(Context context) {
        super(context);
        this.setTimeoutValues(this.getEntryValues());
    }

    protected void setTimeoutValues(CharSequence[] entryValues) {
        Resources res = this.getContext().getResources();

        String txtMinutes = res.getString(R.string.minutes);
        String txtSeconds = res.getString(R.string.seconds);
        String txtIndefinite = res.getString(R.string.indefinite);
        String txtOff = res.getString(R.string.off);

        CharSequence[] entries = new String[entryValues.length];
        for (int i = 0; i < entryValues.length; i++) {
            int val = Integer.parseInt(String.valueOf(entryValues[i]));
            switch (val) {
                case -1:
                    entries[i] = txtIndefinite;
                    break;
                case 0:
                    entries[i] = txtOff;
                    break;
                default:
                    String suffix = ((val / 60) > 0) ? txtMinutes : txtSeconds;
                    int prefix = ((val / 60) > 0) ? (val / 60) : (val % 60);
                    entries[i] = prefix + " " + suffix;
            }
        }
        this.setEntries(entries);
    }

}
