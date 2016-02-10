package zxb.zweibo.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.Utils.Logger;
import zxb.zweibo.adapter.GifAdapter;
import zxb.zweibo.bean.FavoriteItem;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.presenter.IPresenter;
import zxb.zweibo.service.CheckUpdateIntentService;
import zxb.zweibo.ui.fragment.view.IFTLView;

/**
 * 显示FriendsTimeLine最新关注用户的微博
 * Created by rex on 2016-2-8.
 */
public abstract class WeiboBasicFragment extends SwipeListFragment implements IFTLView {

    /**
     * 初始化时传入的父类Activity, LayoutInflater需要使用
     */
    private Activity mContext;
    private GifAdapter mAdapter;
    private IPresenter mPresenter;

    /**
     * 是否在初始化.
     */
    private boolean isInit = true;

    public WeiboBasicFragment(){}

    public WeiboBasicFragment(Activity context){
        this.mContext = context;
    }



    @Override
    protected LinearLayoutManager initLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    @Override
    protected void onSwipeRefresh() {
        stopNotifyService();
        mPresenter.refresh();
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
        this.mPresenter = initPresenter();


        if (isInit) {
            mPresenter.getNextPage(0L);
            mAdapter = GifAdapter.newInstance(mContext, new ArrayList<FavoriteItem>());
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    abstract IPresenter initPresenter();

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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.cleanCache();
        }

        Logger.i("onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter = null;
    }


    /*---------------------IFTLView 接口的实现-----------------------------------*/
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

    @Override
    public void refresh() {
        onSwipeRefresh();
        mSwipeLayout.setRefreshing(true);
        Logger.i("refresh");
    }

    @Override
    public void toTop() {
        llm.smoothScrollToPosition(mRecyclerView, null, 0);
    }
}
