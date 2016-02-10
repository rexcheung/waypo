package zxb.zweibo.presenter;

/**
 * Created by Rex.Zhang on 2016/2/10.
 */
public interface IPresenter {
    void getNextPage(final long currentLast);
    void refresh();
}
