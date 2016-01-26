package zxb.zweibo.bean;

import android.database.Cursor;

import zxb.zweibo.db.JsonCacheDao;

/**
 * Created by rex on 15-8-6.
 */
public class JsonCache {

    private String id;
    private double createTime;
    private String json;

    public JsonCache() {
    }

    public JsonCache(String id, double createTime, String json) {
        this.id = id;
        this.createTime = createTime;
        this.json = json;
    }

    public static JsonCache newInstance(Cursor cs){
        JsonCache cache = new JsonCache();

        int idIndex = cs.getColumnIndex(JsonCacheDao.USER_ID);
        cache.id = cs.getString(idIndex);

        int timeIndex = cs.getColumnIndex(JsonCacheDao.WEIBO_ID);
        cache.createTime = cs.getDouble(timeIndex);

        int jsonIndex = cs.getColumnIndex(JsonCacheDao.JSON);
        cache.json = cs.getString(jsonIndex);

        return cache;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getCreateTime() {
        return createTime;
    }

    public void setCreateTime(double createTime) {
        this.createTime = createTime;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
