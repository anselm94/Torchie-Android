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
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

/**
 * Created by anselm94 on 8/12/15.
 */
public class AboutDialog extends DialogFragment implements View.OnClickListener {

    View rootView;
    TextView tvAboutNote, tvAboutAnselm, tvVisitSite, tvJoinCommunity, tvFacebook, tvGoogle, tvTranslatorNote, tvNotice;

    String notice;

    public AboutDialog() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_about, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        tvAboutNote = (TextView) rootView.findViewById(R.id.tv_about_note);
        tvAboutAnselm = (TextView) rootView.findViewById(R.id.tv_about_anselm);
        tvVisitSite = (TextView) rootView.findViewById(R.id.tv_visit_site);
        tvJoinCommunity = (TextView) rootView.findViewById(R.id.tv_join_community);
        tvFacebook = (TextView) rootView.findViewById(R.id.tv_facebook);
        tvGoogle = (TextView) rootView.findViewById(R.id.tv_googleplus);
        tvTranslatorNote = (TextView) rootView.findViewById(R.id.tv_translator_note);
        tvNotice = (TextView) rootView.findViewById(R.id.tv_notice);

        tvAboutNote.setMovementMethod(LinkMovementMethod.getInstance());
        tvTranslatorNote.setMovementMethod(LinkMovementMethod.getInstance());
        tvAboutAnselm.setOnClickListener(this);
        tvVisitSite.setOnClickListener(this);
        tvJoinCommunity.setOnClickListener(this);
        tvFacebook.setOnClickListener(this);
        tvGoogle.setOnClickListener(this);
        tvNotice.setMovementMethod(LinkMovementMethod.getInstance());

        try {
            notice = String.format(getActivity().getResources().getString(R.string.notice), getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
            tvNotice.setText(notice);
        } catch (Exception e) {

        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (v == tvAboutAnselm) {
            intent.setData(Uri.parse(TorchieConstants.ABOUTANSELM_URI));
        } else if (v == tvVisitSite) {
            intent.setData(Uri.parse(TorchieConstants.WEB_URI));
        } else if (v == tvJoinCommunity) {
            intent.setData(Uri.parse(TorchieConstants.COMMUNITY_URI));
        } else if (v == tvFacebook) {
            intent.setData(Uri.parse(TorchieConstants.FACEBOOK_URI));
        } else if (v == tvGoogle) {
            intent.setData(Uri.parse(TorchieConstants.GOOGLEPLUS_URI));
        }
        startActivity(intent);

    }
}
