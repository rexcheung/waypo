package zxb.zweibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import zxb.zweibo.R;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.Utils.Toastutils;
import zxb.zweibo.bean.CommentsJson;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.bean.holder.FTLHolder;
import zxb.zweibo.common.ImageUtil;
import zxb.zweibo.common.Utils;
import zxb.zweibo.ui.CommentActivity;

/**
 * FriendsTimeLine Fragment里面RecyclerView的Adapter.
 * Created by rex on 15-8-4.
 */
public class FTimeLinsAdapter extends RecyclerView.Adapter<FTLHolder> {

    Context mContext;
    List<StatusContent> mStatusesList;
    private ImageUtil imageUtil;

    private FTimeLinsAdapter() {
    }

    private FTimeLinsAdapter(Context context, List<StatusContent> statusesList, ImageUtil imageUtil) {
        this.mContext = context;
        if (statusesList == null) {
            this.mStatusesList = new ArrayList<>();
        } else {
            this.mStatusesList = statusesList;
        }
        this.imageUtil = imageUtil;

        getScreenSize(context);

    }


    public static FTimeLinsAdapter newInstance(Context context, List<StatusContent> statusesList, ImageUtil imageUtil) {
        FTimeLinsAdapter adapter = new FTimeLinsAdapter(context, statusesList, imageUtil);
        return adapter;
    }

    public static FTimeLinsAdapter newInstance(Context context, ImageUtil imageUtil) {
        FTimeLinsAdapter adapter = new FTimeLinsAdapter(context, null, imageUtil);
        return adapter;
    }


    private int mImgH;
    private int mImgW;

    /**
     * 检测当前设备的屏幕尺寸
     *
     * @param context 必须要Activity的实体才能获取WindowManager，所以需要传入Activity
     */
    private void getScreenSize(Context context) {
        DisplayMetrics metric = Utils.getMetrics((Activity) context);
//        Activity activity = (Activity) context;
//        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
//        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）

        mImgH = height / 6;
        mImgW = width / 6;
    }

    @Override
    public FTLHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_timeline, viewGroup, false);
        FTLHolder holder = new FTLHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final FTLHolder viewHolder, int position) {
        final StatusContent statusContent = mStatusesList.get(position);

        viewHolder.init(mContext, statusContent, imageUtil);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(v, "click", Snackbar.LENGTH_SHORT).show();
//                mWeiboAPI.requestCommentsById(statusContent.getId(), commentListener);
                EventBus.getDefault().postSticky(statusContent);
                mContext.startActivity(new Intent(mContext, CommentActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mStatusesList == null) {
            return 0;
        }
        return mStatusesList.size();
    }

    public void update(List<StatusContent> list) {
        this.mStatusesList.addAll(list);
        this.notifyDataSetChanged();
    }

    public void setDatas(List<StatusContent> list) {
        if (list.size() > 0 && list.get(0).getId() != mStatusesList.get(0).getId()){
            this.mStatusesList = list;
            this.notifyDataSetChanged();
        } else {
            Toastutils.s("没有数据更新哦");
            Logger.i("FTLAdapter.setDatas(): 没有数据更新哦");
        }

    }

    private void resetDimens(ImageView img) {
        img.getLayoutParams().height = mImgH;
        img.getLayoutParams().width = mImgW;
    }

    Gson gson = new Gson();

    public void cleanCache() {
        imageUtil.clearMemoryCache();
    }

    RequestListener commentListener = new RequestListener() {

        @Override
        public void onComplete(String s) {
            Logger.i(s);
            CommentsJson commentsJson = gson.fromJson(s, CommentsJson.class);

        }

        @Override
        public void onWeiboException(WeiboException e) {
            e.printStackTrace();
        }
    };

    /**
     * 返回屏幕上最后一条微博的ID
     *
     * @return ID
     */
    public long getLastId() {
        return mStatusesList.get(mStatusesList.size() - 1).getId();
    }

    public long getFirstId() {
        return mStatusesList.get(0).getId();
    }

}