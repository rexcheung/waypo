package zxb.zweibo.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import java.text.SimpleDateFormat;

import de.greenrobot.event.EventBus;
import zxb.zweibo.R;
import zxb.zweibo.bean.EAuth;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.Constants;
//import zxb.zweibo.ui.MainActivity_;
import zxb.zweibo.ui.view.IAuthView;
import zxb.zweibo.widget.AppManager;

/**
 * 授权页面的Presenter类
 * Created by rex on 15-7-30.
 */
public class AuthPresenter {

    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /**
     * MainActivity实例，用于保存SharePerfences的参数
     */
//    Activity mainActivity;

    /**
     * AuthActivity实例，显示Toast的参数
     */
    Activity authActivity;

    IAuthView authView;

    public AuthPresenter(IAuthView authView){
        this.authView = authView;

//        mainActivity = AppManager.getActivity(MainActivity_.class);
        authActivity = AppManager.getAppManager().currentActivity();

        mAuthInfo = new AuthInfo(
                authActivity, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(authActivity, mAuthInfo);
    }

    public void login() {
        mSsoHandler.authorizeWeb(new AuthListener());
        //All-in-one授权，安装到手机后提示未审核，又没有弹出Web授权页面，所以暂时只能用WEB授权
//        mSsoHandler.authorize(new AuthListener());
    }


    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            //从这里获取用户输入的 电话号码信息
            String  phoneNum =  mAccessToken.getPhoneNum();
            if (mAccessToken.isSessionValid()) {
                // 显示 Token
                updateTokenView(false);
                authView.showHits(mAccessToken.toString() + "\n, Phone: " + phoneNum);
//                content.setSpanText(mAccessToken.toString() + "\n, Phone: " + phoneNum);


                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(authActivity, mAccessToken);
//                AccessTokenKeeper.writeAccessToken(mainActivity, mAccessToken);


                Toast.makeText(authActivity,
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();

                //通过EventBus发送对象
                EventBus.getDefault().post(new EAuth(EAuth.SUCCESS));

                //用AppManager退出此Activity
                authView.finishActivity();
//                AppManager.getAppManager().finishActivity(AuthActivity.this);

            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = authActivity.getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(authActivity, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(authActivity,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(authActivity,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = authActivity.getString(R.string.weibosdk_demo_token_to_string_format_1);
        authView.showHits(String.format(format, mAccessToken.getToken(), date));

        String message = String.format(format, mAccessToken.getToken(), date);
        if (hasExisted) {
            message = authActivity.getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
        }
        authView.showHits(message);
    }
}
