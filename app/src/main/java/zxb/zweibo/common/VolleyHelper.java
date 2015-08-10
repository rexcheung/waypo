package zxb.zweibo.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.zhy.base.cache.disk.DiskLruCacheHelper;

import java.io.IOException;
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
    private ICacheInterface imgUtil;

    public VolleyHelper(Context context){
        this.mContext = context;
        if(mContext!=null){
            this.mQueue = Volley.newRequestQueue(mContext);
        }

        TAG = getClass().getSimpleName();
    }

    public VolleyHelper(Context context, ICacheInterface imgUtil){
        this(context);
        this.imgUtil = imgUtil;
    }

    public void loadImg(final ImageView imgView, final String picUrl) {

        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext);
        }

//        Bitmap cache = getBitmapFromMemCache(picUrl);
        /*if (cache == null) {
            imageRequest(imgView, picUrl);
        } else if (!cache.isRecycled()){
            imgView.setImageBitmap(cache);
        } else {
            imageRequest(imgView, picUrl);
        }*/

        imageRequest(imgView, picUrl);
    }


    private void imageRequest(final ImageView imgView, final String picUrl) {
        ImageRequest imageRequest = new ImageRequest(
                picUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imgView.setImageBitmap(response);

//                        setDismens(imgView, response);
//                            Log.i("VolleyHelper", "response = " + response.toString());
//                        imgUtil.write(picUrl, response);
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

    private void setDismens(ImageView imgView, Bitmap response) {
        imgView.getLayoutParams().height = response.getHeight();
        imgView.getLayoutParams().width = response.getWidth();

    }

    public void loadMultiImg(List<ImageView> imgList, PicUrls[] urlList) {
        for (int i = 0; i < urlList.length; i++) {
            imgList.get(i).setVisibility(View.VISIBLE);
            loadImg(imgList.get(i), urlList[i].getThumbnail_pic());
        }
    }
}
