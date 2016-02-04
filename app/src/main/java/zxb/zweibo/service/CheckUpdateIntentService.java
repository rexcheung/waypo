package zxb.zweibo.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.R;
import zxb.zweibo.Utils.GsonUtils;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.bean.FTLIds;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.receiver.CheckUpdateReceiver;
import zxb.zweibo.ui.MainActivityF;

/**
 * 后台检查有多少条微博更新.
 * 然后在通知栏提示.
 * Created by rex on 15-8-26.
 */
public class CheckUpdateIntentService extends IntentService {
    private Long lastId;
    private WeiboAPIUtils mWeiboUtil;
    private Gson gson;
    private List<Long> mIds;
    private boolean firstStart;
    private AlarmManager alarm;
    private PendingIntent pi;
    private NotificationManager mNotifier;
    private NotificationCompat.Builder mBuilder;
    private Intent mResultIntent;
    private TaskStackBuilder mStackBuilder;

    public static final String LAST_ID = "last_weibo_id";

    public static final String STOP_SERVICE = "stop_notify_service";
    private static boolean isStop = false;

    /**
     * 检查更新的间隔，单位分钟.
     */
    private final int UPDATE_MINS = 5;

    public CheckUpdateIntentService() {
        super("CheckUpdateIntentService");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CheckUpdateIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWeiboUtil = WeiboAPIUtils.getInstance();
        gson = GsonUtils.getGson();
        mIds = new ArrayList<>();
        firstStart = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isStop = intent.getBooleanExtra(STOP_SERVICE, false);
        if (isStop){
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        lastId = 0l;
//        lastId = EventBus.getDefault().getStickyEvent(LastWeibo.class).getLastId();
        lastId = intent.getLongExtra(LAST_ID, -1);
        Logger.i("LastId = " + lastId);

        if (lastId <= 01){
            return;
        }

        // 首次运行时不检查，因为首次一定已经是最新的
        if (!firstStart) {
            mWeiboUtil.imageFTLIds(lastId, 0, mListener);
            Logger.i("检查更新...");
        }else {
            firstStart = false;
        }

        setTimer();
    }

    /**
     * 指定的时间间隔发送广播，在广播在接收类里面再调用这个Service的onStartCommand
     */
    private void setTimer() {
        if (alarm==null){
            alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        int time = UPDATE_MINS*20*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + time;

        Intent intent = new Intent(this, CheckUpdateReceiver.class);
        intent.putExtra(LAST_ID, lastId);
        pi = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String s) {
            FTLIds tempIds = gson.fromJson(s, FTLIds.class);
            if (tempIds!=null){
                mIds.clear();
                mIds = tempIds.getStatuses();

                if (isUpdate()) {
                    Logger.i("您有新的微博"+mIds.size()+"条");
                    showNotification();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }
    };

    private void showNotification() {
        if (mNotifier==null){
            mNotifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (mBuilder == null){
            mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.ic_com_sina_weibo_sdk_logo);
            mBuilder.setContentTitle("亲，您有新的微博");
        }
        mBuilder.setContentText(mIds.size() + "条");

        if (mResultIntent == null) {
            mResultIntent = new Intent(this, MainActivityF.class);
        }
        //加入下面的两行，即可在点击通知栏后回到之前打开的Activity，不会再创建
        mResultIntent.setAction(Intent.ACTION_MAIN);
        mResultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        if (mStackBuilder ==null){
            mStackBuilder = TaskStackBuilder.create(this);
            mStackBuilder.addParentStack(MainActivityF.class);
            mStackBuilder.addNextIntent(mResultIntent);
        }

        PendingIntent resultPendingIntent =
                mStackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);

        mNotifier.notify(1, mBuilder.build());
    }

    public boolean isUpdate() {
        return mIds.size() != 0;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // 关闭程序时需要把广播也关闭也，否则会一直在后台刷
        alarm.cancel(pi);
    }

}
