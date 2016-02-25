package zxb.zweibo.presenter;

import android.os.Handler;

import java.util.List;

import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.biz.FTLBiz;
import zxb.zweibo.biz.IFTLBiz;
import zxb.zweibo.ui.fragment.view.IFTLView;

/**
 * 时间线的Presenter层。
 * Created by Rex.Zhang on 2016/2/8.
 */
public class FTLWeiboPresenter implements IWeiboPresenter {
    private IFTLBiz biz;
    private IFTLView view;
    private Handler mHandler;

    private FTLWeiboPresenter() {
    }

    public static FTLWeiboPresenter newInstance(IFTLView ftl) {
        FTLWeiboPresenter p = new FTLWeiboPresenter();
        p.biz = FTLBiz.newInstance();
        p.view = ftl;
        p.mHandler = new Handler();
        return p;
    }

    @Override
    public void getNextPage(final long currentLast) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                biz.requestNextPage(currentLast, new IFTLBiz.responseListener() {
                    @Override
                    public void onResponse(final List<StatusContent> ftl) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                view.onUpdate(ftl);
                            }
                        });

                    }
                });
            }
        }).start();

    }

    @Override
    public void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                biz.refresh(new IFTLBiz.responseListener() {
                    @Override
                    public void onResponse(final List<StatusContent> ftl) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                view.onRefresh(ftl);
                            }
                        });
                    }
                });
            }
        }).start();

    }


}
