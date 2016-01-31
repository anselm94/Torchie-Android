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

package in.blogspot.anselmbros.torchie.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import in.blogspot.anselmbros.torchie.R;
import in.blogspot.anselmbros.torchie.misc.TorchieConstants;
import in.blogspot.anselmbros.torchie.service.TorchieQuick;

public class ScreenflashActivity extends Activity {

    CloseActivityReceiver closeActivityReceiver;
    TorchieQuick torchieQuickService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenflash);

        setScreenBrightness(1f);

        closeActivityReceiver = new CloseActivityReceiver();
        registerReceiver(closeActivityReceiver,new IntentFilter(TorchieConstants.BROADCAST_CLOSE_ACTIVITY));
    }

    @Override
    protected void onPause(){
        overridePendingTransition(0, 0); //Disable exit animation
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(closeActivityReceiver);
        torchieQuickService = TorchieQuick.getSharedInstance();
        if(torchieQuickService!= null)
            torchieQuickService.notifyScreenlightStatus(false);
        super.onDestroy();
    }

    private void setScreenBrightness(float value){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = value; // 0f - no backlight ... 1f - full backlight
        getWindow().setAttributes(lp);
    }

    public class CloseActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("in.blogspot.anselmbros.torchie.CLOSE_ACTIVITY")) {
                finish();
            }
        }
    }
}
