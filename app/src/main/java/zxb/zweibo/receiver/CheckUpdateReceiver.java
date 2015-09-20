package zxb.zweibo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import zxb.zweibo.service.CheckUpdateService;

/**
 * 启动CheckUpdateService的广播接收器.
 *
 * Created by rex on 15-8-26.
 */
public class CheckUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Activity activity = AppManager.getAppManager().currentActivity();
//        Log.i("CheckActivityReceiver", "CurrenActivity = " + activity.getClass().getSimpleName());
//        Toast.makeText(context, activity.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        context.getApplicationContext().startService(new Intent(context, CheckUpdateService.class));
//        MainActivityNew mainActivity = (MainActivityNew) AppManager.getActivity(MainActivityNew.class);
//        mainActivity.refreshList();
//        mainActivity.
    }
}