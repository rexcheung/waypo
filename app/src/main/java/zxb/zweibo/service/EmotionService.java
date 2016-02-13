package zxb.zweibo.service;

import android.app.IntentService;
import android.content.Intent;

import zxb.zweibo.GlobalApp;
import zxb.zweibo.Utils.EmotionUtil;

/**
 * Created by rex on 16-2-11.
 */
public class EmotionService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public EmotionService(String name) {
        super(name);
    }
    public EmotionService() {
        super("EmotionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        EmotionUtil emotionUtil = new EmotionUtil(GlobalApp.getInstance());
        emotionUtil.insertEmotions();
        emotionUtil.closeDB();
    }
}
