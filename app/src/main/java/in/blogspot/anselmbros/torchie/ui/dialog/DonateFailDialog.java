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

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

/**
 * Created by anselm94 on 9/12/15.
 */
public class DonateFailDialog extends DialogFragment implements View.OnClickListener {

    View rootView;
    Button butDismiss, butProceed;

    public DonateFailDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_donate_failure, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        butDismiss = (Button) rootView.findViewById(R.id.but_error_dismiss);
        butProceed = (Button) rootView.findViewById(R.id.but_proceed_paypal);

        butDismiss.setOnClickListener(this);
        butProceed.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == butDismiss) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    dismiss();
                }

            }, 350L);
        } else if (v == butProceed) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(TorchieConstants.WEB_DONATE_URI));
                    startActivity(intent);
                    dismiss();
                }

            }, 350L);

        }
    }

}
