package zxb.zweibo.biz;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.Utils.GsonUtils;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.Utils.Toastutils;
import zxb.zweibo.bean.FTLIds;
import zxb.zweibo.bean.FTimeLine;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.WayPoConstants;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.db.JsonCacheDao;

/**
 * 时间线逻辑层。
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
    public void refresh(responseListener l) {
        mIds.clear();
        requestNextPage(0, l);
    }

    @Override
    public void requestNextPage(final long lastId, final responseListener l) {
        if (mIds != null && mIds.size() == 0) {
            //初始化时走的逻辑。
            mWeiboAPI.reqNewIds(new reqList(new requestListener() {
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
//		getCaches(current,mIds)


        // 检查缓存
        if (weiboCache.size() == WayPoConstants.PER_PAGE_COUNT) {
            //全部有缓存则返回缓存结果
            l.onResponse(weiboCache);
        } else {
            // 其中一条没有则通过网络查询。
            mWeiboAPI.reqFTL(mIds.get(current), new RequestListener() {
                //监听器。
                @Override
                public void onComplete(String s) {
                    FTimeLine mFTimeLine = GsonUtils.fromJson(s, FTimeLine.class);
                    List<StatusContent> tempList = mFTimeLine.getStatuses();

                    if (tempList.size() != 0) {
                        // 先把结果返回
                        l.onResponse(tempList);

                        // 然后再把缓存JSON。
//                        JsonCacheDao.insertNew(weiboCache, tempList);
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
                                JsonCacheDao.insertSingle(mWeiboAPI.getUserId(), temp);
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

    private List<StatusContent> getCache(int start, final List<Long> ids) {
        List<Long> temp = new ArrayList<>();
        for (int i = start, x = 0; x < WayPoConstants.PER_PAGE_COUNT; i++, x++) {
            temp.add(ids.get(i));
        }

        return JsonCacheDao.queryMulti(mWeiboAPI.getUserId(), temp);
    }

    /**
     * 遍历ID列表，找到与lastId区的ID下标。
     *
     * @param lastId 当前页面最后的ID
     * @param ids    ID列表
     * @return 匹配的下标。
     */
    private int getStart(long lastId, List<Long> ids) {
        int start = 0;
        for (int i = 0; i < ids.size(); i++) {
            if (lastId == ids.get(i)) {
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
            Toastutils.s(e.getMessage());
//            Toast.makeText(mContext, info != null ? info.toString() : "", Toast.LENGTH_LONG).show();
        }

        private void replaceIds(FTLIds tempIds) {
            mIds.clear();
            mIds.addAll(tempIds.getStatuses());
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
