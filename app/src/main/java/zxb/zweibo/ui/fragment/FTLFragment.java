package zxb.zweibo.ui.fragment;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import zxb.zweibo.GlobalApp;
import zxb.zweibo.R;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.adapter.FTimeLinsAdapter;
import zxb.zweibo.bean.FTLIds;
import zxb.zweibo.bean.FTimeLine;
import zxb.zweibo.bean.LastWeibo;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.Constants;
import zxb.zweibo.common.JsonCacheUtil;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.listener.OnBottomListener;
import zxb.zweibo.service.CheckUpdateService;


/**
 * 显示FriendsTimeLine最新关注用户的微博
 * Created by rex on 15-7-31.
 */
public class FTLFragment extends Fragment {

    /**
     * 初始化时传入的父类Activity, LayoutInflater需要使用
     */
    private static Activity mContext;

    /**
     * 新浪SDK.
     */
    private Oauth2AccessToken mAccessToken;
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

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeLayout;

    private LinearLayoutManager llm;

    @Bind(R.id.listContent)
    RecyclerView mRecyclerView;

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
    /**
     * 初始化.
     * @param content  Content.
     * @return 该类的实例
     */
    public static FTLFragment newInstance(Activity content) {
        FTLFragment fragment = new FTLFragment();
        mContext = content;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ftl_fragment, null);

        ButterKnife.bind(this, view);

        init();

        return view;
    }

    private void init() {
        llm = new LinearLayoutManager(mContext);

        TAG = getClass().getSimpleName();

        mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
//        mStatusesAPI = new StatusesAPI(mContext, Constants.APP_KEY, mAccessToken);
        mWeiboAPI = new WeiboAPIUtils(mContext, Constants.APP_KEY, mAccessToken);

//        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvContent);
        mRecyclerView.setLayoutManager(llm);

        mJsonUtil = new JsonCacheUtil(mContext, mAccessToken, mWeiboAPI);

        mStatusesList = new ArrayList<>();
        mIds = new ArrayList<>();

        if(isInit){
            requestIds();
        }
    }

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
        GlobalApp app = (GlobalApp) getActivity().getApplication();
        mAdapter = FTimeLinsAdapter.newInstance(mContext, mStatusesList, app.getmImageUtil());

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new OnBottomListener(llm) {
            @Override
            public void onBottom() {
                if (!isRefresing) {
                    loadMore();
                    Logger.i("Now refreshing...");
                }

                Logger.i("Bttom");
            }
        });

        mSwipeLayout.setOnRefreshListener(mRefreshSwipe);
    }

    SwipeRefreshLayout.OnRefreshListener mRefreshSwipe = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
//            Toast.makeText(MainActivityNew.this, "Refresh", Toast.LENGTH_SHORT).show();
            Snackbar.make(mRecyclerView, "Refreshing", Snackbar.LENGTH_SHORT).show();
//            requestIds();
            refreshList();
        }
    };

    public void refreshList(){
        mSwipeLayout.setRefreshing(true);
        requestIds();
        cancelNotifi();
    }

    private void cancelNotifi(){
        NotificationManager notifi =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifi.cancelAll();
    }

    private void initWidtJsonUtil(){
        initEvents();
    }

    private int idsPage;

    /**
     * 获取最新的N条微博ID
     */
    private void requestIds(){
        long lastIds = 0;
        if (mIds.size()!=0) {
            lastIds = mIds.get(mIds.size() - 1);
        }
        mWeiboAPI.imageFTLIds(0L, lastIds > 0 ? lastIds - 1 : 0, 100, ++idsPage, false, 0, idsListener);
    }


    private RequestListener idsListener = new RequestListener() {
        @Override
        public void onComplete(String json) {
            FTLIds tempIds = mGson.fromJson(json, FTLIds.class);
            if(tempIds!=null){
                if(tempIds.getStatuses().size() != 0){
                    mIds.clear();
                    mIds.addAll(tempIds.getStatuses());
                    //3877868491180738 3877868419452707
                    if (mSwipeLayout.isRefreshing()){
                        mStatusesList.clear();
                        mSwipeLayout.setRefreshing(false);
                    }

//                    Logger.i(mIds.get(0));
                    loadMore();

                } else {
                    Snackbar.make(mSwipeLayout, "暂时没更新，休息下吧骚年", Snackbar.LENGTH_SHORT).show();
                    mSwipeLayout.setRefreshing(false);
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Logger.i(e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(mContext, info != null ? info.toString() : "", Toast.LENGTH_LONG).show();
            mStatusesList = mJsonUtil.readCache(mAccessToken.getUid());
            if (isInit) {
                isInit = false;
                initEvents();
                mAdapter.notifyDataSetChanged();
            }
            if (mSwipeLayout.isRefreshing()) mSwipeLayout.setRefreshing(false);
        }
    };

    Gson mGson = new Gson();

    private int PAGE_SIZE = 10;

    /**
     * 从缓存中获取已有的微博，由缓存判断是否需要联网获取数据
     */
    private void loadMore(){
        isRefresing = true;
        long lastId;
        if (mStatusesList.size() > 0){
            lastId = mStatusesList.get(mStatusesList.size()-1).getId();
        } else {
            lastId = mIds.get(0);
        }

        List<Long> tempList = getPageItems(mIds, lastId, PAGE_SIZE);
        if (tempList!=null){
            mJsonUtil.getCacheFrom(mStatusesList, tempList, mCacheListener);
        }

        restartService();
    }

    private void restartService() {
//        stopService(new Intent(getApplicationContext(), CheckUpdateService.class) );
        EventBus.getDefault().getStickyEvent(LastWeibo.class);
        EventBus.getDefault().postSticky(new LastWeibo(mIds.get(0)));
        mContext.startService(new Intent(mContext, CheckUpdateService.class));
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
            Logger.i("已经没有更多数据了");
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
                Logger.i("");
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
        Logger.i("onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
        mWeiboAPI = null;
        mJsonUtil = null;
        mGson = null;

        cancelNotifi();
        mContext.stopService(new Intent(mContext, CheckUpdateService.class));
    }
}
