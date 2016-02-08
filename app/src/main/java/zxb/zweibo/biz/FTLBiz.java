package zxb.zweibo.biz;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.Utils.GsonUtils;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.bean.FTLIds;
import zxb.zweibo.bean.FTimeLine;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.WayPoConstants;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.db.JsonCacheDao;

/**
 * Created by Rex.Zhang on 2016/2/8.
 */
public class FTLBiz implements IFTLBiz {

    private WeiboAPIUtils mWeiboAPI;
    private List<Long> mIds;

    private FTLBiz() {
    }

    public static FTLBiz newInstance() {
        FTLBiz biz = new FTLBiz();
        biz.mWeiboAPI = WeiboAPIUtils.getInstance();
        biz.mIds = new ArrayList<>();
        return biz;
    }

    @Override
    public List<StatusContent> requestNextPage(final long lastId, final responseListener l) {
        if (mIds != null && mIds.size() == 0) {
            mWeiboAPI.reqNewIds(new reqList(new requestListener() {
                @Override
                public void onSuccess() {
                    // 判断当前最后ID的下标。
                    int start = 0;
                    for (int i = 0; i < mIds.size(); i++) {
                        if (lastId == mIds.get(i)) {
                            start = i;
                        }
                    }

                    // 把需要的条目的缓存读出来。
                    final ArrayList<StatusContent> weiboCache = new ArrayList<>();
                    StatusContent cache;
                    for (int i = start, times = 0; times < WayPoConstants.PER_PAGE_COUNT; times++, i++) {
                        cache = JsonCacheDao.getSingleCache(mWeiboAPI.getmAccessToken().getUid(), mIds.get(i));
                        if (cache != null) {
                            weiboCache.add(cache);
                        }
                    }

                    // 检查缓存
                    if (weiboCache.size() == WayPoConstants.PER_PAGE_COUNT) {
                        //全部有缓存则返回缓存结果
                        l.onResponse(weiboCache);
                    } else {
                        // 其中一条没有则通过网络查询。
                        mWeiboAPI.reqFTL(mIds.get(0), new RequestListener() {
                            @Override
                            public void onComplete(String s) {
                                FTimeLine mFTimeLine = GsonUtils.fromJson(s, FTimeLine.class);
                                List<StatusContent> tempList = mFTimeLine.getStatuses();

                                if (tempList.size() != 0) {
                                    // 先把结果返回
                                    l.onResponse(tempList);

                                    // 然后再把数据缓存。
                                    for (StatusContent temp : tempList) {
                                        boolean have = false;
                                        for (StatusContent cache : weiboCache) {
                                            if (temp.getId() == cache.getId()) {
                                                // 如果有则改变变量hava的值
                                                have = true;
                                                break;
                                            }
                                        }

                                        // have 为false时表示没有缓存，则记录数据。
                                        if (!have) {
                                            JsonCacheDao.insertSingle(mWeiboAPI.getmAccessToken().getUid(), temp);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onWeiboException(WeiboException e) {}
                        });
                    }
                }

                @Override
                public void onFail() {}
            }));
        } else {

        }
        return null;
    }

    @Override
    public List<StatusContent> refresh(responseListener l) {
        return null;
    }

    class reqList implements RequestListener {
        requestListener l;

        public reqList(requestListener l) {
            this.l = l;
        }

        @Override
        public void onComplete(String json) {
            FTLIds tempIds = GsonUtils.fromJson(json, FTLIds.class);
            if (tempIds != null) {
                if (tempIds.getStatuses().size() != 0) {
                    if (mIds.size() != 0) {
                        long newId = tempIds.getStatuses().get(0);
                        long oldId = mIds.get(0);
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
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
//            Toast.makeText(mContext, info != null ? info.toString() : "", Toast.LENGTH_LONG).show();
//            mStatusesList = JsonCacheDao.readCache(mAccessToken.getUid());
//            if (isInit) {
//                isInit = false;
//                initEvents();
//                mAdapter.notifyDataSetChanged();
//            }
//            if (mSwipeLayout.isRefreshing()) mSwipeLayout.setRefreshing(false);
        }

        private void replaceIds(FTLIds tempIds) {
            mIds.clear();
            mIds.addAll(tempIds.getStatuses());
            //3877868491180738 3877868419452707
//            if (mSwipeLayout.isRefreshing()) {
//                mStatusesList.clear();
//                mSwipeLayout.setRefreshing(false);
//            }
//            loadMore();
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
