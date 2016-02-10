package zxb.zweibo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sina.weibo.sdk.call.WeiboPageUtils;

import java.util.List;

import zxb.zweibo.Utils.GsonUtils;
import zxb.zweibo.bean.JsonCache;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.WeiboAPIUtils;

/**
 * 我的收藏的Json缓存。
 * Created by Rex.Zhang on 2016/2/10.
 */
public class FavoriteCacheDao {
    public static final String TABLE = "favcache";
    public static final String USER_ID = "userid";
    public static final String WEIBO_ID = "weiboid";
    public static final String JSON = "json";

    public static void insertSingle(String userId, StatusContent sc) {
        ContentValues values = JsonCache.toContentValues(userId, sc);
        SqliteHelper.getInstance().getReadableDatabase().insert(TABLE, null, values);
    }

    public static StatusContent querySingle(String userId, long weiboId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(TABLE)
                .append(" WHERE ").append(USER_ID).append(" = ? ").append(" AND ").append(WEIBO_ID).append(" = ?");

        Cursor cursor = SqliteHelper.getInstance().getReadableDatabase().rawQuery(sql.toString(), new String[]{userId, String.valueOf(weiboId)});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        StatusContent sc = null;
        if (cursor.moveToNext()) {
            JsonCache cache = JsonCache.newInstance(cursor);
            sc = GsonUtils.fromJson(cache.getJson(), StatusContent.class);
        }

        cursor.close();
        if (sc == null) {
            return null;
        }

        return sc;
    }

    /**
     * 把没有缓存的数据写入数据库。
     * @param newDatas 新数据
     * @param cacheList 已缓存数据。
     */
    public static void inserNew(List<StatusContent> newDatas, List<StatusContent> cacheList) {
        SQLiteDatabase db = SqliteHelper.getInstance().getReadableDatabase();
        db.beginTransaction();
        // 如果没有缓存则插入
        for (StatusContent data : newDatas) {
            boolean isCache = false;
            for (StatusContent cache : cacheList) {
                if (data.getId() == cache.getId()){
                    isCache = true;
                    break;
                }
            }
            if (!isCache){
                insertSingle(WeiboAPIUtils.getUserId(), data);
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public static List<StatusContent> queryMulti(String userId, List<Long> ids) {
        return JsonCacheDao.queryMulti(userId, TABLE, ids);
    }
}
