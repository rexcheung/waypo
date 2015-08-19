package zxb.zweibo.ui.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zxb.zweibo.R;
import zxb.zweibo.Utils.EmotionUtil;

/**
 * 表情缓存测试.
 *
 * Created by rex on 15-8-18.
 */
public class TestEmotionActivity extends Activity{

    @Bind(R.id.imgTest) ImageView imgTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_auth_activity);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.web)
    public void click(){
        EmotionUtil emotionUtil = new EmotionUtil(this);
        byte[] emo = emotionUtil.getEmotion("[哈哈]");
        emotionUtil.closeDB();

        Bitmap bitmap = BitmapFactory.decodeByteArray(emo, 0, emo.length);
        imgTest.setImageBitmap(bitmap);
    }
}
