package xyz.lebalex.daytask;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CALENDAR = 100;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CALENDAR) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCalen();
                startBackgroundService();

            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, PERMISSIONS_REQUEST_READ_CALENDAR);
        } else {
            getCalen();
            startBackgroundService();
        }
    }

    private void getCalen()  {
        try {
            final Context ctx = this;
            final List<ListTasks> eventList = new CalendarHelper().getListCalen(this);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        new NotyHelper().setNoty(ctx, eventList, ConstClass.ACTION_MANUAL_UPDATE);
                    }catch(Exception e)
                    {
                        Log.d("TaskMain",e.getMessage());
                    }
                }
            }).start();

            ListView lvMain = (ListView) findViewById(R.id.lvMain);
            ListTaskSAdapter adapter = new ListTaskSAdapter(this, eventList);
            lvMain.setAdapter(adapter);
            lvMain.setEmptyView(findViewById(R.id.emptyElement));
        }catch(Exception e)
        {
            Log.d("TaskMain",e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        MobileAds.initialize(this, "ca-app-pub-6392397454770928/1263238767");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build());

    }

    private void startBackgroundService() {
        try {

            Intent alarmIntent = new Intent(this, DayTaskServiceReceiver.class);
            alarmIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            PendingIntent pendingIntent;
            pendingIntent = PendingIntent.getBroadcast(this, 1011, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


            Calendar startCalen = Calendar.getInstance();
            //startCalen.add(Calendar.MINUTE, 1);

            startCalen.set(Calendar.HOUR_OF_DAY, 0);
            startCalen.set(Calendar.MINUTE, 15);
            startCalen.set(Calendar.SECOND, 0);
            startCalen.set(Calendar.MILLISECOND, 0);

            startCalen.add(Calendar.DATE, 1);

            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startCalen.getTimeInMillis(), pendingIntent);
        } catch (Exception e) {
            //LogWrite.LogError(this, e.getMessage());
        }
    }



}
