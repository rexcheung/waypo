package zxb.zweibo.ui.test;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import java.text.SimpleDateFormat;

import zxb.zweibo.R;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.Constants;

/**
 * 测试授权
 * Created by rex on 15-7-29.
 */
public class TestAuthActivity extends Activity{

    Button webBtn;
    TextView content;

    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_auth_activity);


//        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, null);
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(TestAuthActivity.this, mAuthInfo);

        mAccessToken = new Oauth2AccessToken();

        content = (TextView) findViewById(R.id.content);

        webBtn = (Button) findViewById(R.id.web);
        webBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSsoHandler.authorizeWeb(new AuthListener());
            }
        });
    }

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
                content.setText(mAccessToken.toString()+"\n, Phone: "+phoneNum);

                // 保存 Token 到 SharedPreferences
                /*AccessTokenKeeper.writeAccessToken(TestAuthActivity.this, mAccessToken);
                Toast.makeText(TestAuthActivity.this,
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();*/
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(TestAuthActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {

        }

        @Override
        public void onCancel() {

        }
    }

    private void updateTokenView(boolean hasExisted) {
        String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                new java.util.Date(mAccessToken.getExpiresTime()));
        String format = getString(R.string.weibosdk_demo_token_to_string_format_1);
        content.setText(String.format(format, mAccessToken.getToken(), date));

        String message = String.format(format, mAccessToken.getToken(), date);
        if (hasExisted) {
            message = getString(R.string.weibosdk_demo_token_has_existed) + "\n" + message;
        }
        content.setText(message);
    }
}
