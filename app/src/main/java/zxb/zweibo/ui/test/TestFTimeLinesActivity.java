package zxb.zweibo.ui.test;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

import zxb.zweibo.R;
import zxb.zweibo.bean.FTimeLine;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.bean.testEntity;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.Constants;

/**
 * Created by rex on 15-7-30.
 */
public class TestFTimeLinesActivity extends Activity {

    private Oauth2AccessToken mAccessToken;
    StatusesAPI mStatusesAPI;

    Button startBtn;
    TextView content;

    String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_auth_activity);

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, mAccessToken);

        content = (TextView) findViewById(R.id.content);
        content.setText("获取最新微博");

        startBtn = (Button) findViewById(R.id.web);
        startBtn.setText("点击获取");
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);
            }
        });
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Gson gson = new Gson();
//                FTimeLine fTimeLine = gson.fromJson(response, FTimeLine.class);
//                testEntity fTimeLine = gson.fromJson(response, testEntity.class);
                LogUtil.i(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(TestFTimeLinesActivity.this,
                                "获取微博信息流成功, 条数: " + statuses.statusList.size(),
                                Toast.LENGTH_LONG).show();
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(TestFTimeLinesActivity.this,
                            "发送一送微博成功, id = " + status.id,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TestFTimeLinesActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(TestFTimeLinesActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };


}
