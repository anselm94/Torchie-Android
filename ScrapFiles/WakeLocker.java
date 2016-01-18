package in.blogspot.anselmbros.torchie;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by anselm94 on 9/11/15.
 */
public abstract class WakeLocker {

    static String TAG = "Class Wakelocker";

    private static PowerManager.WakeLock wakeLock;

    public static void acquireBrightnessLock(Context ctx) {
//        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, MainActivity.APP_TAG);
        wakeLock.acquire();
        Log.e(TAG,"Wakelock acquired!");
    }
    
        public static void acquireCPULock(Context ctx) {
//        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, MainActivity.APP_TAG);
        wakeLock.acquire();
        Log.e(TAG,"Wakelock acquired!");
    }

    public static void release() {
        if (wakeLock != null) wakeLock.release(); wakeLock = null;
        Log.e(TAG,"Wakelock released!");
    }

    public static boolean isAlive()
    {
        if(wakeLock != null)
        {
            Log.e(TAG,"Wakelock is NOT NULL!");
            return true;
        }else
        {
            Log.e(TAG,"Wakelock is NULL!");
            return false;
        }
    }
    public static boolean isHeld()
    {
        boolean isHeld = wakeLock.isHeld();
        Log.e(TAG,"Wakelock held? " + String.valueOf(isHeld));
        return isHeld;
    }
}