package zxb.zweibo.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import zxb.zweibo.R;
import zxb.zweibo.ui.MainActivityNew;
import zxb.zweibo.widget.AppManager;

/**
 * Created by rex on 15-8-24.
 */
public class CheckNewWeiboService extends Service {

    private String TAG = "CheckNewWeiboService";

    NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        /*Notification notification = new Notification(R.drawable.ic_com_sina_weibo_sdk_logo, "Hoho", 5000);
        Intent notifiIntent = new Intent(getApplicationContext(), MainActivityNew.class);
        PendingIntent activity = PendingIntent.getActivity(getApplicationContext(), 0, notifiIntent, 0);
        notification.setLatestEventInfo(this, "title", "thtle content", activity);
        startForeground(1, notification);*/

//        showNotifi();

//        new CheckTimer().execute(10000);
//        aboutActivity();

        alarm();

        return super.onStartCommand(intent, flags, startId);
    }

    private void alarm() {
//        Intent intent=new Intent(this,CheckActivityReceiver.class);
//        intent.setAction("VIDEO_TIMER");
//        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
//        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
//        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 10*1000, sender);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        int tenSecond = 5*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + tenSecond;
        Intent intent = new Intent(this, CheckActivityReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

    }

    private void aboutActivity() {
        Activity currentActivity = AppManager.getAppManager().currentActivity();
        Log.i(TAG, "CurrentActivity = " + currentActivity.getClass().getSimpleName());
        Log.i(TAG, "CurrentThread = " + Thread.currentThread());
    }

    private void showNotifi() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_com_sina_weibo_sdk_logo)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivityNew.class);
        //加入下面的两行，即可在点击通知栏后回到之前打开的Activity，不会再创建
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivityNew.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager =
                (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mNotificationManager.cancel(1);
        Log.i(TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    class CheckTimer extends AsyncTask<Integer,Void,Void>{
        @Override
        protected Void doInBackground(Integer... params) {
            while (true) {
                aboutActivity();
                try {
                    Thread.sleep(params[0]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
