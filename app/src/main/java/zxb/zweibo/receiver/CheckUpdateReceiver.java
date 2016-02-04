package zxb.zweibo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import zxb.zweibo.service.CheckUpdateIntentService;
import zxb.zweibo.service.CheckUpdateService;

/**
 * 启动CheckUpdateService的广播接收器.
 *
 * Created by rex on 15-8-26.
 */
public class CheckUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(CheckUpdateIntentService.LAST_ID, -1);
        if (id == -1){
            return;
        }
        Intent notify = new Intent(context, CheckUpdateIntentService.class);
        notify.putExtra(CheckUpdateIntentService.LAST_ID, id);
        context.getApplicationContext().startService(notify);
    }
}