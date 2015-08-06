package zxb.zweibo.common;

import android.graphics.Bitmap;

/**
 * Created by rex on 15-8-6.
 */
public interface ICacheInterface {
    public void write(String key, Bitmap bitmap);
}
