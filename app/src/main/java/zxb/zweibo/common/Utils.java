package zxb.zweibo.common;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by rex on 15-8-7.
 */
public class Utils {

    /**
     * 获取屏幕参数
     * @param activity
     * @return
     */
    public static DisplayMetrics getMetrics(Activity activity){
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int width = metric.widthPixels;  // 屏幕宽度（像素）
//        int height = metric.heightPixels;  // 屏幕高度（像素）
//        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        return metric;
    }
}
