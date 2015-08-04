package zxb.zweibo.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import zxb.zweibo.bean.PicUrls;

/**
 * Created by rex on 15-8-4.
 */
public class VolleyHelper {
    private static RequestQueue mQueue = null;

    public static void loadImg(Context context, final ImageView imgView, String picUrl) {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(context);
        }

        ImageRequest imageRequest = new ImageRequest(
                picUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imgView.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565,

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //...
                        imgView.setVisibility(View.GONE);
                    }
                }
        );

        mQueue.add(imageRequest);
    }

    public static void loadMultiImg(Context context, List<ImageView> imgList, PicUrls[] urlList) {
        for (int i = 0; i < urlList.length; i++) {
            imgList.get(i).setVisibility(View.VISIBLE);
            loadImg(context, imgList.get(i), urlList[i].getThumbnail_pic());
        }
    }
}
