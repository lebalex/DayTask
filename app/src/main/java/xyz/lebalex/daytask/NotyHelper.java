package xyz.lebalex.daytask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotyHelper {
    public void setNoty(Context ctx, List<ListTasks> listTask, String action) throws Exception
    {
        //Log.d("TaskMain","setNoty");
        if(listTask.size()>0) {

            String GROUP_KEY_WORK_EMAIL = "xyz.lebalex.daytask.TASK_NOTY";
            Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_noty_lage);
            List<Notification> mNotification = new ArrayList<Notification>();
            Intent ii = new Intent(ctx.getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, ii, PendingIntent.FLAG_IMMUTABLE);

            for (ListTasks a: listTask) {
                String allDay="весь день";
                if(a.getDate()!=null)
                    allDay = a.getDate();
                Notification newMessageNotification = new NotificationCompat.Builder(ctx.getApplicationContext(), "notify_001")
                        .setSmallIcon(R.drawable.ic_noty)
                        .setContentIntent(pendingIntent)
                        //.setLargeIcon(bitmap)
                        .setContentTitle(allDay)
                        .setContentText(a.getTitle())
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        //.setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .setAutoCancel(false)

                        .build();
                mNotification.add(newMessageNotification);


            }


            String task=" задач.";
            switch(listTask.size())
            {
                case 1: task=" задача.";break;
                case 2: case 3: case 4: task=" задачи.";break;
                default: task=" задач.";break;

            }
            Calendar calen = Calendar.getInstance();
            String time = calen.get(Calendar.HOUR_OF_DAY)+":"+calen.get(Calendar.MINUTE)+":"+calen.get(Calendar.SECOND);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mNotification.size() > 1) {

                Notification summaryNotification =
                        new NotificationCompat.Builder(ctx.getApplicationContext(), "notify_001")
                                .setContentIntent(pendingIntent)
                                .setContentTitle(ctx.getString(R.string.today_event))
                                //.setContentText("У вас "+Integer.toString(listTask.size())+task+" "+time)
                                .setSmallIcon(R.drawable.ic_noty)

                                .setStyle(new NotificationCompat.InboxStyle()
                                        .setSummaryText("У вас " + Integer.toString(listTask.size()) + task + " " + time))

                                .setGroup(GROUP_KEY_WORK_EMAIL)
                                .setGroupSummary(true)
                                .setOngoing(true)
                                .setAutoCancel(false)
                                //.setPriority(Notification.PRIORITY_MAX)
                                //.setLargeIcon(bitmap)

                                .build();
                mNotification.add(summaryNotification);
            }


            NotificationManager mNotificationManager =
                    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("notify_001",
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH
                );
                mNotificationManager.createNotificationChannel(channel);
            }

            int notyNumber=0;
            for (Notification a: mNotification)
                mNotificationManager.notify(++notyNumber, a);

            //mNotificationManager.notify(270177, summaryNotification);




        }
        else {
            NotificationManager mNotificationManager =
                    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();
        }

        if (!action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intent = new Intent(ctx, DayTaskWidget.class);
            intent.setAction(action);
            intent.putExtra("listEventSize", listTask.size());
            ctx.sendBroadcast(intent);
        }


    }
}
