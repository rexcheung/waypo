package zxb.zweibo.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import zxb.zweibo.widget.AppManager;

/**
 * Created by rex on 15-8-25.
 */
public class CheckActivityReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Activity activity = AppManager.getAppManager().currentActivity();
        Log.i("CheckActivityReceiver", "CurrenActivity = " + activity.getClass().getSimpleName());
        Toast.makeText(context, activity.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        context.getApplicationContext().startService(new Intent(context, CheckNewWeiboService.class));
    }
}
