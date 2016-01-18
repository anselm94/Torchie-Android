package in.blogspot.anselmbros.torchie;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class ScreenOffWakeupLockService extends Service {

    IntentFilter screenStateFilter;
    BroadcastReceiver mScreenStateReceiver;

    public ScreenOffWakeupLockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        screenStateFilter = new IntentFilter();
        mScreenStateReceiver = new ScreenOffReceiver();

        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, screenStateFilter);
        return super.onStartCommand(intent, flags, startId);
    }


    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        unregisterReceiver(mScreenStateReceiver);
        Intent intent = new Intent("in.blogspot.anselmbros.torchie.distresscast");
        sendBroadcast(intent);
    }
}
