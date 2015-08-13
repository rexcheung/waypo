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
 * Created by rex on 15-8-4.
 */
public class VolleyHelper {
    private static RequestQueue mQueue = null;

    private Context mContext;

    private String TAG;
//    private ICacheInterface imgUtil;

    public VolleyHelper(Context context){
        this.mContext = context;
        if(mContext!=null){
            this.mQueue = Volley.newRequestQueue(mContext);
//            DiskBasedCache()
        }

        TAG = getClass().getSimpleName();
    }

    public VolleyHelper(Context context, ICacheInterface imgUtil){
        this(context);
//        this.imgUtil = imgUtil;
    }

    public void loadImg(final ImageView imgView, final String picUrl) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        imageRequest(imgView, picUrl);
    }
    public void loadImg(final ImageView imgView, final String picUrl, Response.Listener<Bitmap> listener) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        ImgRequest req = new ImgRequest(imgView, picUrl, listener);
        req.setShouldCache(false);
//        req.
        mQueue.add(req);
    }

    public void loadLargeImg(final ImageView imgView, final String picUrl, Response.Listener<Bitmap> listener){
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }
        ImgRequest req = new ImgRequest(imgView, picUrl, listener);
        req.setShouldCache(false);

//        WeakReference<ImgRequest> imgRequestWeakReference = new WeakReference<ImgRequest>(req);
        mQueue.add(req);
    }

    static class ImgRequest extends ImageRequest{
        public ImgRequest(final ImageView imgView, String url, Response.Listener<Bitmap> listener) {
            super(url, listener, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, null);
        }
    }

    /*public void loadImg(final ImageView imgView, final String picUrl, Response.Listener listener) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

        ImageRequest imageRequest = new ImageRequest(
                picUrl,
                listener, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565,

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //...
                    }
                }
        );

        mQueue.add(imageRequest);
    }*/

    private void imageRequest(final ImageView imgView, final String picUrl) {
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
    }

    /*private void notCache(ImageRequest imageRequest){
        imageRequest.setShouldCache(false);
    }*/

    /*private void setDismens(ImageView imgView, Bitmap response) {
        imgView.getLayoutParams().height = response.getHeight();
        imgView.getLayoutParams().width = response.getWidth();
    }*/

    /*public void loadMultiImg(List<ImageView> imgList, PicUrls[] urlList) {
        for (int i = 0; i < urlList.length; i++) {
            imgList.get(i).setVisibility(View.VISIBLE);
            loadImg(imgList.get(i), urlList[i].getThumbnail_pic());
        }
    }*/

    /*public void load(ImageView img, final String url, final ImageLoader.ImageCache imageCache){

        ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(mContext), imageCache);
        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bitmap = response.getBitmap();
                if (url != null && bitmap != null)
                    imageCache.putBitmap(url, bitmap);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
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

// clear all volley caches.
        queue.add(new ClearCacheRequest(cache, null));

        /*for (File cacheFile : cacheDir.listFiles()) {
            if (cacheFile.isFile() && cacheFile.length() > 10000000) cacheFile.delete();
        }*/
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
