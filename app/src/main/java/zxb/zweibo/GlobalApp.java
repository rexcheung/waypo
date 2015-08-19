package zxb.zweibo;

import android.app.Application;

import zxb.zweibo.Utils.EmotionUtil;
import zxb.zweibo.common.ImageUtil;

/**
 * Created by rex on 15-8-13.
 */
public class GlobalApp extends Application {

    ImageUtil mImageUtil;

    static GlobalApp app;

    public GlobalApp(){
        this.app = this;
    }

    public static GlobalApp getInstance(){
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mImageUtil = new ImageUtil(this);

        EmotionUtil emotionUtil = new EmotionUtil(this);
        emotionUtil.insertEmotions();
        emotionUtil.closeDB();
        emotionUtil=null;
    }

    public ImageUtil getmImageUtil() {
        return mImageUtil;
    }
}
