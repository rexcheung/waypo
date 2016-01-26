package zxb.zweibo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zxb.zweibo.R;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.presenter.AuthPresenter;
import zxb.zweibo.ui.view.IAuthView;
import zxb.zweibo.widget.AppManager;

/**
 * 测试授权
 * Created by rex on 15-7-29.
 */
public class AuthActivity extends BasicActivity implements IAuthView {

    @Bind(R.id.tvText)
    TextView content;

    AuthPresenter authPresenter;

    private Oauth2AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_auth_activity);
        ButterKnife.bind(this);
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
            this.finishActivity();
        }
    }

    @OnClick(R.id.web)
    public void login() {
        if (authPresenter == null) {
            authPresenter = new AuthPresenter(this);
        }
        authPresenter.login();
    }

    @Override
    public void showHits(String result) {
        content.setText(result);
    }

    @Override
    public void finishActivity() {
//        startActivity(new Intent(this, MainActivityNew.class));
        startActivity(new Intent(this, MainActivityF.class));
        AppManager.getAppManager().finishActivity();
    }
}
