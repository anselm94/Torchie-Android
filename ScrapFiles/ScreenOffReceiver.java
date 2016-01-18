package in.blogspot.anselmbros.torchie;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class ScreenOffReceiver extends BroadcastReceiver {

    static String TAG = "ScreenOffReceiver";
    static ComponentName component;

    public ScreenOffReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
            Log.e(TAG,"Screen OFffffffffff!!");
            component = new ComponentName(context, RockerReceiver.class);
            //Disable
            context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            //Enable
            context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else if (intent.getAction().equals("in.blogspot.anselmbros.torchie.distresscast")) {
            restartService(context);
        } else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            restartService(context);
        }
    }

    private void restartService(Context context) {
        context.startService(new Intent(context, ScreenOffWakeupLockService.class));
        ;
    }
}
