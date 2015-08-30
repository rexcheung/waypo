package zxb.zweibo;

import android.app.Application;
import android.content.Intent;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.HashMap;

import zxb.zweibo.Utils.EmotionUtil;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.Constants;
import zxb.zweibo.common.ImageUtil;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.service.CheckNewWeiboService;

/**
 * 自定义的Application.
 *
 * Created by rex on 15-8-13.
 */
public class GlobalApp extends Application {

    private ImageUtil mImageUtil;

    private Oauth2AccessToken mAccessToken;
    private WeiboAPIUtils mWeiboAPI;

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

        initEmotions();

        initFresco();

        initWeibo();
//        initService();
    }

    private void initWeibo() {
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mWeiboAPI = new WeiboAPIUtils(this, Constants.APP_KEY, mAccessToken);
    }

    public WeiboAPIUtils getWeiboUtil(){
        return mWeiboAPI;
    }

    private void initService() {
        startService(new Intent(this, CheckNewWeiboService.class));
    }

    private void initFresco() {
        Fresco.initialize(this);
    }

    private void initEmotions() {
        EmotionUtil emotionUtil = new EmotionUtil(this);
        emotionUtil.insertEmotions();
        emotionUtil.closeDB();
        emotionUtil=null;
    }

    public ImageUtil getmImageUtil() {
        return mImageUtil;
    }
}
