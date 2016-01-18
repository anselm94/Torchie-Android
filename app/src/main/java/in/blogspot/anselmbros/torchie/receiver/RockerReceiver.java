package in.blogspot.anselmbros.torchie.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import in.blogspot.anselmbros.torchie.misc.TorchieConstants;

public class RockerReceiver extends BroadcastReceiver {

    public String TAG = TorchieConstants.INFO;

    public RockerReceiver() {
        TAG = this.getClass().getName();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals("android.media.VOLUME_CHANGED_ACTION")) {
            int prev_volume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 0);
            int current_volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);

            AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            if (accessibilityManager != null && accessibilityManager.isEnabled()) {
                AccessibilityEvent e = AccessibilityEvent.obtain();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    e.setEventType(AccessibilityEvent.TYPE_ANNOUNCEMENT);
                }
                e.setClassName(TorchieConstants.ACC_VOLUME_CHANGE); //define type of broadcast event
                e.setPackageName(context.getPackageName());
                e.getText().add(String.valueOf(prev_volume) + "," + String.valueOf(current_volume));
                e.setEnabled(true);
                accessibilityManager.sendAccessibilityEvent(e);
            }
        }

//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
