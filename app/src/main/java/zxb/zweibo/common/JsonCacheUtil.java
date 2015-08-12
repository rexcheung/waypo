package zxb.zweibo.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.bean.FTimeLine;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.listener.WeiboRequestListener;


/**
 * 操作Json缓存的类.
 *
 * Created by rex on 15-8-6.
 */
public class JsonCacheUtil {

    private Oauth2AccessToken mAccessToken;
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

//    private List<StatusContent> cacheList;

    Gson gson;

    private String TAG = getClass().getSimpleName();

    public JsonCacheUtil(Context context) {
        super();
        this.mContext = context;
        gson = new Gson();
    }

    WeiboAPIUtils mWeiboAPI;
    public JsonCacheUtil(Context context, Oauth2AccessToken accessToken, StatusesAPI weiboAPI) {
//        this(context);
        this.mContext = context;
        gson = new Gson();
        this.mWeiboAPI = (WeiboAPIUtils) weiboAPI;
        this.mAccessToken = accessToken;
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

        Log.i(TAG, "+++插入缓存 " + jsonList.size() + "条");

//        closeDB();

        // 插入新数据后检查是否需要删除多余缓存
        removeExcessDB(id);
    }

    CacheListener mCacheListener;
    List<StatusContent> mStatusesList;
    /**
     * 检查数据是否已经缓存需要的JSON，若其中一条没有，则发送请求并缓存.
     *
     * @param scList Adapter操作的List
     * @param requestIds 微博条目的ID集合
     * @param listener 监听器，数据成功加载后调用
     */
    public void getCacheFrom(List<StatusContent> scList, List<Long> requestIds,
                             CacheListener listener){
        if(scList==null){
            Log.i(TAG, "StatusContent List can't null");
        }
        if (scList == null){
            return;
        }
        this.mStatusesList = scList;
        this.mCacheListener = listener;

        if (requestIds == null){
            return;
        } else if (requestIds.size() == 0){
            return;
        }

        Log.i(TAG, "该用户有缓存 "+checkCount(mAccessToken.getUid())+"条");

        int count = checkHow(requestIds, mAccessToken.getUid());

        // 如果其中一条没有缓存，则发送网络请求，然后把请求的数据显示并缓存
        if (count < requestIds.size()) {

            // 在加载更加的时候，传入的请求列表第一条为画面显示的最后一条数据
            // 所以page总是第一页就可以了， 每次max_id这个参数都不同
            mWeiboAPI.friendsTimeline(0L, requestIds.get(0), 10, 1, false, 2, false,
                    mRequestListener);
            Log.i(TAG, "发送微博请求");
        //全部都有缓存，则从缓存中获取数据
        } else {
            List<StatusContent> cacheList = null;
//            cacheList = getCache(requestIds, mAccessToken.getUid());
            cacheList = getBetweenCache(mAccessToken.getUid(),
                    String.valueOf(requestIds.get(0)), String.valueOf(requestIds.get(requestIds.size() - 1)));
            if( cacheList == null){
                return;
            }
            scList.addAll(cacheList);
            listener.OnCacheComplete();
        }

        Log.i(TAG, "该用户有缓存 "+checkCount(mAccessToken.getUid())+"条");


    }

    /**
     *
     * @param targetList
     * @param userId
     * @return
     */
    private int checkHow(List<Long> targetList, String userId) {
        int count = 0;

        List<Long> cacheIds =
                getBetweenIds(userId, String.valueOf(targetList.get(0)),
                        String.valueOf(targetList.get(targetList.size() - 1)));
        if(cacheIds==null){
            return 0;
        } else if (cacheIds.size() == 0){
            return 0;
        }

        int targetSize = targetList.size();
        for (int i = 0; i < targetSize; i++) {
            int cacheSize = cacheIds.size();

            innner:for (int a = 0; a < cacheSize; a++) {
                if( cacheIds.get(a).longValue() == targetList.get(i).longValue()){
                    count++;
                    break innner;
                }
            }
        }

        return count;
    }

    /**
     * 需要获取缓存的IDS.
     *
     * @param idList 全部ID
     * @param index  从这个开始
     * @param num 每次多少条
     * @return 返回需要的IDS
     */
    private List<Long> getTargetList(List<Long> idList, int index, int num) {
        List<Long> targetList = new ArrayList<>();
        for (int i=index; i<num; i++){
            targetList.add(idList.get(i));
        }
        return targetList;
    }

    public interface CacheListener {
        public void OnCacheComplete();
    }

    /**
     * 获取指定用户的所有缓存
     * @param targetList 需要获取缓存ID的列表
     * @param userId UserID
     * @return 缓存列表
     */
    private List<StatusContent> getCache(List<Long> targetList, String userId) {
        long start = targetList.get(0);
        long end = targetList.get(targetList.size()-1);

        StringBuilder sqlString = new StringBuilder();
        sqlString.append("SELECT * FROM jsonobject");
        sqlString.append("WHERE userid = ? AND weibo < ? AND weibo > ? ");
        sqlString.append("ORDER BY weiboid DESC ");

        int index = 0;
        List<StatusContent> resultList = new ArrayList<>();
        Cursor cs = db.rawQuery(sqlString.toString(), new String[]{userId, String.valueOf(start), String.valueOf(end)});
        while (cs.moveToNext()){
            if (targetList.get(index) == cs.getLong(1)){
                StatusContent statusContent = gson.fromJson(cs.getString(2), StatusContent.class);
                resultList.add(statusContent);
                index++;
            }
        }
        cs.close();

        return resultList;
    }



//    private FTimeLine mFTimeLine;
    WeiboRequestListener mRequestListener = new WeiboRequestListener(mContext){
        @Override
        protected void onSuccess(String response) {
            FTimeLine mFTimeLine = gson.fromJson(response, FTimeLine.class);
            List<StatusContent> tempList = mFTimeLine.getStatuses();

            mStatusesList.addAll(tempList);
            mCacheListener.OnCacheComplete();

            insertNew(mAccessToken.getUid(), tempList);
        }
    };

    /**
     * 对比已缓存的ID，计算有多少条已经缓存.
     *
     * @param idList 微博条目的ID集合
     * @param index 从哪个下标数据开始检索
     * @param num 检索多少条数据
     * @param userId 用户ID
     * @return 根据ID查询现有多少条数据已经缓存
     */
    private int checkHow(List<Long> idList, int index, int num, String userId) {
        int to = index + num;
        int count = 0;

        ArrayList<Long> allCacheId = getAllCacheId(userId);
        List<Long> tempId =
                getBetweenIds(userId, String.valueOf(idList.get(index)), String.valueOf(idList.get(to-1)));
        if (allCacheId == null) {
            return 0;
        }
        if (allCacheId.size() == 0) {
            return 0;
        }

        for (int i = index; i < to; i++) {
            long targetId = idList.get(i);
            int size = allCacheId.size();
            inner:for (int a = 0, z = size; a < z; ) {
                int currsor = ((z - a) / 2) + a;
                long curId = allCacheId.get(i);
                if (targetId < curId) {
                    a=currsor;
                } else if (targetId > curId) {
                    z=currsor;
                } else if (targetId == curId){
                    count++;
                    break inner;
                }
            }
        }

        return count;
    }
    /**
     * 获取指定ID在idList里面的下标.
     * @param idList list
     * @param from from
     * @return 返回下标，没有错误则返回-1
     */
    private int getIndex(List<Long> idList, long from) {
        int size = idList.size();
        for (int i = 0; i < size; i++) {
            if (idList.get(i) == from) {
                return i;
            }
        }
        return -1;
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
     * 查询指定用户两条微博之间的微博ID
     * @param userId UserId
     * @param start startId
     * @param end
     * @return
     */
    private List<Long> getBetweenIds(String userId, String start, String end){
        ArrayList<Long> weiboIds = new ArrayList<>();

        String sql = "SELECT weiboid " +
                "FROM jsonobject " +
                "WHERE userid=? AND weiboid BETWEEN ? AND ? ORDER BY weiboid DESC";

        Cursor cs = db.rawQuery(sql, new String[]{userId, end, start});
        while (cs.moveToNext()) {
            weiboIds.add(cs.getLong(0));
        }
        cs.close();

        /*if(weiboIds.size() == 0){
            return null;
        }*/
        return weiboIds;
    }

    private List<StatusContent> getBetweenCache(String userId, String start, String end){
        ArrayList<StatusContent> resultList = new ArrayList<>();

        String sql = "SELECT * " +
                "FROM jsonobject " +
                "WHERE userid=? AND weiboid BETWEEN ? AND ? ORDER BY weiboid DESC";

        Cursor cs = db.rawQuery(sql, new String[]{userId, end, start});
        while (cs.moveToNext()) {
            StatusContent sc = gson.fromJson(cs.getString(2), StatusContent.class);
            if (resultList.size()!=0){
                if (resultList.get(resultList.size()-1).getId() == sc.getId()){
                    continue;
                }
            }
            resultList.add(sc);
        }
        cs.close();

        return resultList;
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

        List<StatusContent> cacheList = new ArrayList<StatusContent>();
        StringBuilder sql = new StringBuilder();

        // 数据由最近开始排列
        sql.append(" SELECT * FROM ");
        sql.append(TABLE);
        sql.append(" WHERE userid=? ");
        sql.append(" ORDER BY weiboid DESC");

        Cursor cs = db.rawQuery(sql.toString(), new String[]{id});
        while (cs.moveToNext()) {
            StatusContent sc = gson.fromJson(cs.getString(COL_JSON), StatusContent.class);
            if (cacheList.size()!=0){
                if (cacheList.get(cacheList.size()-1).getId() == sc.getId()){
                    continue;
                }
            }
            cacheList.add(sc);
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
     * @param userid UserID
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
        for (int i = 0; i < size; i++) {
            if (weiboList.get(i).getId() == mNewest) {
                return i;
            }
        }

        return size;
    }

    /**
     * 插入比较新的JSON缓存，若没有缓存则全部插入.
     *
     * @param userId UserId
     * @param scList DataList
     */
    public void insertNew(String userId, List<StatusContent> scList) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        mNewest = getNewest(userId);

        List<Long> cacheIds = getBetweenIds(mAccessToken.getUid(),
                String.valueOf(scList.get(0)), String.valueOf(scList.get(scList.size() - 1)));

        if (cacheIds == null){
            return;
        }

        //若没有缓存则全部插入
        if (cacheIds.size() == 0) {
            insertAll(userId, scList);

        // 把没有缓存的提取出来，然后再插入数据库
        } else {
            insertAll(mAccessToken.getUid(), getNoCache(scList, cacheIds));
        }
    }


    /**
     * 把没有缓存的ITEM抽取出来返回.
     *
     * @param scList 需要缓存的数据
     * @param cacheIds 现有缓存的ID集合
     * @return 如果全部都已经缓存，则返回的List.size()为0
     */
    private List<StatusContent> getNoCache(List<StatusContent> scList, List<Long> cacheIds) {
        List<StatusContent> tempList = new ArrayList<>();
        out:for (StatusContent sc : scList) {
            long id = sc.getId();

            int size = cacheIds.size();
            for (int i=0; i<size; i++){
                if(id == cacheIds.get(i)){
                    continue out;
                }
            }

            tempList.add(sc);
        }
        return tempList;
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

        initDB();

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
            /*String sql = " create table jsonobject(id varchar(128) not null, " +
                    " time int(128) not null ," +
                    " json varchar(5120) not null);";*/
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