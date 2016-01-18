package in.blogspot.anselmbros.torchie.ui.dialog;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import in.blogspot.anselmbros.torchie.R;

/**
 * Created by anselm94 on 8/12/15.
 */
public class PermissionDialog extends DialogFragment implements View.OnClickListener {

    View rootView;
    TextView tvPermissionNote, tvPermissionEnable, tvPermissionLater, tvPermissionDetailed;

    public PermissionDialog() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_permission, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        tvPermissionNote = (TextView) rootView.findViewById(R.id.tv_permission_note);
        tvPermissionEnable = (TextView) rootView.findViewById(R.id.tv_permission_enable);
        tvPermissionLater = (TextView) rootView.findViewById(R.id.tv_permission_later);
        tvPermissionDetailed = (TextView) rootView.findViewById(R.id.tv_permission_detail);

        tvPermissionDetailed.setMovementMethod(LinkMovementMethod.getInstance());
        tvPermissionNote.setMovementMethod(LinkMovementMethod.getInstance());
        tvPermissionEnable.setOnClickListener(this);
        tvPermissionLater.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == tvPermissionEnable) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    openAccessibilitySettings();
                    dismiss();
                }

            }, 350L);

        } else if (v == tvPermissionLater) {
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    dismiss();
                }

            }, 350L);
        }
    }

    private void openAccessibilitySettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
}
