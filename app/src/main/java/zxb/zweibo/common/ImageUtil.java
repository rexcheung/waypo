package zxb.zweibo.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.zhy.base.cache.disk.DiskLruCacheHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import zxb.zweibo.bean.PicUrls;

/**
 * 首先检查磁盘缓存有无图片，有则读取缓存显示到ImageView，
 * 否则使用Volley下载图片
 * Created by rex on 15-8-6.
 */
public class ImageUtil {
    private Context mContext = null;
    private DiskLruCacheHelper diskHelper;
    private VolleyHelper volleyHelper;

    private LruCache<String, Bitmap> mMemoryCache;

    private String TAG = "ImageUtil";

    public ImageUtil(Context context){
        this.mContext = context;
        try {
            diskHelper = new DiskLruCacheHelper(mContext);
        } catch (IOException e) {
            e.printStackTrace();
        }

        volleyHelper = new VolleyHelper(mContext);

        initMemoryCache();
    }

    private void initMemoryCache() {

            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            Log.i(TAG, "Maxmemory = " + maxMemory + " KB");
            // 使用最大可用内存值的1/8作为缓存的大小。
            int cacheSize = maxMemory / 10;
            Log.i(TAG, "CacheSize = "+cacheSize+" KB");
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                    return bitmap.getByteCount() / 1024;
                }
            };
    }

    public void showImage(final ImageView imageView, final String url){
        if(mMemoryCache == null){
            initMemoryCache();
        }

        if(url.isEmpty() || url == null || imageView == null){
            return;
        }

        Bitmap bitmap = checkCache(url);
        if (bitmap == null) {
//            mImgListener.key = url;
//            mImgListener.imgView = imageView;
            /*new ImgListener(url, imageView)*/
            volleyHelper.loadImg(imageView, url, new ImgListener(url, imageView));
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void showLargeImage(final ImageView imageView, final String url){

        if(mMemoryCache == null){
            initMemoryCache();
        }

        if(url.isEmpty() || url == null || imageView == null){
            return;
        }

        Bitmap bitmap = checkCache(url);
        if (bitmap == null) {
//            mImgListener.key = url;
//            mImgListener.imgView = imageView;
            /*new ImgListener(url, imageView)*/
            volleyHelper.loadLargeImg(imageView, url, new ImgListener(url, imageView));
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    /*public void showImages(List<ImageView> imgList, List<String> urlList){
        if(imgList == null || urlList == null){
            return;
        }
        if(imgList.size() == 0 || urlList.size() == 0){
            return;
        }

        int size = urlList.size();
        for(int i=0; i<size; i++){
            showImage(imgList.get(i), urlList.get(i));
        }
    }*/

    public void showImages(List<ImageView> imgList, PicUrls[] picUrls){
        for (int i=0; i<picUrls.length; i++){
            showImage(imgList.get(i), picUrls[i].getThumbnail_pic());
        }
    }

    public Bitmap getBitmap(String key){
        if(key.isEmpty() || key == null){
            return null;
        }

        Bitmap bitmap = mMemoryCache.get(key);
        if(bitmap != null){
            return bitmap;
        }else {
            bitmap = diskHelper.getAsBitmap(key);
        }

        return null;
    }

    public void removeFromMemory(String url){
        Bitmap bitmap = mMemoryCache.get(url);
        if (bitmap != null){
            bitmap.recycle();
            mMemoryCache.remove(url);
        }
    }

    public void clearVolleyCache(){
        volleyHelper.clearCache();
    }

    public void destory(){
//        volleyHelper.clearCache();
        volleyHelper.destory();
        volleyHelper = null;
        try {
            diskHelper.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        diskHelper = null;
        mContext = null;
    }

    /*@Override
    public void OnCacheComplete(String key, Bitmap bitmap) {
        Bitmap cachePic = diskHelper.getAsBitmap(key);
        if(cachePic == null){
            mMemoryCache.put(key, bitmap);
            diskHelper.put(key, bitmap);
        }
    }*/

    /**
     * 先检查内存有无图片，有则返回
     * 无则读取磁盘缓存.
     *
     * @param key 一般传url进来，后面会再通过MD5加密
     * @return 内存或磁盘缓存有则返回Bitmap对象，无则返回null
     */
    public Bitmap checkCache(String key){
        Bitmap bitmap = null;
        bitmap = mMemoryCache.get(key);

        // 如果内存的bitmap有效则返回
        if(bitmap != null){
            if(!bitmap.isRecycled()){
                return bitmap;
            }
        }

        //无效则尝试从磁盘缓存获取并放进内存
        return syncCache(key);
    }

    /**
     * 从磁盘缓存获取图片，并把图片放到内存缓存里.
     *
     * @param key url
     * @return 如果有则返回
     */
    public Bitmap syncCache(String key){
        Bitmap bitmap = diskHelper.getAsBitmap(key);
        if (bitmap != null){
            mMemoryCache.put(key, bitmap);
        }
        return bitmap;
    }

    /**
     * 把图片加入到LruCache和DiskLruCache
     * @param key url
     * @param bitmap bitmap
     */
    private void addCache(String key, Bitmap bitmap){
        if (TextUtils.isEmpty(key) || bitmap == null){
            return;
        }

        if (mMemoryCache != null) mMemoryCache.put(key, bitmap);
        if (diskHelper != null) diskHelper.put(key, bitmap);
    }

    ImgListener mImgListener = new ImgListener();
    /**
     * ImageRequest会把请求到的bitmap传到监听器，
     * 需要把这个bitmap拿到才能添加到缓存，所以此监听器需要在这里实现.
     */
    class ImgListener implements Response.Listener<Bitmap> {
        ImageView imgView;
        String key;

        public ImgListener(){};

        public ImgListener(String key, ImageView imageView){
            this.imgView = imageView;
            this.key = key;
        }
        @Override
        public void onResponse(Bitmap bitmap) {
            imgView.setImageBitmap(bitmap);
            addCache(key, bitmap);
            imgView = null;
            key = null;
        }
    }

    public void clearMemoryCache(){
        mMemoryCache.evictAll();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
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

    private class MyImageCache implements ImageLoader.ImageCache {
        @Override
        public Bitmap getBitmap(String url) {
            return null;
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mMemoryCache.put(url, bitmap);
            diskHelper.put(url, bitmap);
        }
    };
}
