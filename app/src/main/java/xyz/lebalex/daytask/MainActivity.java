package xyz.lebalex.daytask;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.util.Log;

import android.widget.ListView;
import android.widget.Toast;


import java.util.Calendar;
import java.util.List;
import java.util.Map;

/*
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;*/


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CALENDAR = 100;
    final String[] PERMISSIONS = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.POST_NOTIFICATIONS
    };
    private static Activity thisActivity = null;
    private ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract;
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;

    private ActivityResultContracts.RequestPermission singlePermissionsContract;
    private ActivityResultLauncher<String> singlePermissionLauncher;
    private void askPermissions(ActivityResultLauncher<String[]> multiplePermissionLauncher) {
        if (!hasPermissionsREAD_CALENDAR() && !hasPermissionsPOST_NOTIFICATIONS()) {
            multiplePermissionLauncher.launch(PERMISSIONS);
        } else if (!hasPermissionsREAD_CALENDAR()) {
            multiplePermissionLauncher.launch(PERMISSIONS);
        } else {
            startBackgroundService();
            new Thread(new Runnable() {
                public void run() {
                    getCalen();
                }
            }).start();
        }
    }

    private boolean hasPermissionsREAD_CALENDAR() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
    private boolean hasPermissionsPOST_NOTIFICATIONS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*@Override
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
    }*/

    @Override
    protected void onResume() {
        super.onResume();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, PERMISSIONS_REQUEST_READ_CALENDAR);
        }else {
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
            Log.e("TaskMain",e.getMessage(),e);
            Log.d("TaskMain",e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        singlePermissionsContract = new ActivityResultContracts.RequestPermission();
        singlePermissionLauncher = registerForActivityResult(singlePermissionsContract, isGranted -> {
            if (isGranted) {
                startBackgroundService();
                new Thread(new Runnable() {
                    public void run() {
                        getCalen();
                    }
                }).start();
            }
        });


        multiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionLauncher = registerForActivityResult(multiplePermissionsContract,
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> isGranted) {
                        /*if (isGranted.containsValue(false)) {
                            multiplePermissionLauncher.launch(PERMISSIONS);
                        }*/
                        if (isGranted.containsKey(Manifest.permission.READ_CALENDAR) && isGranted.get(Manifest.permission.READ_CALENDAR) == true) {
                            startBackgroundService();
                            new Thread(new Runnable() {
                                public void run() {
                                    getCalen();
                                }
                            }).start();
                        } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                            builder.setTitle("Необходим доступ к календарю");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setMessage("Предоставьте доступ к календарю");
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    singlePermissionLauncher.launch(Manifest.permission.READ_CALENDAR);
                                }
                            });
                            builder.show();
                        } else if (isGranted.containsKey(Manifest.permission.READ_CALENDAR) && isGranted.get(Manifest.permission.READ_CALENDAR) == false) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                            builder.setTitle("Необходим доступ к календарю");
                            builder.setPositiveButton(android.R.string.ok, null);
                            builder.setMessage("Предоставьте доступ к календарю");
                            builder.show();
                        }

                    }

                });
        askPermissions(multiplePermissionLauncher);




      /*  MobileAds.initialize(this, "ca-app-pub-6392397454770928~5034042594");*/



       /* AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
        mAdView.loadAd(adRequest);
*/
        /*mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                String errorSrt="";
                if(errorCode == AdRequest.ERROR_CODE_INTERNAL_ERROR)
                    errorSrt="ERROR_CODE_INTERNAL_ERROR";
                else if(errorCode == AdRequest.ERROR_CODE_INVALID_REQUEST)
                    errorSrt="ERROR_CODE_INVALID_REQUEST";
                else if(errorCode == AdRequest.ERROR_CODE_NETWORK_ERROR)
                    errorSrt="ERROR_CODE_NETWORK_ERROR";
                else if(errorCode == AdRequest.ERROR_CODE_NO_FILL)
                    errorSrt="ERROR_CODE_NO_FILL";

                //Toast.makeText(getApplication(), errorSrt, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });*/

    }

    private void startBackgroundService() {
        try {

            Intent alarmIntent = new Intent(this, DayTaskServiceReceiver.class);
            alarmIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            PendingIntent pendingIntent;
            pendingIntent = PendingIntent.getBroadcast(this, 1011, alarmIntent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


            Calendar startCalen = Calendar.getInstance();
            //startCalen.add(Calendar.MINUTE, 1);

            startCalen.set(Calendar.HOUR_OF_DAY, 0);
            startCalen.set(Calendar.MINUTE, 15);
            startCalen.set(Calendar.SECOND, 0);
            startCalen.set(Calendar.MILLISECOND, 0);

            startCalen.add(Calendar.DATE, 1);

            manager.setExact(AlarmManager.RTC_WAKEUP, startCalen.getTimeInMillis(), pendingIntent);
        } catch (Exception e) {
            //LogWrite.LogError(this, e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.politic) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.politic_link)));
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
