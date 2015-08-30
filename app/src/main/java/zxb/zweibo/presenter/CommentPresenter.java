package zxb.zweibo.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.List;

import zxb.zweibo.GlobalApp;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.bean.CommentJson;
import zxb.zweibo.bean.CommentsJson;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.ui.view.IComment;

/**
 * Created by rex on 15-8-22.
 */
public class CommentPresenter {

    IComment commentInterface;
    Context mContext;
    WeiboAPIUtils mWeiboAPI;
    List<CommentJson> mCommentList;
    StatusContent mStatusContent;

    Gson gson;
    public CommentPresenter(IComment iComment, StatusContent sc){
        this.commentInterface = iComment;
        this.mStatusContent = sc;

        this.mContext = (Context) iComment;
        this.gson = new Gson();
    }

    public void requestComments(){
        GlobalApp app = (GlobalApp) mContext.getApplicationContext();
        mWeiboAPI = app.getWeiboUtil();
        mWeiboAPI.requestCommentsById(mStatusContent.getId(), commentListener);
    }

    RequestListener commentListener = new RequestListener(){
        @Override
        public void onComplete(String response) {
            CommentsJson commentsJson = gson.fromJson(response, CommentsJson.class);
            if (mCommentList != null) {
                mCommentList.clear();
            }
            mCommentList = commentsJson.comments;

            commentInterface.updateList(mCommentList);
        }

        @Override
        public void onWeiboException(WeiboException e) {}
    };

    public void add2Favorite(){
        mWeiboAPI.addFavorites(mStatusContent.getId(), favListen);
    }

    RequestListener favListen = new RequestListener() {
        @Override
        public void onComplete(String s) {
            commentInterface.favoriteResponse(IComment.FAVORUTE_SUCCESS);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            e.printStackTrace();
            commentInterface.favoriteResponse(IComment.FAVORUTE_FAIL);
        }
    };

    public void forward(String status){
        mWeiboAPI.forward(mStatusContent.getId(), status, forwardListen);
    }

    RequestListener forwardListen = new RequestListener() {
        @Override
        public void onComplete(String s) {
            commentInterface.fowardResponse(IComment.FORWARD_SUCCESS);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            e.printStackTrace();
            commentInterface.fowardResponse(IComment.FORWARD_FAIL);
        }
    };
}
