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

package in.blogspot.anselmbros.torchie.ui.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

/**
 * Created by anselm94 on 21/4/16.
 */
public class LangSelectDialog extends DialogFragment {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    String languages[], lang_codes[];

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences(TorchieConstants.PREF_KEY_APP, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();
        segment_code(getResources().getStringArray(R.array.language_codes));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.language_select)
                .setItems(languages, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        prefEditor.putString(TorchieConstants.PREF_LOCALE, lang_codes[which]).apply();
                        Intent i = getActivity().getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
        return builder.create();
    }

    private void segment_code(String[] code){
        String temp[];
        languages = new String[code.length];
        lang_codes = new String[code.length];
        for(int i = 0; i < code.length; i++){
            temp = code[i].split(",");
            languages[i] = temp[0];
            lang_codes[i] = temp[1];
        }
    }
}
