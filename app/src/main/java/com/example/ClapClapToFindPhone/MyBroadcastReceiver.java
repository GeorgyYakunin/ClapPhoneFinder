package com.example.ClapClapToFindPhone;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private Intent i;
    private Context mCtx;

    public void onReceive(Context context, Intent intent) {
        this.mCtx = context;
        if (!new ClassesApp(context).read("StopService", "1").equals("1")) {
            startAlert(context);
            if (!isMyServiceRunning(VocalService.class)) {
                this.i = new Intent(context, VocalService.class);
                context.startService(this.i);
            }
        }
    }

    public void startAlert(Context context) {
        ((AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM)).set(0, System.currentTimeMillis() + ((long) 1000), PendingIntent.getBroadcast(context, 234, new Intent(context, MyBroadcastReceiver.class), 0));
    }

    private boolean isMyServiceRunning(Class<?> cls) {
        for (RunningServiceInfo runningServiceInfo : ((ActivityManager) this.mCtx.getSystemService("activity")).getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
