package zxb.zweibo.ui.fragment.view;

import java.util.List;

import zxb.zweibo.bean.StatusContent;

/**
 * Created by Rex.Zhang on 2016/2/8.
 */
public interface IFTLView {
    void onRefresh(List<StatusContent> weiboList);
    void onUpdate(List<StatusContent> weiboList);
}
