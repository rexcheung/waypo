package zxb.zweibo.biz;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.Utils.GsonUtils;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.Utils.Toastutils;
import zxb.zweibo.bean.FavoriteIds;
import zxb.zweibo.bean.FavoriteItem;
import zxb.zweibo.bean.FavoriteJson;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.WayPoConstants;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.db.FavoriteCacheDao;

/**
 * 时间线逻辑层。
 * Created by Rex.Zhang on 2016/2/8.
 */
public class FavortiesBiz implements IFTLBiz {

    private WeiboAPIUtils mWeiboAPI;
    private List<FavoriteIds.FavoritesEntity> mIds;

    private FavortiesBiz() {
    }

    public static FavortiesBiz newInstance() {
        FavortiesBiz biz = new FavortiesBiz();
        biz.mWeiboAPI = WeiboAPIUtils.getInstance();
        biz.mIds = new ArrayList<>();
        return biz;
    }


    @Override
    public void refresh(responseListener l) {
        mIds.clear();
        requestNextPage(0, l);
    }

    @Override
    public void requestNextPage(final long lastId, final responseListener l) {
        if (mIds != null && mIds.size() == 0) {
            //初始化时走的逻辑。
            mWeiboAPI.reqFavoritesIds(new reqList(new requestListener() {
                @Override
                public void onSuccess() {
                    getFTL(lastId, l);
                }

                @Override
                public void onFail() {
                }
            }));
        } else {
            //加载更多
            getFTL(lastId, l);
        }
    }

    private void getFTL(long lastId, final responseListener l) {
        // 判断当前最后ID的下标。
        int current = getStart(lastId, mIds);
        if (current > 0) {
            current++;
        }
        // 把需要的条目的缓存读出来。
        final List<StatusContent> weiboCache = getCache(current, mIds);

        // 检查缓存
        if (weiboCache.size() == WayPoConstants.FAVORITES_PER_PAGE_COUNT) {
            //全部有缓存则返回缓存结果
            l.onResponse(weiboCache);
        } else {
            int page = 1;
            if (current == 0) {
                page = 1;
            } else {
                page = current / WayPoConstants.FAVORITES_PER_PAGE_COUNT;
            }
            // 其中一条没有则通过网络查询。
            mWeiboAPI.reqFavorites(WayPoConstants.FAVORITES_PER_PAGE_COUNT, page, new RequestListener() {
                //监听器。
                @Override
                public void onComplete(String s) {
                    FavoriteJson favJson = GsonUtils.fromJson(s, FavoriteJson.class);
                    List<StatusContent> tempList = new ArrayList<>();
                    List<FavoriteItem> favorites = favJson.getFavorites();
                    for (FavoriteItem fav : favorites) {
                        tempList.add(fav.getStatus());
                    }

                    if (tempList.size() != 0) {
                        // 先把结果返回
                        l.onResponse(tempList);

                        // 然后再把缓存JSON。
                        for (StatusContent temp : tempList) {
                            boolean have = false;
                            for (StatusContent cache : weiboCache) {
                                if (temp.getId() == cache.getId()) {
                                    // 如果有则改变变量have的值
                                    have = true;
                                    break;
                                }
                            }

                            // have 为false时表示没有缓存，则记录数据。
                            if (!have) {
                                FavoriteCacheDao.insertSingle(mWeiboAPI.getUserId(), temp);
                            }
                        }
                    }
                }

                @Override
                public void onWeiboException(WeiboException e) {
                    Toastutils.s(e.getMessage());
                }
            });
        }
    }

    private List<StatusContent> getCache(int start, List<FavoriteIds.FavoritesEntity> ids) {
        List<Long> temp = new ArrayList<>();
        int count;
        if (ids.size() < WayPoConstants.FAVORITES_PER_PAGE_COUNT) {
            count = ids.size();
        } else {
            count = WayPoConstants.FAVORITES_PER_PAGE_COUNT;
        }

        for (int x = 0; x < count; x++) {
            temp.add(Long.valueOf(ids.get(x).getStatus()));
        }
        return FavoriteCacheDao.queryMulti(mWeiboAPI.getUserId(), temp);
    }

    /**
     * 遍历ID列表，找到与lastId区的ID下标。
     *
     * @param lastId 当前页面最后的ID
     * @param ids    ID列表
     * @return 匹配的下标。
     */
    private int getStart(long lastId, List<FavoriteIds.FavoritesEntity> ids) {
        int start = 0;
        for (int i = 0; i < ids.size(); i++) {
            if (lastId == Long.valueOf(ids.get(i).getStatus())) {
                start = i;
            }
        }
        return start;
    }

    class reqList implements RequestListener {
        requestListener l;

        public reqList(requestListener l) {
            this.l = l;
        }

        @Override
        public void onComplete(String json) {
            FavoriteIds tempIds = GsonUtils.fromJson(json, FavoriteIds.class);
            if (tempIds != null) {
                if (tempIds.getFavorites().size() != 0) {
                    if (mIds.size() != 0) {
                        long newId = Long.valueOf(tempIds.getFavorites().get(0).getStatus());
                        long oldId = Long.valueOf(mIds.get(0).getStatus());
                        if (newId != oldId) {
                            // 根据新旧ID列表第一位判断有无新微博。
                            replaceIds(tempIds);
                            l.onSuccess();
                        } else {
                            noNew();
                        }
                    } else {
                        // mIds长度为0则表示是初始化，直接替换则可。
                        replaceIds(tempIds);
                        l.onSuccess();
                    }
                } else {
                    noNew();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Logger.i(e.getMessage());
            Toastutils.s(e.getMessage());
//            Toast.makeText(mContext, info != null ? info.toString() : "", Toast.LENGTH_LONG).show();
        }

        private void replaceIds(FavoriteIds tempIds) {
            mIds.clear();
            mIds.addAll(tempIds.getFavorites());
        }

        private void noNew() {
//            Snackbar.make(mSwipeLayout, "暂时没更新，休息下吧骚年", Snackbar.LENGTH_SHORT).show();
//            mSwipeLayout.setRefreshing(false);
        }
    }

    public void destory() {
        mWeiboAPI = null;
    }
}
