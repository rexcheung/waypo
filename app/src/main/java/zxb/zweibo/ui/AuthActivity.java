package zxb.zweibo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import zxb.zweibo.R;
import zxb.zweibo.presenter.AuthPresenter;
import zxb.zweibo.ui.view.IAuthView;
import zxb.zweibo.widget.AppManager;

/**
 * 测试授权
 * Created by rex on 15-7-29.
 */
public class AuthActivity extends Activity implements IAuthView {

    Button webBtn;
    TextView content;

    AuthPresenter authPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_auth_activity);

        content = (TextView) findViewById(R.id.content);

        authPresenter = new AuthPresenter(this);

        webBtn = (Button) findViewById(R.id.web);
        webBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authPresenter.login();
            }
        });

        AppManager.getAppManager().addActivity(this);
    }

    @Override
    public void showHits(String result) {
        content.setText(result);
    }

    @Override
    public void finishActivity() {
        AppManager.getAppManager().finishActivity();
    }


}
