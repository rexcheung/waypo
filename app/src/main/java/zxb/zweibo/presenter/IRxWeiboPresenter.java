package zxb.zweibo.presenter;

import java.util.List;

import rx.Observable;
import zxb.zweibo.bean.StatusContent;

/**
 * Created by Rex.Zhang on 2016/2/22.
 */
public interface IRxWeiboPresenter {
	Observable<List<StatusContent>> getNextPage(final long currentLast);
	Observable<List<StatusContent>> refresh();
}
