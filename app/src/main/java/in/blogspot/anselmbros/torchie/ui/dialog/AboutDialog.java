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
    TextView tvAboutNote, tvAboutAnselm, tvVisitSite, tvJoinCommunity, tvFacebook, tvGoogle, tvTranslatorNote;

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

        tvAboutNote.setMovementMethod(LinkMovementMethod.getInstance());
        tvTranslatorNote.setMovementMethod(LinkMovementMethod.getInstance());
        tvAboutAnselm.setOnClickListener(this);
        tvVisitSite.setOnClickListener(this);
        tvJoinCommunity.setOnClickListener(this);
        tvFacebook.setOnClickListener(this);
        tvGoogle.setOnClickListener(this);
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
