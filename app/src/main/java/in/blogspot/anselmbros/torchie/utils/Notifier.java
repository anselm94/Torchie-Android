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

package in.blogspot.anselmbros.torchie.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import in.blogspot.anselmbros.torchie.R;

/**
 * Created by anselm94 on 21/4/16.
 */
public class Notifier {
    NotificationCompat.Builder mBuilder;
    private Context mContext;

    public Notifier(Context context) {
        this.mContext = context;
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_logo_mono);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        mBuilder.setContentTitle(String.format(context.getResources().getString(R.string.notify_title)));
        mBuilder.setContentText(String.format(context.getResources().getString(R.string.notify_text)));
        mBuilder.setAutoCancel(true);
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
    }

    public void show() {
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
