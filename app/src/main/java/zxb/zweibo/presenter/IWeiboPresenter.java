package zxb.zweibo.presenter;

/**
 * Created by Rex.Zhang on 2016/2/10.
 */
public interface IWeiboPresenter {
    void getNextPage(final long currentLast);
    void refresh();
}
