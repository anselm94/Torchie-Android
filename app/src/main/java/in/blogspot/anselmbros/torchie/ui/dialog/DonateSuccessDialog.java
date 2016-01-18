package in.blogspot.anselmbros.torchie.ui.dialog;

import android.app.DialogFragment;
import android.content.Intent;
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
public class DonateSuccessDialog extends DialogFragment implements View.OnClickListener {

    View rootView;
    Button butDismiss;
    Button butShare;

    public DonateSuccessDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_donate_success, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        butDismiss = (Button) rootView.findViewById(R.id.but_thanks_dismiss);
        butShare = (Button) rootView.findViewById(R.id.but_thanks_share);

        butDismiss.setOnClickListener(this);
        butShare.setOnClickListener(this);

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
        } else if (v == butShare) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent int_tell = new Intent(Intent.ACTION_SEND);
                    int_tell.setType("text/plain");
                    int_tell.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_info) + TorchieConstants.PLAY_URI);
                    startActivity(Intent.createChooser(int_tell, getResources().getString(R.string.share_via)));
                    dismiss();
                }

            }, 350L);

        }
    }

}
