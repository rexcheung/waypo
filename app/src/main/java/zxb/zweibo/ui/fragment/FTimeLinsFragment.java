package zxb.zweibo.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.R;
import zxb.zweibo.adapter.FTimeLinsAdapter;
import zxb.zweibo.bean.FTLIds;
import zxb.zweibo.bean.FTimeLine;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.Constants;
import zxb.zweibo.common.JsonCacheUtil;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.listener.OnBottomListener;


/**
 * 显示FriendsTimeLine最新关注用户的微博
 * Created by rex on 15-7-31.
 */
public class FTimeLinsFragment extends Fragment {

    /**
     * 初始化时传入的父类Activity, LayoutInflater需要使用
     */
    private static Activity mContext;
    private static int mPosition;

    /**
     * 新浪SDK.
     */
    private Oauth2AccessToken mAccessToken;
//    private StatusesAPI mStatusesAPI;
    private WeiboAPIUtils mWeiboAPI;
    /**
     * 接收最近10条微博的实体类.
     */
    private FTimeLine mFTimeLine;

    /**
     * 实体类中的10条微博.
     */
    private List<StatusContent> mStatusesList;

    private String TAG;

    private LinearLayoutManager llm;
    private RecyclerView mRecyclerView;

    private int currentPage;

    private FTimeLinsAdapter mAdapter;

    /**
     * 是否在初始化.
     */
    private boolean isInit = true;

    /**
     * 判断是否正在刷新列表.
     */
    private boolean isRefresing = false;

    private List<Long> mIds;

    private JsonCacheUtil mJsonUtil;
//    private boolean noMore = false;
    /**
     * 初始化.
     * @param position 位置.
     * @param content  Content.
     * @return 该类的实例
     */
    public static FTimeLinsFragment newInstance(int position, Activity content) {
        FTimeLinsFragment fragment = new FTimeLinsFragment();
        mPosition = position;
        mContext = content;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timeline, null);

        init(view);

        return view;
    }

    private void init(View view) {
        llm = new LinearLayoutManager(mContext);

        TAG = getClass().getSimpleName();

        mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
//        mStatusesAPI = new StatusesAPI(mContext, Constants.APP_KEY, mAccessToken);
        mWeiboAPI = new WeiboAPIUtils(mContext, Constants.APP_KEY, mAccessToken);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvContent);
        mRecyclerView.setLayoutManager(llm);

        mJsonUtil = new JsonCacheUtil(mContext, mAccessToken, mWeiboAPI);

        mStatusesList = new ArrayList<>();
        mIds = new ArrayList<>();

        if(isInit){
//            sendRequest();
            requestIds();
        }

//        readJsonCache();
    }

    /*private void readJsonCache(){
        JsonCacheUtil jsonCacheUtil = new JsonCacheUtil(mContext);
        mStatusesList = (ArrayList<StatusContent>) jsonCacheUtil.readCache(mAccessToken.getUid());
        initDatas();
    }*/


    private void initDatas() {
        mStatusesList = mFTimeLine.getStatuses();

        initEvents();

        // 若刷新后有部分数据已经有缓存，则从缓存中合并数据后，刷新列表
        if(mJsonUtil.combineCache(mAccessToken.getUid(), mStatusesList, true)){
            mAdapter.notifyDataSetChanged();
//            noMore = true;
        }
    }

    private void initEvents(){
        mAdapter = FTimeLinsAdapter.newInstance(mContext, mStatusesList);

        mRecyclerView.setAdapter(mAdapter);

        /*mRecyclerView.setOnScrollListener(new OnBottomListener(llm) {
            @Override
            public void onBottom() {
                if (!noMore) {
                    if (!isRefresing) {
                        sendRequest();
                        Log.i(TAG, "Now refreshing...");
                    }
                }

                Log.i(TAG, "Bttom");
            }
        });*/

        mRecyclerView.setOnScrollListener(new OnBottomListener(llm) {
            @Override
            public void onBottom() {
                    if (!isRefresing) {
                        loadMore();
                        Log.i(TAG, "Now refreshing...");
                    }

                Log.i(TAG, "Bttom");
            }
        });
    }

    /*private void initWithCache() {
        mJsonUtil.combineCache(mAccessToken.getUid(), mStatusesList, false);
//        noMore = true;
        initEvents();
    }*/

    private void initWidtJsonUtil(){
        initEvents();
    }

    /*private void sendRequest(){
//        mStatusesAPI.friendsTimeline(0L, 0L, 10, ++currentPage, false, 0, false, friendTimeLineListener);
        mWeiboAPI.friendsTimeline(0L, 0L, 10, ++currentPage, false, 0, false, friendTimeLineListener);
        isRefresing = true;
    }*/

    /*private RequestListener friendTimeLineListener = new RequestListener() {

        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                refreshDatas(response);
            }
        }
        @Override
        public void onWeiboException(WeiboException e) {
            isRefresing = false;
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(mContext, info != null ? info.toString() : "", Toast.LENGTH_LONG).show();

            // 请求失败后则把缓存数据取出显示，并禁用底部加载
            initWithCache();

        }

        *//*private void sqliteDemo(String response) {
            SQLiteOpenHelper sqliteHelper = new DBHelper(mContext, "test", null, 1);
            SQLiteDatabase db = sqliteHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("json", response);
            db.insert("jsonobject",null,values);
        }*//*

        *//*class DBHelper extends SQLiteOpenHelper {

            public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
                super(context, name, factory, version);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                String sql = "create table jsonobject(json varchar(65535) not null);";
                db.execSQL(sql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        }*//*
    };*/

    private int idsPage;

    /**
     * 获取最新的N条微博ID
     */
    private void requestIds(){
        long lastIds = 0;
        if (mIds.size()!=0) {
            lastIds = mIds.get(mIds.size() - 1);
        }
        mWeiboAPI.friendsTimeLineIds(0L, lastIds > 0 ? lastIds - 1 : 0, 100, ++idsPage, false, 0, idsListener);
    }


//    private FTLIds mIds = new FTLIds();
    private RequestListener idsListener = new RequestListener() {
        @Override
        public void onComplete(String json) {
            FTLIds tempIds = mGson.fromJson(json, FTLIds.class);
            if(tempIds!=null){
                mIds.addAll(tempIds.getStatuses());
            }

            loadMore();
//            String j = json;
//            Log.i(TAG, json);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Log.i(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(mContext, info != null ? info.toString() : "", Toast.LENGTH_LONG).show();
            mStatusesList = mJsonUtil.readCache(mAccessToken.getUid());
            if (isInit) {
                isInit = false;
                initEvents();
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    Gson mGson = new Gson();
    /**
     * 如果是第一次初始化，则运行initDatas()
     * 否则刷新RecycleView
     * @param response Json
     */
    /*private void refreshDatas(String response) {
        mFTimeLine = mGson.fromJson(response, FTimeLine.class);

//        JsonCacheUtil jsonCacheUtil = new JsonCacheUtil(mContext);
//        mStatusesList = jsonCacheUtil.readCache(mAccessToken.getUid());
        if (isInit) {
            initDatas();
            isInit = false;
            isRefresing = false;
//            jsonCacheUtil.insertAll(mAccessToken.getUid(), mStatusesList);
            return;
        }

        for (StatusContent sc : mFTimeLine.getStatuses()){
            mStatusesList.add(sc);
        }

        if(mJsonUtil.combineCache(mAccessToken.getUid(), mStatusesList, true)){
            // TODO 读取缓存后长度为21条，正常应该是可以继续之前的网络JSON的，但因为之前的noMore机制设置成了true，不发送更新
            noMore = true;
        }
        mAdapter.notifyDataSetChanged();


//        jsonCacheUtil.insertAll(mAccessToken.getUid(), mStatusesList);
        isRefresing = false;
        Log.i(TAG, "Refresh finish");
    }*/

    private int PAGE_SIZE = 10;

    /**
     * 从缓存中获取已有的微博，由缓存判断是否需要联网获取数据
     */
    private void loadMore(){
        isRefresing = true;
        long lastId = 0;
        if (mStatusesList.size() > 0){
            lastId = mStatusesList.get(mStatusesList.size()-1).getId();
        } else {
            lastId = mIds.get(0);
        }
//        mJsonUtil.getCacheFrom(mStatusesList, mIds, lastId, PAGE_SIZE, mCacheListener);
        List<Long> tempList = getPageItems(mIds, lastId, PAGE_SIZE);
        if (tempList!=null){
            mJsonUtil.getCacheFrom(mStatusesList, tempList, mCacheListener);
        }
    }

    JsonCacheUtil.CacheListener mCacheListener = new JsonCacheUtil.CacheListener(){
        @Override
        public void OnCacheComplete() {
            if(isInit){
                initWidtJsonUtil();
                isInit = false;
            }
            isRefresing = false;
            mAdapter.notifyDataSetChanged();
        }
    } ;


    /**
     * 获取需要ITEM的ID列表
     * @param idList IDS总表
     * @param fromId 从哪个ID开始
     * @param page_size 加载多少个ID
     * @return
     */
    private List<Long> getPageItems(List<Long> idList, long fromId, int page_size) {
        int index = getIndex(idList, fromId);
        if (index == idList.size()-1 || index < 0) {
            Log.i(TAG, "已经没有更多数据了");
            return null;
        }

        // 需要从画面上最后一条数据之后的一条开始查询，否则会出现重复
        return getTargetList(idList, index == 0 ? 0 : index+1, page_size);
    }

    /**
     * 需要获取缓存的IDS
     * @param idList
     * @param index
     * @param num
     * @return
     */
    private List<Long> getTargetList(List<Long> idList, int index, int num) {
        List<Long> targetList = new ArrayList<>();
        int target = index+num;
        if(target > idList.size()){
            target = idList.size();
        }
        for (int i=index; i<target; i++){
            if (i == 50){
                Log.i("","");
            }
            targetList.add(idList.get(i));
        }
        return targetList;
    }

    /**
     * 获取指定ID在idList里面的下标.
     * @param idList list
     * @param fromId from
     * @return 返回下标，没有，错误或最后一条则返回-1
     */
    private int getIndex(List<Long> idList, long fromId) {
        int size = idList.size();
        if(fromId == idList.get(0).longValue()){
            return 0;
        } else if(fromId == idList.get(idList.size()-1).longValue()){
            return -1;
        }

        for (int i = 0; i < size; i++) {
            if (idList.get(i) == fromId) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public void onResume() {
        super.onResume();
        mJsonUtil.initDB();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.cleanCache();
        }

        if(mJsonUtil != null){
            mJsonUtil.closeDB();
        }
        Log.i(getClass().getSimpleName(), "onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
        mWeiboAPI = null;
        mJsonUtil = null;
        mGson = null;
    }
}
