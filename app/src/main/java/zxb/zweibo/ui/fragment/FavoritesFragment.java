package zxb.zweibo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import zxb.zweibo.Utils.Snack;
import zxb.zweibo.adapter.FavoritesAdapter;
import zxb.zweibo.bean.FavoriteItem;
import zxb.zweibo.presenter.FaovritesPresenter;
import zxb.zweibo.ui.fragment.view.IFavorites;

/**
 * 收藏页面.
 *
 * Created by rex on 15-8-28.
 */
public class FavoritesFragment extends WeiboFragment implements IFavorites{

    FaovritesPresenter mPresenter;
    FavoritesAdapter mAdapter;

    private boolean firstInit;

    public FavoritesFragment(Context context) {
        super(context);
        firstInit = true;
        mPresenter = new FaovritesPresenter(this, context);
    }

    public static FavoritesFragment newInstance(Context context){
        FavoritesFragment f = new FavoritesFragment(context);
        return f;
    }

    @Override
    protected void loadMore() {

    }

    @Override
    protected void refreshData() {

    }

    @Override
    protected void initData() {
        mPresenter.requestFavList();
    }

    //-----------------------Interface method----------------------------------------
    @Override
    public void response(List<FavoriteItem> favList) {
        if (favList == null){
            Snack.show(mRecyclerView, "请求失败");
            return;
        }

        //初始化
        if (firstInit) {
            mAdapter = FavoritesAdapter.newInstance(mContext, favList);
            mRecyclerView.setAdapter(mAdapter);
            firstInit = false;
        } else {

        }
    }
}
