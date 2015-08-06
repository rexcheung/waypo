package zxb.zweibo.common;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by rex on 15-8-5.
 */
public class JsonCache<T> {
    private static Gson gson;

    public void  write(Class cls, List<T> dataList){
        if(gson == null){
            gson = new Gson();
        }
        String json = gson.toJson(dataList);

    }
}
