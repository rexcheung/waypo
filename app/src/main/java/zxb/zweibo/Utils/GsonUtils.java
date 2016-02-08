package zxb.zweibo.Utils;

import com.google.gson.Gson;

/**
 * Created by Rex.Zhang on 2016/2/4.
 */
public class GsonUtils {
    private static Gson gson;

    public static Gson getGson(){
        if (gson==null){
            gson = new Gson();
        }
        return gson;
    }

    public static <T extends Object>T  fromJson(String json, Class<T> cls){
        return getGson().fromJson(json, cls);
    }

    public static String toJson(Object cls){
        return getGson().toJson(cls);
    }
}
