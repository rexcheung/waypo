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
import zxb.zweibo.db.JsonCacheDao;
import zxb.zweibo.db.SqliteHelper;
import zxb.zweibo.listener.WeiboRequestListener;


/**
 * 操作Json缓存的类.
 * <p/>
 * Created by rex on 15-8-6.
 */
public class JsonCacheUtil {

    private Oauth2AccessToken mAccessToken;
    private Context mContext;
    private SQLiteOpenHelper mdbHelper;
    private SQLiteDatabase db;

    private long mNewest = -1;

    private int HALFHOUR = 1800000;
    private int MINUTE = 60000;
    private int UPDATE_TIME = MINUTE * 5;

//    private List<StatusContent> cacheList;

    static Gson gson;

    private static final String TAG = "JsonCacheUtil";

    public JsonCacheUtil(Context context) {
        super();
        this.mContext = context;
    }

    WeiboAPIUtils mWeiboAPI;

    public JsonCacheUtil(Context context, Oauth2AccessToken accessToken, StatusesAPI weiboAPI) {
//        this(context);
        this.mContext = context;
        gson = new Gson();
        this.mWeiboAPI = (WeiboAPIUtils) weiboAPI;
        this.mAccessToken = accessToken;
    }

    /*public void initDB() {
        Log.i(TAG, "+++初始化数据库");
        if (mdbHelper == null) {
            mdbHelper = SqliteHelper.getInstance();
        }
        db = mdbHelper.getWritableDatabase();
    }*/

    CacheListener mCacheListener;
    List<StatusContent> mStatusesList;

    /**
     * 检查数据是否已经缓存需要的JSON，若其中一条没有，则发送请求并缓存.
     *
     * @param scList     Adapter操作的List
     * @param requestIds 微博条目的ID集合
     * @param listener   监听器，数据成功加载后调用
     */
    public void getCacheFrom(List<StatusContent> scList, List<Long> requestIds,
                             CacheListener listener) {
        if (scList == null) {
            Log.i(TAG, "StatusContent List can't null");
            return;
        }
        this.mStatusesList = scList;
        this.mCacheListener = listener;

        if (requestIds == null) {
            return;
        } else if (requestIds.size() == 0) {
            return;
        }

        Log.i(TAG, "该用户有缓存 " + JsonCacheDao.checkCount(mAccessToken.getUid()) + "条");

//        int count = JsonCacheDao.checkHow(requestIds, mAccessToken.getUid());
        int count = JsonCacheDao.getBetweenCount(mAccessToken.getUid(),
                "" + requestIds.get(0),
                "" + requestIds.get(requestIds.size() - 1));


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
            cacheList = JsonCacheDao.getBetweenCache(mAccessToken.getUid(),
                    String.valueOf(requestIds.get(0)), String.valueOf(requestIds.get(requestIds.size() - 1)));
            if (cacheList == null) {
                return;
            }
            scList.addAll(cacheList);
            listener.OnCacheComplete();
        }

        Log.i(TAG, "该用户有缓存 " + JsonCacheDao.checkCount(mAccessToken.getUid()) + "条");
    }

    public interface CacheListener {
        public void OnCacheComplete();
    }

    //    private FTimeLine mFTimeLine;
    WeiboRequestListener mRequestListener = new WeiboRequestListener() {
        @Override
        protected void onSuccess(String response) {
            FTimeLine mFTimeLine = gson.fromJson(response, FTimeLine.class);
            List<StatusContent> tempList = mFTimeLine.getStatuses();

            if (tempList.size() != 0) {
                mStatusesList.addAll(tempList);
                mCacheListener.OnCacheComplete();
                JsonCacheDao.insertNew(mAccessToken.getUid(), tempList);
            }
        }
    };

    /**
     * 把新数据及缓存数据做合并.
     *
     * @param userId    UserId
     * @param weiboList 已经拿到的List
     * @param isReplace true时，如果有数与最新的缓存匹配时，会合并，
     *                  false时，直接把缓存替换，在未联网打开软件时使用
     * @return 成功合并则返回true, 全部为新数据或者没有缓存则返回false.
     */
    public boolean combineCache(String userId, List<StatusContent> weiboList, boolean isReplace) {
        if (TextUtils.isEmpty(userId)) {
            return false;
        }

        if (isReplace) {
            int newCount = getNewCount(userId, weiboList);

            // 如果全部为新数据，则不合并缓存数据
            if (newCount == weiboList.size()) {
                JsonCacheDao.insertAll(userId, weiboList);
                return false;
            } else {
                removeExcess(weiboList, newCount);
                JsonCacheDao.insertAll(userId, weiboList);
                weiboList.addAll(JsonCacheDao.readCache(userId));
                Log.i(TAG, "合拼数据成功， 现在内存有 " + weiboList.size() + " 条微博");
            }
        } else {
            weiboList.addAll(JsonCacheDao.readCache(userId));
        }

        return true;
    }

    /**
     * 从网络获取的微博有可能会与本地缓存重复，所以要删除.
     *
     * @param weiboList
     * @param num
     */
    private void removeExcess(List<StatusContent> weiboList, int num) {
        for (int i = weiboList.size() - 1; i >= num; i--) {
            weiboList.remove(i);
        }
    }

    /**
     * 返回有多少条新数据，
     *
     * @param userId    UserId
     * @param weiboList DataList
     * @return 对比现有最新的缓存ID，统计有多少条为新数据，－1为UserId错误
     */
    public int getNewCount(String userId, List<StatusContent> weiboList) {
        if (TextUtils.isEmpty(userId)) {
            return -1;
        }

        if (mNewest <= 0) {
            mNewest = JsonCacheDao.getNewest(userId);
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
     * 把没有缓存的ITEM抽取出来返回.
     *
     * @param scList   需要缓存的数据
     * @param cacheIds 现有缓存的ID集合
     * @return 如果全部都已经缓存，则返回的List.size()为0
     */
    private List<StatusContent> getNoCache(List<StatusContent> scList, List<Long> cacheIds) {
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

    public void closeDB() {
        if (db.isOpen()) {
            db.close();
        }

        mdbHelper.close();
        Log.i(TAG, "---关闭数据库");
    }
}