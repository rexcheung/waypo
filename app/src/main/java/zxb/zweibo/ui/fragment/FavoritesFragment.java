package zxb.zweibo.ui.fragment;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.Utils.Snack;
import zxb.zweibo.adapter.FavoritesAdapter;
import zxb.zweibo.bean.FavoriteItem;
import zxb.zweibo.bean.StatusContent;
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

    List<FavoriteItem> mDataList;

    private boolean firstInit;

    public FavoritesFragment(Context context) {
        super(context);
        firstInit = true;
        mPresenter = new FaovritesPresenter(this, context);
        mDataList = new ArrayList<>();
//        StatusContent tempSC = new StatusContent();
//        mDataList.get(0).status = tempSC;

    }

    public static FavoritesFragment newInstance(Context context){
        FavoritesFragment f = new FavoritesFragment(context);
        f.mDataList = new ArrayList<>();
        f.firstInit = true;
        f.mPresenter = new FaovritesPresenter(f, context);
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
//        mAdapter = FavoritesAdapter.newInstance(context, mDataList);
//        mRecyclerView.setAdapter(mAdapter);


        //初始化
        if (mDataList.size() == 0) {
            mDataList.addAll(favList);
            mAdapter = FavoritesAdapter.newInstance(mContext, mDataList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mDataList.addAll(favList);
            mAdapter.notifyDataSetChanged();
        }
    }
}
