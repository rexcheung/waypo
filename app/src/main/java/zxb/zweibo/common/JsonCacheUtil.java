package zxb.zweibo.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.bean.StatusContent;


/**
 * 操作Json缓存的类.
 *
 * Created by rex on 15-8-6.
 */
public class JsonCacheUtil {

    private Context mContext;
    private SQLiteOpenHelper mdbHelper;
    private SQLiteDatabase db;

    private long mNewest = -1;

    private int COL_USER_ID = 0;
    private int COL_WEIBO_ID = 1;
    private int COL_JSON = 2;
    private int HALFHOUR = 1800000;
    private int MINUTE = 60000;
    private int UPDATE_TIME = MINUTE * 5;
    private String TABLE= "jsonobject";

    private List<StatusContent> cacheList;

    Gson gson;

    private String TAG = getClass().getSimpleName();

    public JsonCacheUtil(Context context) {
        this.mContext = context;
//        initDB();
        gson = new Gson();
    }

    public void initDB(){
        Log.i(TAG, "+++初始化数据库");
        if (mdbHelper == null){
            mdbHelper = new DBHelper(mContext, "jsoncache", null, 1);
        }
        db = mdbHelper.getWritableDatabase();
    }

    public void insertAll(String id, List<StatusContent> jsonList) {
        if (TextUtils.isEmpty(id)) {
            return;
        }

        if (jsonList == null || jsonList.size() == 0){
            Log.i(TAG, "没有Json数据可写入缓存");
            return;
        }

//        initDB();

//        long timeMillis = System.currentTimeMillis();

        db.beginTransaction();
        for (StatusContent sc : jsonList){
            ContentValues values = new ContentValues();
            values.put("userid", id);
            values.put("weiboid", sc.getId());
            values.put("json", gson.toJson(sc));
            db.insert("jsonobject",null,values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();

//        closeDB();

        // 插入新数据后检查是否需要删除多余缓存
        removeExcessDB(id);
    }

    /**
     * 返回指定用户所有缓存的微博消息号
     * @param userId UserId
     * @return 没有缓存则返回null
     */
    private ArrayList<Long> getAllCacheId(String userId){
        String sql = "select weiboid from jsonobject where userid = ? ORDER BY weiboid DESC";

        ArrayList<Long> weiboIds = new ArrayList<>();
        Cursor cs = db.rawQuery(sql, new String[]{userId});
        while (cs.moveToNext()) {
            weiboIds.add(cs.getLong(0));
        }
        cs.close();

        if(weiboIds.size() == 0){
            return null;
        }
        return weiboIds;
    }


    /**
     * 根据登陆用户ID，返回缓存对象.
     *
     * @param id UserId
     * @return 有则返回List, 无则为Null
     */
    public List<StatusContent> readCache(String id) {
        if (TextUtils.isEmpty(id)){
            return null;
        }

//        initDB();

        cacheList = new ArrayList<StatusContent>();
        StringBuilder sql = new StringBuilder();

        // 数据由最近开始排列
        sql.append(" SELECT * FROM ");
        sql.append(TABLE);
        sql.append(" WHERE userid=? ");
        sql.append(" ORDER BY weiboid DESC");

        Cursor cs = db.rawQuery(sql.toString(), new String[]{id});
        while (cs.moveToNext()) {
            StatusContent statusContent = gson.fromJson(cs.getString(COL_JSON), StatusContent.class);
            cacheList.add(statusContent);
        }
        cs.close();

        if (cacheList.size() == 0){
            return null;
        }

//        closeDB();
        return cacheList;
    }

    /**
     * 把新数据及缓存数据做合并.
     *
     * @param userId UserId
     * @param weiboList 已经拿到的List
     * @param isReplace true时，如果有数与最新的缓存匹配时，会合并，
     *                  false时，直接把缓存替换，在未联网打开软件时使用
     * @return 成功合并则返回true, 全部为新数据或者没有缓存则返回false.
     */
    public boolean combineCache(String userId, List<StatusContent> weiboList, boolean isReplace){
        if (TextUtils.isEmpty(userId)) {
            return false;
        }

        if (isReplace) {
            int newCount = getNewCount(userId, weiboList);

            // 如果全部为新数据，则不合并缓存数据
            if (newCount == weiboList.size()) {
                insertAll(userId, weiboList);
                return false;
            } else {
                removeExcess(weiboList, newCount);
                insertAll(userId, weiboList);
                weiboList.addAll(readCache(userId));
                Log.i(TAG, "合拼数据成功， 现在内存有 " + weiboList.size() + " 条微博");
            }
        } else {
            weiboList.addAll(readCache(userId));
        }

        return true;
    }

    /**
     * 从网络获取的微博有可能会与本地缓存重复，所以要删除.
     *
     * @param weiboList
     * @param num
     */
    private void removeExcess(List<StatusContent> weiboList, int num){
        for (int i=weiboList.size()-1; i>=num; i--){
            weiboList.remove(i);
        }
    }

    /**
     * 如果缓存数量大于100条，则删除多余的条目
     * @param userid
     */
    private void removeExcessDB(String userid){
        if (TextUtils.isEmpty(userid)) {
            return;
        }


        int count = checkCount(userid);
        if (count < 100){
            return;
        }


        Log.i(TAG, "删除前的数据 = "+count+" 条");
        //降序排列weiboid前100条
        StringBuilder descSort = new StringBuilder();
        descSort.append("SELECT weiboid FROM jsonobject WHERE userid = '");
        descSort.append(userid);
        descSort.append("' ORDER BY weiboid DESC LIMIT 100");

        // 获取前100条里面ID最小的，即其中最早的一条
        StringBuilder min = new StringBuilder();
        min.append("SELECT MIN(weiboid) FROM (");
        min.append(descSort);
        min.append(" )");

        //删除比最小ID还要小的条目
        StringBuilder delSql = new StringBuilder();
        delSql.append("DELETE FROM jsonobject WHERE userid ='");
        delSql.append(userid);
        delSql.append("' AND weiboid < (");
        delSql.append(min);
        delSql.append(")");

//        initDB();
        db.execSQL(delSql.toString());

        Log.i(TAG, "**删除后的数据 = " + checkCount(userid) + " 条");
//        closeDB();
    }

    /**
     * 返回指定用户的缓存记录数.
     *
     * @param userid
     * @return 记录条数
     */
    private int checkCount(String userid) {
        if (TextUtils.isEmpty(userid)) {
            return -1;
        }

//        initDB();
        // 首先判断缓存数据有无多于100条
        String sql = "SELECT count(weiboid) FROM jsonobject WHERE userid = ?";
        Cursor cs = db.rawQuery(sql, new String[]{userid});

        int count = 0;
        if (cs.moveToNext()){
            count = cs.getInt(0);
        }

        cs.close();
//        closeDB();
        return count;
    }

    /**
     * 返回有多少条新数据，
     *
     * @param userId UserId
     * @param weiboList DataList
     * @return 对比现有最新的缓存ID，统计有多少条为新数据，－1为UserId错误
     */
    public int getNewCount(String userId, List<StatusContent> weiboList){
        if (TextUtils.isEmpty(userId)) {
            return -1;
        }

        if (mNewest <= 0){
            mNewest = getNewest(userId);
        }

        int size = weiboList.size();
        for (int i=0; i<weiboList.size(); i++){
            if (weiboList.get(i).getId() == mNewest){
                return i;
            }
        }

        return size;
    }

    /**
     * 插入比较新的JSON缓存，若没有缓存则全部插入.
     *
     * @param userId UserId
     * @param weiboList DataList
     */
    public void insertNew(String userId, List<StatusContent> weiboList) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        mNewest = getNewest(userId);

        //若没有缓存则全部插入，有则插入最新的
        if (mNewest < 0) {
            insertAll(userId, weiboList);
        } else {
//            initDB();
            db.beginTransaction();

            for (StatusContent sc : weiboList) {
                if (sc.getId() > mNewest) {
                    ContentValues values = new ContentValues();
                    values.put("id", userId);
                    values.put("time", System.currentTimeMillis());
                    values.put("json", gson.toJson(sc));
                    db.insert("jsonobject", null, values);
                } else {
                    break;
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();
//            closeDB();
        }
    }

    /**
     * 返回指定用户最新一条JSON缓存的WeiboID.
     *
     * @param userId 用户ID
     * @return 没有缓存则返回-1, userId不合法则返回-2
     */
    private long getNewest(String userId){
        if(TextUtils.isEmpty(userId)){
            return -2;
        }

//        initDB();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT MAX(weiboid) FROM ");
        sql.append(TABLE);
        sql.append(" WHERE userid = ?");
//        sql.append(userId);

        Cursor cs = db.rawQuery(sql.toString(), new String[]{userId});
        long weiboid = 0;

        if(cs.moveToNext()){
            weiboid = cs.getLong(0);
        }
        cs.close();

//        closeDB();
        return weiboid;
    }

    /**
     * 清除某用户的JSON缓存.
     *
     * @param userId 用户ID
     * @return 零则没有数据删除，非零为删除的数据条数
     */
    public int cleanCache(String userId){
        if(!TextUtils.isEmpty(userId)){
            return db.delete(TABLE, "userid = ?", new String[]{userId});
        }
        return 0;
    }

    public void closeDB(){
        if(db.isOpen()){
            db.close();
        }

        mdbHelper.close();
        Log.i(TAG, "---关闭数据库");
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = " create table jsonobject(id varchar(128) not null, " +
                    " time int(128) not null ," +
                    " json varchar(5120) not null);";
            StringBuilder sqlString = new StringBuilder();
            sqlString.append(" create table ");
            sqlString.append(TABLE);
            sqlString.append(" (userid varchar(128) not null, ");
            sqlString.append(" weiboid int(128) not null, ");
            sqlString.append(" json varchar(5120) not null); ");
            db.execSQL(sqlString.toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}