package zxb.zweibo.presenter;

import java.util.List;

import rx.Observable;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.biz.IRxAtBiz;
import zxb.zweibo.biz.RxAtBiz;

/**
 * Created by Rex.Zhang on 2016/2/22.
 */
public class RxAtPresenter implements IRxWeiboPresenter {

	IRxAtBiz biz;

	private RxAtPresenter(){}

	public static RxAtPresenter newInstance(){
		RxAtPresenter p = new RxAtPresenter();
		p.biz = RxAtBiz.newInstance();
		return p;
	}

	@Override
	public Observable<List<StatusContent>> getNextPage(long currentLast) {
		return biz.getNextPage(currentLast);
	}

	@Override
	public Observable<List<StatusContent>> refresh() {
		return null;
	}
}
