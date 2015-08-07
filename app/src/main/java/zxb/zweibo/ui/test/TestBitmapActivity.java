package zxb.zweibo.ui.test;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import zxb.zweibo.R;

/**
 * Created by rex on 15-8-4.
 */
public class TestBitmapActivity extends Activity {

    private Button webBtn;
    private TextView content;
    private ImageView imgTest;

    private String TAG = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
    }

    private void initViews() {
        setContentView(R.layout.test_auth_activity);

        TAG = getClass().getSimpleName();

        content = (TextView) findViewById(R.id.content);
        imgTest = (ImageView) findViewById(R.id.imgTest);
        imgTest.setImageResource(R.drawable.ship);
        imgTest.setScaleType(ImageView.ScaleType.CENTER);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ship, options);
        options.inJustDecodeBounds = false;

        Log.i(TAG, "SourceHeight = " + options.outHeight+", SourceWidth = "+ options.outWidth);

        Bitmap bitmapTest = BitmapFactory.decodeResource(getResources(), R.drawable.ship, null);
        Log.i(TAG, "BitmapHeight = " + bitmapTest.getHeight() +", BitmapWidth = "+ bitmapTest.getWidth());

        webBtn = (Button) findViewById(R.id.web);
        webBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "ImgV height = " + imgTest.getHeight() + ", ImgV Width = " + imgTest.getWidth());
                Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), R.drawable.ship,
                        imgTest.getWidth(), imgTest.getHeight());
                imgTest.setImageBitmap(bitmap);

                Log.i(TAG, "height = " + bitmap.getHeight()+", width = "+bitmap.getWidth());
            }
        });
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int sourceHeight = options.outHeight;
        final int sourceWidth = options.outWidth;
        int inSampleSize = 1;
        if (sourceHeight > reqHeight || sourceWidth > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) sourceHeight / (float) reqHeight);
            final int widthRatio = Math.round((float) sourceWidth / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
