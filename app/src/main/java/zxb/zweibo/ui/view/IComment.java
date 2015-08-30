package zxb.zweibo.ui.view;

import java.util.List;

import zxb.zweibo.bean.CommentJson;

/**
 * 评论Activity
 * Created by rex on 15-8-22.
 */
public interface IComment {

    public static final int FAVORUTE_SUCCESS=1000;
    public static final int FAVORUTE_FAIL=1010;

    public static final int FORWARD_SUCCESS=2000;
    public static final int FORWARD_FAIL=2020;


    /**
     * 关闭Activity
     */
    public void close();

    /**
     * 收藏
     * @param responseCode 收藏成功与否
     */
    public void favoriteResponse(int responseCode);

    /**
     * 转发
     */
    public void fowardResponse(int response);

    /**
     * 更新内容
     * @param commentList 请求评论成功后
     */
    public void updateList(List<CommentJson> commentList);
}
