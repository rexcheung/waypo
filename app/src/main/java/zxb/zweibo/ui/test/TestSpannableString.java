package zxb.zweibo.ui.test;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zxb.zweibo.R;
import zxb.zweibo.Utils.SpanHelper;
import zxb.zweibo.bean.EmotionBean;
import zxb.zweibo.common.AccessTokenKeeper;

/**
 * 测试SpannableString
 * Created by rex on 15-8-14.
 */
public class TestSpannableString extends Activity{

    private Oauth2AccessToken mAccessToken;

    String string = "#美媛馆MYG.HK# http://t.cn/RL1XcWS有比基尼桥的全职小牛奶@Milk716 还有啥？？？" +
            "http://t.cn/RLMWD49卖萌耍宝的小舌头算不算？ " +
            "http://t.cn/RL1QeJT@委屈猫Luhu麻麻:我只能触摸你的肉体[ok] " +
            "[蜡烛] [耶] [弱] [ok]  [爱你] [拍手][NO]";

    @Bind(R.id.tvText) TextView tvText;
//    @Bind(R.id.web)Button web;
    @Bind(R.id.imgTest) ImageView imgTest;

//    @Bind(R.id.my_image_view) SimpleDraweeView simpleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.test_auth_activity);
        ButterKnife.bind(this);

        mAccessToken = new Oauth2AccessToken();
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
    }

    @OnClick(R.id.web)
    public void test(){
        SpanHelper spanHelper = new SpanHelper(this);
        tvText.setText(spanHelper.newSpanInstance(string));
        tvText.setMovementMethod(LinkMovementMethod.getInstance());

//        frescoUsage();
    }

    /*private void frescoUsage() {
//        Uri uri = Uri.parse("http://img.t.sinajs.cn/t4/appstyle/expression/ext/normal/5c/huanglianwx_org.gif");
        Uri uri = Uri.parse("http://ww4.sinaimg.cn/bmiddle/4165f919gw1ev2g1v3tq9j20c80c8weu.jpg");
        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.my_image_view);
        draweeView.setImageURI(uri);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
        simpleView.setController(controller);
    }*/
}
