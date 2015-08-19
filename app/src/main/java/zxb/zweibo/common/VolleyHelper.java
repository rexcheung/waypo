package zxb.zweibo.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Network;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.zhy.base.cache.disk.DiskLruCacheHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import zxb.zweibo.bean.PicUrls;

/**
 * 简化Volley的操作.
 *
 * Created by rex on 15-8-4.
 */
public class VolleyHelper {
    /**
     * 已经start的请求队列.
     */
    private static RequestQueue mQueue = null;

    /**
     * 上来文，最好传入ApplicationContext，整个APP只持有一个实例
     * 避免频繁创建注销，导致资源消耗.
     */
    private Context mContext;

    /**
     * TAG, 一般在LOG的时候使用
     */
    private String TAG;

    public VolleyHelper(Context context){
        this.mContext = context;
        if(mContext!=null){
            this.mQueue = Volley.newRequestQueue(mContext);
        }

        TAG = getClass().getSimpleName();
    }

    /*public void loadImg(final ImageView imgView, final String picUrl) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        imageRequest(imgView, picUrl);
    }*/

    /**
     * 从网络图片并进行Volley自身的缓存.
     *
     * @param imgView 要显示在哪个ImageView上面
     * @param picUrl 图片的URL
     * @param listener 收到图片后的处理，这个应该由上一个类传入，否则拿到Bitmap后不能进行缓存
     */
    public void loadImg(String picUrl, Response.Listener<Bitmap> listener) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        ImgRequest req = new ImgRequest(picUrl, listener);
        req.setShouldCache(false);

        mQueue.add(req);
    }

    /**
     * 从网络获取图片，但不使用Volley自身的缓存，
     * 已经使用了DiskLruCache缓存，避免资源消耗.
     *
     * @param picUrl 图片的URL
     * @param listener 收到图片后的处理，这个应该由上一个类传入，否则拿到Bitmap后不能进行缓存
     */
    public void loadLargeImg(String picUrl, Response.Listener<Bitmap> listener){
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        ImgRequest req = new ImgRequest(picUrl, listener);
        req.setShouldCache(false);

        mQueue.add(req);
    }

    /**
     * Volley需要的请求类，会添加到请求队列中，
     * 为了方便使用，所以把里面的参数写死，
     * 需要的时候可以修改.
     */
    class ImgRequest extends ImageRequest {
        public ImgRequest(String url, Response.Listener<Bitmap> listener) {
            super(url, listener, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.i("VolleyError", "-----volleyError-----");
                            volleyError.printStackTrace();
                        }
                    });
        }
    }

    /*private void imageRequest(final ImageView imgView, final String picUrl) {
        final ImageRequest imageRequest = new ImageRequest(
                picUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imgView.setImageBitmap(bitmap);
                        if (bitmap.getByteCount()> 10000) {

//                                    setShouldCache(false);
                        }
//                        mQueue.
                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565,

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //...
//                        Log.e(TAG, "").
                    }
                }
        );

        mQueue.add(imageRequest);
    }*/


    public void clearCache(){
        File cacheDir = new File(mContext.getCacheDir(), "volley");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        RequestQueue queue = new RequestQueue(cache, new com.android.volley.Network(){
            @Override
            public NetworkResponse performRequest(Request<?> request) throws VolleyError {
                return null;
            }
        });
        queue.start();

        queue.add(new ClearCacheRequest(cache, null));
    }

    private byte[] transferToArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public Bitmap decodeSampledBitmapFromResource(byte[] res,
                                                  int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(res, 0, res.length, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(res, 0, res.length, options);
    }

    public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
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

    public void destory(){
        if (mQueue!=null){
            mQueue.stop();
//            mQueue.cancelAll();
        }
        mContext = null;
        mQueue = null;
    }

}
