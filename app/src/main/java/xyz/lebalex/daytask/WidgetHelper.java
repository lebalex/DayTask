package xyz.lebalex.daytask;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.widget.RemoteViews;

public class WidgetHelper {

    public void updateWidget(Context ctx, AppWidgetManager appWidgetManager,
                             int widgetID, int count)
    {
        try {

            RemoteViews widgetView;
            widgetView = new RemoteViews(ctx.getPackageName(),
                    R.layout.app_widget_count);

            if(count>0) {
                widgetView.setImageViewResource(R.id.red_ball, R.drawable.red_ball);
                //widgetView.setImageViewResource(R.id.bg, R.drawable.bg_c2);
                widgetView.setTextViewText(R.id.counter, Integer.toString(count));
                if(count<100)
                    widgetView.setTextViewTextSize(R.id.counter, TypedValue.COMPLEX_UNIT_DIP, 16);
                else
                    widgetView.setTextViewTextSize(R.id.counter, TypedValue.COMPLEX_UNIT_DIP, 12);


            }
            else {
                widgetView.setImageViewResource(R.id.red_ball, R.drawable.red_ball_emty);
                //widgetView.setImageViewResource(R.id.bg, R.drawable.bg2);
                widgetView.setTextViewText(R.id.counter, "N");
                widgetView.setTextViewTextSize(R.id.counter, TypedValue.COMPLEX_UNIT_DIP, 12);
            }

            Intent configIntent = new Intent(ctx, MainActivity.class);
            configIntent.setAction(ConstClass.ACTION_MANUAL_UPDATE);
            PendingIntent pIntent = PendingIntent.getActivity(ctx, widgetID,
                    configIntent, PendingIntent.FLAG_IMMUTABLE);

            widgetView.setOnClickPendingIntent(R.id.bg, pIntent);


            appWidgetManager.updateAppWidget(widgetID, widgetView);


        } catch (Exception e1) {

        }
    }
}
