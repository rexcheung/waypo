package zxb.zweibo;

import android.app.Application;

import zxb.zweibo.common.ImageUtil;

/**
 * Created by rex on 15-8-13.
 */
public class MyApplication extends Application {

    ImageUtil mImageUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        mImageUtil = new ImageUtil(this);
    }

    public ImageUtil getmImageUtil() {
        return mImageUtil;
    }
}
