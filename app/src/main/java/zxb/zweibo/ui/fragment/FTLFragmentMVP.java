package zxb.zweibo.ui.fragment;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.GlobalApp;
import zxb.zweibo.Utils.GsonUtils;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.adapter.FTimeLinsAdapter;
import zxb.zweibo.bean.FTLIds;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.biz.IFTLBiz;
import zxb.zweibo.common.JsonCacheUtil;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.db.JsonCacheDao;
import zxb.zweibo.presenter.FTLPresenter;
import zxb.zweibo.service.CheckUpdateIntentService;
import zxb.zweibo.ui.fragment.view.IFTLView;


/**
 * 显示FriendsTimeLine最新关注用户的微博
 * Created by rex on 15-8-20.
 */
public class FTLFragmentMVP extends SwipeListFragment implements IFTLView {

    /**
     * 初始化时传入的父类Activity, LayoutInflater需要使用
     */
    private Activity mContext;

    /**
     * 新浪SDK.
     */
    private Oauth2AccessToken mAccessToken;
    private WeiboAPIUtils mWeiboAPI;

    /**
     * 实体类中的10条微博.
     */
    private List<StatusContent> mStatusesList;

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

    private FTLPresenter mPresenter;

    /**
     * 初始化.
     *
     * @param content Content.
     * @return 该类的实例
     */
    public static FTLFragmentMVP newInstance(Activity content) {
        FTLFragmentMVP f = new FTLFragmentMVP();
        f.mContext = content;
//        f.mPresenter = new FTLPresenter(this);
        return f;
    }

    @Override
    protected LinearLayoutManager initLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    @Override
    protected void onSwipeRefresh() {
//        Snackbar.make(mRecyclerView, "Refreshing", Snackbar.LENGTH_SHORT).show();
        refreshList();
    }

    @Override
    protected void onBottomAction() {
        Logger.i("Bttom");

        if (!isRefresing) {
            loadMore();
            Logger.i("Now refreshing...");
        }
    }

    @Override
    protected void initEvent() {
        this.mPresenter = FTLPresenter.newInstance(this);

        mAccessToken = WeiboAPIUtils.getAccessToken();
        mWeiboAPI = WeiboAPIUtils.getInstance();

        mJsonUtil = new JsonCacheUtil(mContext, mAccessToken, mWeiboAPI);

        mStatusesList = new ArrayList<>();
        mIds = new ArrayList<>();

        if (isInit) {
//            mWeiboAPI.reqNewIds(idsListener);
            mPresenter.getNextPage(0L);
            initEvents();
        }
    }

    private void initEvents() {
        GlobalApp app = GlobalApp.getInstance();
        mAdapter = FTimeLinsAdapter.newInstance(mContext, mStatusesList, app.getmImageUtil());
        mRecyclerView.setAdapter(mAdapter);

        mSwipeLayout.setOnRefreshListener(mRefreshSwipe);
    }

    public void refreshList() {
        mSwipeLayout.setRefreshing(true);
        mWeiboAPI.reqNewIds(idsListener);
        cancelNotifi();
    }

    private void cancelNotifi() {
        NotificationManager notifi =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifi.cancelAll();
    }

    private void initWidtJsonUtil() {
        initEvents();
    }

    private RequestListener idsListener = new RequestListener() {
        @Override
        public void onComplete(String json) {
            FTLIds tempIds = mGson.fromJson(json, FTLIds.class);
            FTLIds gsonTest = GsonUtils.fromJson(json, FTLIds.class);
            if (tempIds != null) {
                if (tempIds.getStatuses().size() != 0) {
                    if (mIds.size() != 0){
                        long newId = tempIds.getStatuses().get(0);
                        long oldId = mIds.get(0);
                        if (newId != oldId){
                            // 根据新旧ID列表第一位判断有无新微博。
                            replaceIds(tempIds);
                        } else {
                            noNew();
                        }
                    } else {
                        // mIds长度为0则表示是初始化，直接替换则可。
                        replaceIds(tempIds);
                    }
                } else {
                    noNew();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Logger.i(e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(mContext, info != null ? info.toString() : "", Toast.LENGTH_LONG).show();
            mStatusesList = JsonCacheDao.readCache(mAccessToken.getUid());
            if (isInit) {
                isInit = false;
                initEvents();
                mAdapter.notifyDataSetChanged();
            }
            if (mSwipeLayout.isRefreshing()) mSwipeLayout.setRefreshing(false);
        }

        private void replaceIds(FTLIds tempIds){
            mIds.clear();
            mIds.addAll(tempIds.getStatuses());
            //3877868491180738 3877868419452707
            if (mSwipeLayout.isRefreshing()) {
                mStatusesList.clear();
                mSwipeLayout.setRefreshing(false);
            }
            loadMore();
        }

        private void noNew(){
            Snackbar.make(mSwipeLayout, "暂时没更新，休息下吧骚年", Snackbar.LENGTH_SHORT).show();
            mSwipeLayout.setRefreshing(false);
        }
    };

    Gson mGson = new Gson();

    private int PAGE_SIZE = 10;

    /**
     * 从缓存中获取已有的微博，由缓存判断是否需要联网获取数据
     */
    private void loadMore() {
        isRefresing = true;
        long lastId;
        if (mStatusesList.size() > 0) {
            lastId = mStatusesList.get(mStatusesList.size() - 1).getId();
        } else {
            lastId = mIds.get(0);
        }

        List<Long> tempList = getPageItems(mIds, lastId, PAGE_SIZE);
        if (tempList != null) {
            mJsonUtil.getCacheFrom(mStatusesList, tempList, mCacheListener);
        }

        restartNotifyService();
    }

    private void restartNotifyService() {
//        stopService(new Intent(getApplicationContext(), CheckUpdateService.class) );
//        EventBus.getDefault().getStickyEvent(LastWeibo.class);
//        EventBus.getDefault().postSticky(new LastWeibo(mIds.get(0)));
        Intent notifyIntent = new Intent(mContext, CheckUpdateIntentService.class);
        notifyIntent.putExtra(CheckUpdateIntentService.LAST_ID, mIds.get(0));
        mContext.startService(notifyIntent);
    }

    JsonCacheUtil.CacheListener mCacheListener = new JsonCacheUtil.CacheListener() {
        @Override
        public void OnCacheComplete() {
            if (isInit) {
                initWidtJsonUtil();
                isInit = false;
            }
            isRefresing = false;
            mAdapter.notifyDataSetChanged();
        }
    };


    /**
     * 获取需要ITEM的ID列表
     *
     * @param idList    IDS总表
     * @param fromId    从哪个ID开始
     * @param page_size 加载多少个ID
     * @return
     */
    private List<Long> getPageItems(List<Long> idList, long fromId, int page_size) {
        int index = getIndex(idList, fromId);
        if (index == idList.size() - 1 || index < 0) {
            Logger.i("已经没有更多数据了");
            return null;
        }

        // 需要从画面上最后一条数据之后的一条开始查询，否则会出现重复
        return getTargetList(idList, index == 0 ? 0 : index + 1, page_size);
    }

    /**
     * 需要获取缓存的IDS
     *
     * @param idList
     * @param index
     * @param num
     * @return
     */
    private List<Long> getTargetList(List<Long> idList, int index, int num) {
        List<Long> targetList = new ArrayList<>();
        int target = index + num;
        if (target > idList.size()) {
            target = idList.size();
        }
        for (int i = index; i < target; i++) {
            if (i == 50) {
                Logger.i("");
            }
            targetList.add(idList.get(i));
        }
        return targetList;
    }

    /**
     * 获取指定ID在idList里面的下标.
     *
     * @param idList list
     * @param fromId from
     * @return 返回下标，没有，错误或最后一条则返回-1
     */
    private int getIndex(List<Long> idList, long fromId) {
        int size = idList.size();
        if (fromId == idList.get(0).longValue()) {
            return 0;
        } else if (fromId == idList.get(idList.size() - 1).longValue()) {
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
//        mJsonUtil.initDB();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.cleanCache();
        }

        if (mJsonUtil != null) {
//            mJsonUtil.closeDB();
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
    }

    @Override
    public void onRefresh(List<StatusContent> weiboList) {

    }

    @Override
    public void onUpdate(List<StatusContent> weiboList) {
        mAdapter.update(weiboList);
    }
}
