package zxb.zweibo.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.LogUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.R;
import zxb.zweibo.bean.FTimeLine;
import zxb.zweibo.bean.PicUrls;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.bean.User;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.Constants;

/**
 * 显示FriendsTimeLine最新关注用户的微博
 * Created by rex on 15-7-31.
 */
public class FTimeLinsFragment extends Fragment {

    /**
     * 初始化时传入的父类Activity, LayoutInflater需要使用
     */
    private static Activity mContext;
    private static int mPosition;
    List<String> mDatas;

    /**
     * 新浪SDK.
     */
    private Oauth2AccessToken mAccessToken;
    private StatusesAPI mStatusesAPI;

    /**
     * 接收最近10条微博的实体类.
     */
    FTimeLine mFTimeLine;

    /**
     * 实体类中的10条微博.
     */
    ArrayList<StatusContent> mStatusesList;

    private String TAG;

    RecyclerView mRecyclerView;

    /**
     * 初始化.
     *
     * @param position 位置.
     * @param content  Content.
     * @return
     */
    public static FTimeLinsFragment newInstance(int position, Activity content) {
        FTimeLinsFragment fragment = new FTimeLinsFragment();
        mPosition = position;
        mContext = content;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        View view = inflater.inflate(R.layout.fragment_timeline, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvContent);

        LinearLayoutManager ll = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(ll);


        return view;
    }

    private void init() {
        TAG = getClass().getSimpleName();

        mAccessToken = AccessTokenKeeper.readAccessToken(mContext);
        mStatusesAPI = new StatusesAPI(mContext, Constants.APP_KEY, mAccessToken);

        mStatusesAPI.friendsTimeline(0L, 0L, 10, 1, false, 0, false, mListener);

        mDatas = new ArrayList<String>();
        for (int i = 'A'; i <= 'z'; i++) {
            mDatas.add(String.valueOf((char) i));
        }
    }

    private void initDatas() {
        mStatusesList = mFTimeLine.getStatuses();
        if (mStatusesList == null) {
            return;
        }

        mRecyclerView.setAdapter(new MyAdapter());
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.Holder> {

        @Override
        public MyAdapter.Holder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_timeline, viewGroup, false);
            Holder holder = new Holder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyAdapter.Holder viewHolder, int position) {
            ArrayList<StatusContent> list = mStatusesList;
            StatusContent statusContent = mStatusesList.get(position);
            User user = statusContent.getUser();

            viewHolder.tvScreenName.setText(user.getScreen_name());

            StringBuilder source = new StringBuilder(statusContent.getSource());
            int begin = source.indexOf(">");
            int end = source.indexOf("</a");
//            Log.i(TAG, )
            String realSrouce = source.substring(begin+1, end-1);

            viewHolder.tvFrom.setText(statusContent.getCreated_at()+"  " + realSrouce);
            viewHolder.tvContent.setText(statusContent.getText());

            //如果为转发
            StatusContent retweeted_status = statusContent.getRetweeted_status();
            User reUser = null;
            String like = null;
            String rePost = null;
            String commentCount = null;
            //判断是否原创微博
            if (retweeted_status != null) {
                if (retweeted_status.getUser() != null) {
                    reUser = retweeted_status.getUser();
                    viewHolder.tvReUser.setText(reUser.getScreen_name()+": ");
                }
                viewHolder.tvReContent.setText(retweeted_status.getText());

                PicUrls[] picUrls = retweeted_status.getPic_urls();
                if (picUrls != null || picUrls.length != 0) {

                }

            } else {
                //原创微博，则把转发者和文字内容TextView隐藏
                viewHolder.tvReUser.setVisibility(View.GONE);
                viewHolder.tvReContent.setVisibility(View.GONE);
            }


            //------------------赞，转和评论数-------------------------
            //赞，转和评论数处理逻辑:转发者只要其中不为0，则显示转发者的数量
            like = String.valueOf(statusContent.getAttitudes_count());
            rePost = statusContent.getReposts_count();
            commentCount = statusContent.getComments_count();

            //没有则显示原作者的相应数量
            if(retweeted_status != null){
                if (!like.equals("0") && !rePost.equals("0") && !commentCount.equals("0")) {
                    like = String.valueOf(retweeted_status.getAttitudes_count());
                    rePost = retweeted_status.getReposts_count();
                    commentCount = retweeted_status.getComments_count();
                }
            }

            //底部，赞，转，评论数：如果为哪一项0，则不显示数字，只显示图标
            if(!like.equals("0")){
                viewHolder.tvLikeCount.setText(like);
            }

            if(!rePost.equals("0")) {
                viewHolder.tvRepostCount.setText(rePost);
            }

            if(!commentCount.equals("0")){
                viewHolder.tvCommentCount.setText(commentCount);
            }
            //------------------赞，转和评论数-------------------------


        }

        @Override
        public int getItemCount() {
            return mStatusesList.size();
        }

        /**
         * ViewHolder
         */
        class Holder extends RecyclerView.ViewHolder {
            TextView tvScreenName;
            TextView tvFrom;
            TextView tvContent;
            TextView tvReContent;
            TextView tvReUser;
            TextView tvRepostCount;
            TextView tvCommentCount;
            TextView tvLikeCount;

            public Holder(View itemView) {
                super(itemView);
                tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
                tvFrom = (TextView) itemView.findViewById(R.id.tvFrom);
                tvContent = (TextView) itemView.findViewById(R.id.tvContent);
                tvReContent = (TextView) itemView.findViewById(R.id.tvReContent);
                tvReUser = (TextView) itemView.findViewById(R.id.tvReUser);
                tvRepostCount = (TextView) itemView.findViewById(R.id.tvRepostCount);
                tvCommentCount = (TextView) itemView.findViewById(R.id.tvCommentCount);
                tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
            }
        }
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                Gson gson = new Gson();
                mFTimeLine = gson.fromJson(response, FTimeLine.class);
//                testEntity mFTimeLine = gson.fromJson(response, testEntity.class);
                initDatas();
                LogUtil.i(TAG, response);
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(mContext,
                                "获取微博信息流成功, 条数: " + statuses.statusList.size(),
                                Toast.LENGTH_LONG).show();
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(mContext,
                            "发送一送微博成功, id = " + status.id,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(mContext, info.toString(), Toast.LENGTH_LONG).show();
        }
    };
}
