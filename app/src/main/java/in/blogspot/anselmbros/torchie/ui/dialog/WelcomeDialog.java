package in.blogspot.anselmbros.torchie.ui.dialog;

import android.app.DialogFragment;
import android.content.Context;
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
 * Created by anselm94 on 8/12/15.
 */
public class WelcomeDialog extends DialogFragment implements View.OnClickListener {

    View rootView;
    Button but_dismiss;

    public WelcomeDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_welcome, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        but_dismiss = (Button) rootView.findViewById(R.id.but_welcome_dismiss);
        but_dismiss.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        getActivity().getSharedPreferences(TorchieConstants.PREF_KEY_APP, Context.MODE_PRIVATE).edit().putBoolean(TorchieConstants.PREF_FIRST_TIME, false).commit();
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        if (v == but_dismiss) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    dismiss();
                }

            }, 350L);
        }
    }

}
