package zxb.zweibo.presenter;

import android.os.Handler;

import zxb.zweibo.ui.fragment.view.IFTLView;

/**
 * @我页面的Presenter。
 * Created by Rex.Zhang on 2016/2/22.
 */
public class AtPresenter implements IWeiboPresenter{
	private IFTLView view;
	private Handler mHandler;

	public static AtPresenter newInstance(IFTLView view){
		AtPresenter p = new AtPresenter();
		p.view = view;
		p.mHandler = new Handler();
		return p;
	}

	@Override
	public void getNextPage(long currentLast) {

	}

	@Override
	public void refresh() {

	}
}
