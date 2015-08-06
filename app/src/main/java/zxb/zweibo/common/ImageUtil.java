package zxb.zweibo.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.zhy.base.cache.disk.DiskLruCacheHelper;

import java.io.IOException;
import java.util.List;

import zxb.zweibo.bean.PicUrls;

/**
 * 首先检查磁盘缓存有无图片，有则读取缓存显示到ImageView，
 * 否则使用Volley下载图片
 * Created by rex on 15-8-6.
 */
public class ImageUtil implements ICacheInterface{
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

        volleyHelper = new VolleyHelper(mContext, this);

        initMemoryCache();
    }

    private void initMemoryCache() {

            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            Log.i(TAG, "Maxmemory = " + maxMemory + " KB");
            // 使用最大可用内存值的1/8作为缓存的大小。
            int cacheSize = maxMemory / 20;
            Log.i(TAG, "CacheSize = "+cacheSize+" KB");
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                    return bitmap.getByteCount() / 1024;
                }
            };
    }

    public void showImage(ImageView imageView, String url){
        if(mMemoryCache == null){
            initMemoryCache();
        }

        if(url.isEmpty() || url == null || imageView == null){
            return;
        }

        Bitmap bitmap = checkCache(url);
        if (bitmap == null) {
            volleyHelper.loadImg(imageView, url);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void showImages(List<ImageView> imgList, List<String> urlList){
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
    }

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


    @Override
    public void write(String key, Bitmap bitmap) {
        Bitmap cachePic = diskHelper.getAsBitmap(key);
        if(cachePic == null){
            mMemoryCache.put(key, bitmap);
            diskHelper.put(key, bitmap);
        }
    }

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

    public void clearMemoryCache(){
        mMemoryCache.evictAll();
    }
}
