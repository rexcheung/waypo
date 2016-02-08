package zxb.zweibo.presenter;

import java.util.List;

import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.biz.FTLBiz;
import zxb.zweibo.biz.IFTLBiz;
import zxb.zweibo.ui.fragment.view.IFTLView;

/**
 * 时间线的Presenter层。
 * Created by Rex.Zhang on 2016/2/8.
 */
public class FTLPresenter {
    private IFTLBiz biz;
    private IFTLView view;

    private FTLPresenter() {
    }

    public static FTLPresenter newInstance(IFTLView ftl) {
        FTLPresenter p = new FTLPresenter();
        p.biz = FTLBiz.newInstance();
        p.view = ftl;
        return p;
    }

    public List<StatusContent> getNextPage(final long currentLast) {
        biz.requestNextPage(currentLast, new IFTLBiz.responseListener() {
            @Override
            public void onResponse(List<StatusContent> ftl) {
                view.onUpdate(ftl);
            }
        });
        return null;
    }

    public List<StatusContent> refrresh(IFTLBiz.responseListener l) {
        biz.refresh(l);
        return null;
    }


}
