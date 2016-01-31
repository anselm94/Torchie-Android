package in.blogspot.anselmbros.torchie.utils;

import android.content.Context;
import android.content.Intent;

import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.ui.activity.ScreenlightActivity;

/**
 * Created by anselm94 on 27/1/16.
 */
public class Screenflash {

    private Context mContext;

    public Screenflash(Context context){
        this.mContext = context;
    }

    public void turnOn(){
        Intent intent = new Intent(this.mContext, ScreenlightActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mContext.startActivity(intent);
    }

    public void turnOff(){
        this.mContext.sendBroadcast(new Intent(TorchieConstants.BROADCAST_CLOSE_ACTIVITY));
    }

    public boolean isFlashOn(){

    }
}
