package zxb.zweibo.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.List;

import zxb.zweibo.GlobalApp;
import zxb.zweibo.bean.FavoriteItem;
import zxb.zweibo.bean.FavoriteJson;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.ui.fragment.view.IFavorites;

/**
 * Created by rex on 15-8-28.
 */
public class FaovritesPresenter {
    IFavorites iFavorites;
    Context mContext;
    WeiboAPIUtils mWeiboAPI;

    Gson gson;

    final int COUNT = 50;
    int page;

    private FaovritesPresenter(){

    }

    public FaovritesPresenter(IFavorites iFavorites, Context context) {
        FaovritesPresenter fp = new FaovritesPresenter();
        this.iFavorites = iFavorites;
        this.mContext = context;
        gson = new Gson();

        GlobalApp app = (GlobalApp) mContext.getApplicationContext();
//        mWeiboAPI = app.getWeiboUtil();
        mWeiboAPI = WeiboAPIUtils.getInstance();
    }

    public void requestFavList(){
        mWeiboAPI.reqFavorites(COUNT, ++page, favListen);
    }

    /**
     * 请求结果的监听器，有结果后将调用iFavorites的response().
     *
     */
    RequestListener favListen = new RequestListener() {
        @Override
        public void onComplete(String json) {
            FavoriteJson favoriteJson = gson.fromJson(json, FavoriteJson.class);
            iFavorites.response(favoriteJson.getFavorites());
        }

        @Override
        public void onWeiboException(WeiboException e) {
            iFavorites.response(null);
        }
    };

}
