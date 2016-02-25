package zxb.zweibo.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.adapter.GifAdapter;
import zxb.zweibo.bean.FavoriteItem;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.presenter.IRxWeiboPresenter;
import zxb.zweibo.presenter.RxAtPresenter;
import zxb.zweibo.service.CheckUpdateIntentService;

/**
 * Created by Rex.Zhang on 2016/2/22.
 */
public class AtFragment extends SwipeListFragment {

	private GifAdapter mAdapter;
	private IRxWeiboPresenter mPresenter;


	/**
	 * 是否在初始化.
	 */
	private boolean isInit = true;

	public static AtFragment newInstance(Context context){
		AtFragment f = new AtFragment();
		f.mContext = context;
		return f;
	}

	@Override
	protected LinearLayoutManager initLayoutManager() {
		return new LinearLayoutManager(mContext);
	}

	@Override
	protected void onSwipeRefresh() {
		stopNotifyService();
	}

	@Override
	protected void onBottomAction() {
		if (!mSwipeLayout.isRefreshing()) {
			mSwipeLayout.setRefreshing(true);
//			mPresenter.getNextPage(mAdapter.getLastId());
			Logger.i("Now refreshing...");
		}
	}

	@Override
	protected void initEvent() {
		this.mPresenter = RxAtPresenter.newInstance();

		if (isInit) {
			mAdapter = GifAdapter.newInstance(mContext, new ArrayList<FavoriteItem>());
			mRecyclerView.setAdapter(mAdapter);
			mPresenter.getNextPage(0L)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Subscriber<List<StatusContent>>() {
						@Override
						public void onCompleted() {

						}

						@Override
						public void onError(Throwable e) {
							e.printStackTrace();
						}

						@Override
						public void onNext(List<StatusContent> statusContents) {
							mAdapter.setDatas(statusContents);
						}
					});
		}
	}

	private void stopNotifyService(){
		Intent notifyIntent = new Intent(mContext, CheckUpdateIntentService.class);
		notifyIntent.putExtra(CheckUpdateIntentService.STOP_SERVICE, true);
		mContext.startService(notifyIntent);
	}


}
