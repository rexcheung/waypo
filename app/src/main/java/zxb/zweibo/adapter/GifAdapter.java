package zxb.zweibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import zxb.zweibo.GlobalApp;
import zxb.zweibo.R;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.Utils.SpanHelper;
import zxb.zweibo.Utils.Toastutils;
import zxb.zweibo.bean.FavoriteItem;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.bean.holder.FTLHolder;
import zxb.zweibo.common.ImageUtil;
import zxb.zweibo.common.Utils;
import zxb.zweibo.ui.CommentActivity;

/**
 * FriendsTimeLine Fragment里面RecyclerView的Adapter.
 * Created by rex on 15-8-4.
 */
public class GifAdapter extends RecyclerView.Adapter<FTLHolder> {

    Context mContext;
    List<StatusContent> mStatusesList;
    private ImageUtil imageUtil;

    private SpanHelper spanHelper;

    private GifAdapter(){}

    private GifAdapter(Context context, List<FavoriteItem> favList){
        this.mContext = context;

        spanHelper = new SpanHelper(context);

        getScreenSize(context);

        GlobalApp app = (GlobalApp) context.getApplicationContext();
        imageUtil = app.getmImageUtil();

        mStatusesList = new ArrayList<>();
        for (FavoriteItem item : favList) {
            mStatusesList.add(item.getStatus());
        }
    }

    public static GifAdapter newInstance(Context context, List<FavoriteItem> favList) {
        return new GifAdapter(context,favList);
    }


    private int mImgH;
    private int mImgW;
//    private int mSingleImgH;
//    private int mSingleImgW;

    /**
     * 检测当前设备的屏幕尺寸.
     *
     * @param context 必须要Activity的实体才能获取WindowManager，所以需要传入Activity
     */
    private void getScreenSize(Context context){
        DisplayMetrics metric = Utils.getMetrics((Activity) context);
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
        return new FTLHolder(view);
    }

    @Override
    public void onBindViewHolder(final FTLHolder viewHolder, int position) {
        final StatusContent statusContent = mStatusesList.get(position);
		viewHolder.init(mContext, statusContent, imageUtil);
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void cleanCache(){
        imageUtil.clearMemoryCache();
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

    public long getLastId() {
        return mStatusesList.get(mStatusesList.size() - 1).getId();
    }

    public long getFirstId() {
        return mStatusesList.get(0).getId();
    }
}