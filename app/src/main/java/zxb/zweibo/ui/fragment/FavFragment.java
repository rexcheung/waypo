package zxb.zweibo.ui.fragment;

import android.app.Activity;

import zxb.zweibo.presenter.FTLPresenter;
import zxb.zweibo.presenter.FavoritesPresenter;
import zxb.zweibo.presenter.IPresenter;

/**
 * Created by Rex.Zhang on 2016/2/10.
 */
public class FavFragment extends WeiboBasicFragment {

    public FavFragment() {
        super();
    }

//    Context mContext;
    private FavFragment(Activity context){
        super(context);
    }

    public static FavFragment newInstance(Activity context){
        FavFragment f = new FavFragment(context);
        return f;
    }

    @Override
    IPresenter initPresenter() {
        return FavoritesPresenter.newInstance(this);
    }
}
