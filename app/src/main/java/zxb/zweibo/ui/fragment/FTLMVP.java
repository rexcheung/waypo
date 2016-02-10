package zxb.zweibo.ui.fragment;

import android.app.Activity;

import zxb.zweibo.presenter.FTLPresenter;
import zxb.zweibo.presenter.IPresenter;

/**
 * Created by Rex.Zhang on 2016/2/10.
 */
public class FTLMVP extends WeiboBasicFragment {

    public FTLMVP() {
        super();
    }

//    Context mContext;
    private FTLMVP(Activity context){
        super(context);
    }

    public static FTLMVP newInstance(Activity context){
        FTLMVP f = new FTLMVP(context);
        return f;
    }

    @Override
    IPresenter initPresenter() {
        return FTLPresenter.newInstance(this);
    }
}
