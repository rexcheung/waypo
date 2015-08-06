package zxb.zweibo.ui.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.base.cache.disk.DiskLruCacheHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.R;
import zxb.zweibo.common.ImageUtil;
import zxb.zweibo.common.VolleyHelper;

/**
 * Created by rex on 15-8-6.
 */
public class TestVolleyCache extends Activity{

    List<String> urls;

    Button startBtn;
    TextView content;
    ImageView imgTest;

    DiskLruCacheHelper helper;
    ImageUtil imageUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.test_auth_activity);
        urls = new ArrayList<String>();
        urls.add("http://down1.sucaitianxia.com/psd01/psd122/psds17754.jpg");
        urls.add("http://down1.sucaitianxia.com/psd02/psd217/psds52541.jpg");
        urls.add("http://down1.sucaitianxia.com/psd02/psd217/psds52523.jpg");
        urls.add("http://down1.sucaitianxia.com/psd02/psd207/psds49249.jpg");
        urls.add("http://s1.att.sucaitianxia.cn/2015/0612/51c68d6278379a11e33839b4d771ea2e.jpg");
//        urls.add("");


        content = (TextView) findViewById(R.id.content);
        imgTest = (ImageView) findViewById(R.id.imgTest);
        try {
            helper = new DiskLruCacheHelper(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUtil = new ImageUtil(this);
        imageUtil.showImage(imgTest, urls.get(0));
//        Bitmap testBitmap = helper.getAsBitmap(urls.get(0));
        /*if (testBitmap != null) {
            imgTest.setImageBitmap(testBitmap);
        }*/


        startBtn = (Button) findViewById(R.id.web);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                VolleyHelper volleyHelper = new VolleyHelper(TestVolleyCache.this);
//                volleyHelper.loadImg(TestVolleyCache.this, imgTest, urls.get(0));
//                Bitmap cachePic = volleyHelper.getBitmapFromMemCache(urls.get(0));
//                helper.put(urls.get(0), cachePic);
            }
        });

    }
}
