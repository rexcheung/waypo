package zxb.zweibo.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import zxb.zweibo.Utils.GsonUtils;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.bean.JsonCache;
import zxb.zweibo.bean.StatusContent;

/**
 * Created by rex on 15-12-19.
 */
public class JsonCacheDao {

    public static final String TABLE = "jsonobject";
    public static final String USER_ID = "userid";
    public static final String WEIBO_ID = "weiboid";
    public static final String JSON = "json";

    private static Gson gson = new Gson();

    public static void insertAll(String id, List<StatusContent> jsonList) {
        if (TextUtils.isEmpty(id)) {
            return;
        }

        if (jsonList == null || jsonList.size() == 0) {
            Logger.i("没有Json数据可写入缓存");
            return;
        }

        SQLiteDatabase db = SqliteHelper.getInstance().getReadableDatabase();
        db.beginTransaction();
        for (StatusContent sc : jsonList) {
            ContentValues values = new ContentValues();
            values.put(USER_ID, id);
            values.put(WEIBO_ID, sc.getId());
            values.put(JSON, gson.toJson(sc));
            db.insert(TABLE, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        Logger.i("+++插入缓存 " + jsonList.size() + "条");

        // 插入新数据后检查是否需要删除多余缓存
        removeExcessDB(id);
    }

    /**
     * 如果缓存数量大于100条，则删除多余的条目
     *
     * @param userid UserID
     */
    private static void removeExcessDB(String userid) {
        if (TextUtils.isEmpty(userid)) {
            return;
        }

        int count = checkCount(userid);
        if (count < 100) {
            return;
        }

        Logger.i("删除前的数据 = " + count + " 条");
        //降序排列weiboid前100条
        StringBuilder descSort = new StringBuilder();
        descSort.append("SELECT " + WEIBO_ID);
        descSort.append(" FROM " + TABLE);
        descSort.append(" WHERE " + USER_ID + " = '" + userid + "' ");
        descSort.append(" ORDER BY " + WEIBO_ID + " DESC LIMIT 100");

        // 获取前100条里面ID最小的，即其中最早的一条
        StringBuilder min = new StringBuilder();
        min.append(" SELECT MIN( " + WEIBO_ID + " ) ");
        min.append(" FROM ( ");
        min.append(descSort);
        min.append(" )");

        //删除比最小ID还要小的条目
        StringBuilder delSql = new StringBuilder();
        delSql.append(" DELETE FROM " + TABLE);
        delSql.append(" WHERE " + USER_ID + " = '" + userid + "'");
        delSql.append(" AND " + WEIBO_ID + " < (" + min + ") ");

//        initDB();
        SqliteHelper.getInstance().getReadableDatabase().execSQL(delSql.toString());

        Logger.i("**删除后的数据 = " + checkCount(userid) + " 条");
//        closeDB();
    }

    /**
     * 返回指定用户的缓存记录数.
     *
     * @param userid
     * @return 记录条数
     */

    public static int checkCount(final String userid) {
        if (TextUtils.isEmpty(userid)) {
            return -1;
        }

        return Observable.just(userid)
                // 拼接SQL
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        StringBuilder sql = new StringBuilder();
                        sql.append("SELECT * ")
                                .append(" FROM " + TABLE)
                                .append(" WHERE " + USER_ID + " = '" + s + "'");
                        return sql.toString();
                    }
                })
                //查询数据
                .map(new Func1<String, Cursor>() {
                    @Override
                    public Cursor call(String s) {
                        return SqliteHelper.getInstance().getReadableDatabase().rawQuery(s, null);
                    }
                })
                //Cursor 转为int
                .map(new Func1<Cursor, Integer>() {
                    @Override
                    public Integer call(Cursor cursor) {
                        return cursor.getCount();
                    }
                })
                // 返回数据。
                .toBlocking().first();
    }

    /**
     * 返回指定用户最新一条JSON缓存的WeiboID.
     *
     * @param userId 用户ID
     * @return 没有缓存则返回-1, userId不合法则返回-2
     */
    public static long getNewest(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return -2;
        }

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT MAX(" + WEIBO_ID + ") FROM ");
        sql.append(TABLE);
        sql.append(" WHERE " + USER_ID + " = ?");

        Cursor cs = SqliteHelper.getInstance().getReadableDatabase()
                .rawQuery(sql.toString(), new String[]{userId});
        long weiboid = 0;

        if (cs.moveToNext()) {
            weiboid = cs.getLong(0);
        }
        cs.close();

        return weiboid;
    }

    /**
     * 插入比较新的JSON缓存，若没有缓存则全部插入.
     *
     * @param userId UserId
     * @param scList DataList
     */
    public static void insertNew(String userId, List<StatusContent> scList) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        long mNewest = JsonCacheDao.getNewest(userId);

        if (scList.size() == 0) {
            return;
        }
        List<Long> cacheIds = getBetweenIds(userId,
                String.valueOf(scList.get(0)), String.valueOf(scList.get(scList.size() - 1)));

        if (cacheIds == null) {
            return;
        }

        //若没有缓存则全部插入
        if (cacheIds.size() == 0) {
            JsonCacheDao.insertAll(userId, scList);

            // 把没有缓存的提取出来，然后再插入数据库
        } else {
            JsonCacheDao.insertAll(userId, getNoCache(scList, cacheIds));
        }
    }

    private static List<StatusContent> getNoCache(List<StatusContent> scList, List<Long> cacheIds) {
        List<StatusContent> tempList = new ArrayList<>();
        out:
        for (StatusContent sc : scList) {
            long id = sc.getId();

            int size = cacheIds.size();
            for (int i = 0; i < size; i++) {
                if (id == cacheIds.get(i)) {
                    continue out;
                }
            }

            tempList.add(sc);
        }
        return tempList;
    }

    public static List<StatusContent> getBetweenCache(String userId, String start, String end) {
        ArrayList<StatusContent> resultList = new ArrayList<>();

        String sql = "SELECT * " +
                "FROM " + TABLE
                + " WHERE " + USER_ID + " = ? AND " + WEIBO_ID + " BETWEEN ? AND ? ORDER BY " + WEIBO_ID + " DESC";

        Cursor cs = SqliteHelper.getInstance().getReadableDatabase()
                .rawQuery(sql, new String[]{userId, end, start});
        while (cs.moveToNext()) {
            StatusContent sc = gson.fromJson(cs.getString(2), StatusContent.class);
            if (resultList.size() != 0) {
                if (resultList.get(resultList.size() - 1).getId() == sc.getId()) {
                    continue;
                }
            }
            resultList.add(sc);
        }
        cs.close();

        return resultList;
    }

    /**
     * 查询指定用户两条微博之间的微博ID
     *
     * @param userId UserId
     * @param start  startId
     * @param end
     * @return
     */
    public static List<Long> getBetweenIds(String userId, String start, String end) {
        ArrayList<Long> weiboIds = new ArrayList<>();

        String sql = "SELECT " + WEIBO_ID
                + " FROM " + TABLE
                + " WHERE " + USER_ID + " = ? AND " + WEIBO_ID + " BETWEEN ? AND ? ORDER BY " + WEIBO_ID + " DESC";

        Cursor cs = SqliteHelper.getInstance().getReadableDatabase()
                .rawQuery(sql, new String[]{userId, end, start});
        while (cs.moveToNext()) {
            weiboIds.add(cs.getLong(0));
        }
        cs.close();
        return weiboIds;
    }

    /**
     * 需要获取缓存的IDS.
     *
     * @param idList 全部ID
     * @param index  从这个开始
     * @param num    每次多少条
     * @return 返回需要的IDS
     */
    private static List<Long> getTargetList(List<Long> idList, int index, int num) {
        List<Long> targetList = new ArrayList<>();
        for (int i = index; i < num; i++) {
            targetList.add(idList.get(i));
        }
        return targetList;
    }

    /**
     * 返回指定用户所有缓存的微博消息号
     *
     * @param userId UserId
     * @return 没有缓存则返回null
     */
    public static ArrayList<Long> getAllCacheId(String userId) {
        String sql = "SELECT " + WEIBO_ID
                + " FROM " + TABLE +
                " WHERE " + USER_ID + " = ? ORDER BY " + WEIBO_ID + " DESC";

        ArrayList<Long> weiboIds = new ArrayList<>();
        Cursor cs = SqliteHelper.getInstance().getReadableDatabase().rawQuery(sql, new String[]{userId});
        while (cs.moveToNext()) {
            weiboIds.add(cs.getLong(0));
        }
        cs.close();

        if (weiboIds.size() == 0) {
            return null;
        }
        return weiboIds;
    }

    public static StatusContent getSingleCache(String userId, long Id) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(TABLE)
                .append(" WHERE ").append(USER_ID).append(" = ? ").append(" AND ").append(WEIBO_ID).append(" = ?");

        Cursor cursor = SqliteHelper.getInstance().getReadableDatabase().rawQuery(sql.toString(), new String[]{userId, String.valueOf(Id)});
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        StatusContent sc = null;
        if (cursor.moveToNext()){
            JsonCache cache = JsonCache.newInstance(cursor);
            sc = GsonUtils.fromJson(cache.getJson(), StatusContent.class);
        }

        cursor.close();
        if (sc==null){
            return null;
        }

        return sc;
    }

    public static void insertSingle(String userId, StatusContent sc){
        ContentValues values = JsonCache.toContentValues(userId, sc);
        SqliteHelper.getInstance().getReadableDatabase().insert(TABLE, null, values);
    }

    /**
     * 检查有多少条缓存。
     *
     * @param targetList
     * @param userId
     * @return
     */
    public static int checkHow(List<Long> targetList, String userId) {
        int count = 0;

        List<Long> cacheIds =
                getBetweenIds(userId, String.valueOf(targetList.get(0)),
                        String.valueOf(targetList.get(targetList.size() - 1)));
        if (cacheIds == null) {
            return 0;
        } else if (cacheIds.size() == 0) {
            return 0;
        }

        int targetSize = targetList.size();
        for (int i = 0; i < targetSize; i++) {
            int cacheSize = cacheIds.size();

            innner:
            for (int a = 0; a < cacheSize; a++) {
                if (cacheIds.get(a).longValue() == targetList.get(i).longValue()) {
                    count++;
                    break innner;
                }
            }
        }

        return count;
    }

    /**
     * 根据登陆用户ID，返回缓存对象.
     *
     * @param id UserId
     * @return 有则返回List, 无则为Null
     */
    public static List<StatusContent> readCache(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }

        List<StatusContent> cacheList = new ArrayList<StatusContent>();
        StringBuilder sql = new StringBuilder();

        // 数据由最近开始排列
        sql.append(" SELECT * FROM ");
        sql.append(JsonCacheDao.TABLE);
        sql.append(" WHERE userid=? ");
        sql.append(" ORDER BY weiboid DESC");

        Cursor cs = SqliteHelper.getInstance().getReadableDatabase()
                .rawQuery(sql.toString(), new String[]{id});
        while (cs.moveToNext()) {
            StatusContent sc = gson.fromJson(cs.getString(cs.getColumnIndex(JSON)), StatusContent.class);
            if (cacheList.size() != 0) {
                if (cacheList.get(cacheList.size() - 1).getId() == sc.getId()) {
                    continue;
                }
            }
            cacheList.add(sc);
        }
        cs.close();

        if (cacheList.size() == 0) {
            return null;
        }

        return cacheList;
    }

    /**
     * 对比已缓存的ID，计算有多少条已经缓存.
     *
     * @param idList 微博条目的ID集合
     * @param index  从哪个下标数据开始检索
     * @param num    检索多少条数据
     * @param userId 用户ID
     * @return 根据ID查询现有多少条数据已经缓存
     */
    public static int checkHow(List<Long> idList, int index, int num, String userId) {
        int to = index + num;
        int count = 0;

        ArrayList<Long> allCacheId = getAllCacheId(userId);
        List<Long> tempId =
                getBetweenIds(userId, String.valueOf(idList.get(index)), String.valueOf(idList.get(to - 1)));
        if (allCacheId == null) {
            return 0;
        }
        if (allCacheId.size() == 0) {
            return 0;
        }

        for (int i = index; i < to; i++) {
            long targetId = idList.get(i);
            int size = allCacheId.size();
            inner:
            for (int a = 0, z = size; a < z; ) {
                int currsor = ((z - a) / 2) + a;
                long curId = allCacheId.get(i);
                if (targetId < curId) {
                    a = currsor;
                } else if (targetId > curId) {
                    z = currsor;
                } else if (targetId == curId) {
                    count++;
                    break inner;
                }
            }
        }

        return count;
    }

    /**
     * 获取指定ID在idList里面的下标.
     *
     * @param idList list
     * @param from   from
     * @return 返回下标，没有错误则返回-1
     */
    private static int getIndex(List<Long> idList, long from) {
        int size = idList.size();
        for (int i = 0; i < size; i++) {
            if (idList.get(i) == from) {
                return i;
            }
        }
        return -1;
    }

    public static int getBetweenCount(String userId, String start, String end) {
        String sql = "SELECT * " /*+ WEIBO_ID*/
                + " FROM " + TABLE
                + " WHERE " + USER_ID + " = ? AND " + WEIBO_ID + " BETWEEN ? AND ? ORDER BY " + WEIBO_ID + " DESC";

        int count;
        ArrayList<JsonCache> cacheList = new ArrayList<>();
        Cursor cs = SqliteHelper.getInstance().getReadableDatabase()
                .rawQuery(sql, new String[]{userId, end, start});
        count = cs.getCount();
        while (cs.moveToNext()) {
            cacheList.add(JsonCache.newInstance(cs));
        }
        cs.close();

        Logger.i(cacheList.size());

        /*if(weiboIds.size() == 0){
            return null;
        }*/
        return count;
    }

    /**
     * 获取指定用户的所有缓存
     *
     * @param targetList 需要获取缓存ID的列表
     * @param userId     UserID
     * @return 缓存列表
     */
    public static List<StatusContent> getCache(List<Long> targetList, String userId) {
        long start = targetList.get(0);
        long end = targetList.get(targetList.size() - 1);

        StringBuilder sqlString = new StringBuilder();
        sqlString.append("SELECT * FROM " + TABLE);
        sqlString.append("WHERE " + USER_ID + " = ? AND weibo < ? AND weibo > ? ");
        sqlString.append("ORDER BY " + WEIBO_ID + " DESC ");

        int index = 0;
        List<StatusContent> resultList = new ArrayList<>();
        Cursor cs = SqliteHelper.getInstance().getReadableDatabase()
                .rawQuery(sqlString.toString(), new String[]{userId, String.valueOf(start), String.valueOf(end)});
        while (cs.moveToNext()) {
            if (targetList.get(index++) == cs.getLong(1)) {
                StatusContent statusContent = gson.fromJson(cs.getString(2), StatusContent.class);
                resultList.add(statusContent);
            }
        }
        cs.close();

        return resultList;
    }

    /**
     * 清除某用户的JSON缓存.
     *
     * @param userId 用户ID
     * @return 零则没有数据删除，非零为删除的数据条数
     */
    public int cleanCache(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            return SqliteHelper.getInstance().getReadableDatabase()
                    .delete(TABLE, USER_ID + " = ?", new String[]{userId});
        }
        return 0;
    }
}
