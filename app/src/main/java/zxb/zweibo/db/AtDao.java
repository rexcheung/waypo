package zxb.zweibo.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import zxb.zweibo.bean.JsonCache;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.WeiboAPIUtils;

/**
 * Created by Rex.Zhang on 2016/2/20.
 */
public class AtDao {
	public static final String TABLE = "at";
	public static final String USER_ID = "userid";
	public static final String WEIBO_ID = "weiboid";
	public static final String JSON = "json";

	public static void insertSingle(String userId, StatusContent sc) {
		ContentValues values = JsonCache.toContentValues(userId, sc);
		SqliteHelper.getInstance().getReadableDatabase().insert(TABLE, null, values);
	}

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
				ContentValues values = JsonCache.toContentValues(WeiboAPIUtils.getUserId(), data);
				db.insert(TABLE, null, values);
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();

	}

	public static List<StatusContent> queryMulti(String userId, List<Long> ids) {
		return JsonCacheDao.queryMulti(userId, TABLE, ids);
	}
}
