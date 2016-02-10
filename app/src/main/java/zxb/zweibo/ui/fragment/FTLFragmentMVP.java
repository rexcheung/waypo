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
 * Created by rex on 2016-2-8.
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
        return f;
    }

    @Override
    protected LinearLayoutManager initLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    @Override
    protected void onSwipeRefresh() {
        stopNotifyService();
        mPresenter.refrresh();
    }

    @Override
    protected void onBottomAction() {
        Logger.i("Bttom");

        if (!mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(true);
            mPresenter.getNextPage(mAdapter.getLastId());
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
            mPresenter.getNextPage(0L);
            initEvents();
        }
    }

    private void initEvents() {
        GlobalApp app = GlobalApp.getInstance();
        mAdapter = FTimeLinsAdapter.newInstance(mContext, mStatusesList, app.getmImageUtil());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void restartNotifyService() {
        Intent notifyIntent = new Intent(mContext, CheckUpdateIntentService.class);
        notifyIntent.putExtra(CheckUpdateIntentService.LAST_ID, mAdapter.getFirstId());
        mContext.startService(notifyIntent);
    }

    private void stopNotifyService(){
        Intent notifyIntent = new Intent(mContext, CheckUpdateIntentService.class);
        notifyIntent.putExtra(CheckUpdateIntentService.STOP_SERVICE, true);
        mContext.startService(notifyIntent);
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
    }

    @Override
    public void onRefresh(List<StatusContent> weiboList) {
        mSwipeLayout.setRefreshing(false);
        mAdapter.setDatas(weiboList);
        restartNotifyService();
    }

    @Override
    public void onUpdate(List<StatusContent> weiboList) {
        mAdapter.update(weiboList);
        mSwipeLayout.setRefreshing(false);
    }
}
