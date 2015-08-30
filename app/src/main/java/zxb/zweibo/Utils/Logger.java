package zxb.zweibo.Utils;

import android.util.Log;

/**
 * 简化Log输出.
 *
 * Created by rex on 15-8-26.
 */
public class Logger {
    private static String TAG = "WayPo Logger";

    public static void i(String msg){
        Log.i(TAG, msg);
    }
    public static void i(Long msg){
        i(String.valueOf(msg));
    }
    public static void i(int msg){
        i(String.valueOf(msg));
    }

    public static void e(String msg){
        Log.e(TAG, msg);
    }

    public static void e(Long msg){
        e(String.valueOf(msg));
    }

    public static void e(int msg){
        e(String.valueOf(msg));
    }

    public static void d(String msg){
        Log.d(TAG, msg);
    }
}
