package xyz.lebalex.daytask;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;


/**
 * Created by ivc_lebedevav on 12.01.2017.
 */

public class DayTaskServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        startBackgroundService(context);
        if(intent!=null)
            doWork(context, intent.getAction());
    }

    private void startBackgroundService(Context context) {
        try {
            Intent alarmIntent = new Intent(context, DayTaskServiceReceiver.class);
            alarmIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            PendingIntent pendingIntent;
            pendingIntent = PendingIntent.getBroadcast(context, 1011, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


            Calendar startCalen = Calendar.getInstance();
            //startCalen.add(Calendar.MINUTE, 120);

            startCalen.set(Calendar.HOUR_OF_DAY, 0);
            startCalen.set(Calendar.MINUTE, 15);
            startCalen.set(Calendar.SECOND, 0);
            startCalen.set(Calendar.MILLISECOND, 0);

            startCalen.add(Calendar.DATE, 1);

            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startCalen.getTimeInMillis(), pendingIntent);

        } catch (Exception e) {
            Log.d("DayTaskServiceReceiver", e.getMessage());
        }
    }

    private static List<ListTasks> getTasks(Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        CalendarHelper mCalendarHelper = new CalendarHelper();
        return mCalendarHelper.getListCalen(context);
    }
    private void doWork(Context ctx, String action)
    {
        try {
            new NotyHelper().setNoty(ctx, getTasks(ctx), (action==null)?ConstClass.ACTION_MANUAL_UPDATE:action);
        }catch(Exception e){}
    }


}
