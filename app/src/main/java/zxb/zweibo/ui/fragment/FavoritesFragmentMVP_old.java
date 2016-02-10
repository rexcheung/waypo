package zxb.zweibo.ui.fragment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.Utils.Snack;
import zxb.zweibo.adapter.GifAdapter;
import zxb.zweibo.bean.FavoriteItem;
import zxb.zweibo.presenter.FaovritesPresenter;
import zxb.zweibo.ui.fragment.view.IFavorites;

/**
 * 收藏页面.
 *
 * Created by rex on 15-8-28.
 */
public class FavoritesFragmentMVP_old extends SwipeListFragment implements IFavorites{

    FaovritesPresenter mPresenter;
    GifAdapter mAdapter;
    Context mContext;

    List<FavoriteItem> mDataList;

    private boolean firstInit;

    public static FavoritesFragmentMVP_old newInstance(Context context){
        FavoritesFragmentMVP_old f = new FavoritesFragmentMVP_old();
        f.mContext = context;
        f.mDataList = new ArrayList<>();
        f.firstInit = true;
        f.mPresenter = new FaovritesPresenter(f, context);
        return f;
    }

    /*@Override
    protected void initData() {
        mPresenter.requestFavList();
    }*/

    //-----------------------Interface method----------------------------------------
    @Override
    public void response(List<FavoriteItem> favList) {
        if (favList == null){
            Snack.show(mRecyclerView, "请求失败");
            return;
        }

        //初始化
        if (mDataList.size() == 0) {
            mDataList.addAll(favList);
            mAdapter = GifAdapter.newInstance(mContext, mDataList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mDataList.addAll(favList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected LinearLayoutManager initLayoutManager() {
        return new LinearLayoutManager(mContext);
    }

    @Override
    protected void onSwipeRefresh() {
        if (!mSwipeLayout.isRefreshing()){

        }
    }

    @Override
    protected void onBottomAction() {

    }

    @Override
    protected void initEvent() {

    }
}
