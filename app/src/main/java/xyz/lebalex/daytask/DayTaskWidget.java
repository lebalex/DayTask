package xyz.lebalex.daytask;

/**
 * Created by ivc_lebedevav on 30.01.2017.
 */

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class DayTaskWidget extends AppWidgetProvider {


    public static String ACTION_APPWIDGET_ENABLED = "android.appwidget.action.APPWIDGET_ENABLED";
    private static int listEventSize=-1;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        try {

            if (ConstClass.ACTION_MANUAL_UPDATE.equalsIgnoreCase(intent.getAction()) || ACTION_APPWIDGET_ENABLED.equalsIgnoreCase(intent.getAction())
                    /*|| "android.appwidget.action.APPWIDGET_UPDATE".equalsIgnoreCase(intent.getAction())*/
                    ) {
                //Log.d("DayTaskWidget",intent.getAction());
                listEventSize = intent.getIntExtra("listEventSize", -1);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                ComponentName thisAppWidget = new ComponentName(context.getPackageName(), DayTaskWidget.class.getName());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                onUpdate(context, appWidgetManager, appWidgetIds);


            }
        }catch (Exception e) {
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
    }

    static void updateWidget(Context ctx, AppWidgetManager appWidgetManager,
                             int widgetID) {
        try {
                WidgetHelper mWidgetHelper = new WidgetHelper();
                mWidgetHelper.updateWidget(ctx, appWidgetManager, widgetID, getTaskCount(ctx));
        } catch (Exception e1) {

        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Nullable
    private static int getTaskCount(Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (listEventSize>-1) {
            return listEventSize;
        }else {
            CalendarHelper mCalendarHelper = new CalendarHelper();
            return (mCalendarHelper.getListCalen(context)).size();
        }
    }

}
