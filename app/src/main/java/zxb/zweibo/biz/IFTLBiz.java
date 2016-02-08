package zxb.zweibo.biz;

import java.util.List;

import zxb.zweibo.bean.StatusContent;

/**
 * 时间线的业务逻辑层。
 * Created by Rex.Zhang on 2016/2/8.
 */
public interface IFTLBiz {
    /**
     * 获取下一面的微博。
     * @param lastId 0则为初始化
     * @return
     */
    List<StatusContent> requestNextPage(long lastId, responseListener l);

    /**
     * 刷新当前页面。
     * @return 如果为null则表示没有更新。
     */
    List<StatusContent> refresh(responseListener l);

    interface responseListener {
        void onResponse(List<StatusContent> ftl);
    }

    interface idsListener {
        void onResponse(List<Long> ids);
    }

    interface requestListener {
        void onSuccess();
        void onFail();
    }
}
