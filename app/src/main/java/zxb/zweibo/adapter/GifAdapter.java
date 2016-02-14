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

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import zxb.zweibo.GlobalApp;
import zxb.zweibo.R;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.Utils.SpanHelper;
import zxb.zweibo.Utils.Toastutils;
import zxb.zweibo.bean.FavoriteItem;
import zxb.zweibo.bean.ImgBrowserWeiBoItem;
import zxb.zweibo.bean.PicUrls;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.bean.User;
import zxb.zweibo.bean.holder.FTLHolder;
import zxb.zweibo.common.ImageUtil;
import zxb.zweibo.common.Utils;
import zxb.zweibo.ui.CommentActivity;
import zxb.zweibo.ui.GifBrowserActivity;

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
//        viewHolder.init(mContext, statusContent, imageUtil);

        initImage(viewHolder, statusContent);

        initWord(viewHolder, statusContent);

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

    /**
     * 处理文字信息.
     *
     * @param viewHolder ViewHolder
     * @param statusContent 当前Json实体
     */
    private void initWord(FTLHolder viewHolder, StatusContent statusContent) {
        viewHolder.tvScreenName.setSpanText(statusContent.getUser().getScreen_name());

        //XX分钟前，发自iPhoneX
        viewHolder.tvFrom.setText(statusContent.getCreated_at()/*+"  " + source.substring(begin+1, end-1)*/);

        SpannableString original = spanHelper.newSpanInstance(statusContent.getText());
        viewHolder.tvContent.setText(original);
        viewHolder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());

        //如果为转发
        StatusContent retweeted_status = statusContent.getRetweeted_status();
        //判断是否原创微博
        if (retweeted_status != null) {
            /*if (retweeted_status.getUser() != null) {
                User reUser = retweeted_status.getUser();
                viewHolder.tvReUser.setVisibility(View.VISIBLE);
                viewHolder.tvReUser.setSpanText(reUser.getScreen_name()+": ");
            }*/

            User reUser = retweeted_status.getUser();
            String reContentText = null;
            if(reUser != null){
                // 把被转发的用户与微博文本拼接
                StringBuilder sb = new StringBuilder();
                sb.append("@");
                sb.append(reUser.getScreen_name());
                sb.append(":");
                sb.append(retweeted_status.getText());

                reContentText = sb.toString();
            } else {
                reContentText = retweeted_status.getText();
            }

            SpannableString reSpan = spanHelper.newSpanInstance(reContentText);
            viewHolder.tvReContent.setVisibility(View.VISIBLE);
            viewHolder.tvReContent.setText(reSpan);
            viewHolder.tvReContent.setMovementMethod(LinkMovementMethod.getInstance());

            viewHolder.layDiver.setVisibility(View.VISIBLE);
        } else {
            //原创微博，则把转发者和文字内容TextView隐藏
            viewHolder.tvReUser.setVisibility(View.GONE);
            viewHolder.tvReContent.setVisibility(View.GONE);

            //隐藏分隔线
            viewHolder.layDiver.setVisibility(View.GONE);
        }


    }

    /**
     * 每条微博底部的赞，转和评论数
     * @param viewHolder ViewHolder
     * @param statusContent 该条微博的内容
     */
    private void initAttitudes(FTLHolder viewHolder, StatusContent statusContent) {
        String like;
        String rePost;
        String commentCount;

        //赞，转和评论数处理逻辑:转发者只要其中不为0，则显示转发者的数量
        like = String.valueOf(statusContent.getAttitudes_count());
        rePost = statusContent.getReposts_count();
        commentCount = statusContent.getComments_count();

        StatusContent retweeted_status = statusContent.getRetweeted_status();
        //没有则显示原作者的相应数量
        if (retweeted_status != null) {
            if (!"0".equals(like) && !"0".equals(rePost) && !"0".equals(commentCount)) {
                like = String.valueOf(retweeted_status.getAttitudes_count());
                rePost = retweeted_status.getReposts_count();
                commentCount = retweeted_status.getComments_count();
            }
        }

        //底部，赞，转，评论数：如果为哪一项0，则不显示数字，只显示图标
        if (!"0".equals(like)) {
            viewHolder.tvLikeCount.setText(like);
        }

        if (!"0".equals(rePost)) {
            viewHolder.tvRepostCount.setText(rePost);
        }

        if (!"0".equals(commentCount)) {
            viewHolder.tvCommentCount.setText(commentCount);
        }
    }


//    private VolleyHelper mVolleyHelper;
    /**
     * 显示图像，把不需要的ImageView设置为GONE
     * @param viewHolder
     * @param statusContent
     */
    private void initImage(FTLHolder viewHolder, final StatusContent statusContent) {
        /*if(mVolleyHelper == null){
            mVolleyHelper = new VolleyHelper(mContext);
        }*/
        resetImage(viewHolder.imgAvatar, R.drawable.icon_github);
        resetImages(viewHolder.imgList, R.drawable.icon_github);

        User user = statusContent.getUser();
        if (user.getVerified()) viewHolder.imgV.setVisibility(View.VISIBLE);
        else viewHolder.imgV.setVisibility(View.GONE);

        //获取头像图片
//        mVolleyHelper.loadImg(viewHolder.imgAvatar, user.getProfile_image_url());
        imageUtil.showImage(viewHolder.imgAvatar, user.getProfile_image_url());

        PicUrls[] oriPicUrls = statusContent.getPic_urls();
        int length = oriPicUrls.length;

        PicUrls[] rePicUrls = null;
        int rePicLength = 0;
        // 若为转发，则进入if里面
        if (statusContent.getRetweeted_status() != null) {
            rePicUrls = statusContent.getRetweeted_status().getPic_urls();
            // 转发内容有多少张图片
            if (rePicUrls != null) {
                rePicLength = rePicUrls.length;
            }
        }

        if (length != 0) {
            imageUtil.showImages(viewHolder.imgList, oriPicUrls);
//            if(length == 1){
//                viewHolder.img1.getLayoutParams().height = mSingleImgH;
//                viewHolder.img1.getLayoutParams().width = mSingleImgW;
//            }
        } else if (rePicLength != 0) {
            imageUtil.showImages(viewHolder.imgList, rePicUrls);
//            if(rePicLength == 1){
//                viewHolder.img1.getLayoutParams().height = mSingleImgH;
//                viewHolder.img1.getLayoutParams().width = mSingleImgW;
//            }
        }

        // 把没有图片的ImageView隐藏
        for (int i = (length != 0 ? length : rePicLength); i < 9; i++) {
            viewHolder.imgList.get(i).setVisibility(View.GONE);
        }

        for (int i=0; i<viewHolder.imgList.size(); i++){
            ImageView iv = viewHolder.imgList.get(i);
            if (iv.getVisibility() == View.VISIBLE){
                final int finalI = i;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        EventBus.getDefault().postSticky(new ImgBrowserWeiBoItem(statusContent, finalI));
                        Intent intent = new Intent(mContext, GifBrowserActivity.class);
                        intent.putExtra(GifBrowserActivity.PUT_ITEM, new ImgBrowserWeiBoItem(statusContent, finalI));
                        mContext.startActivity(intent);
                    }
                });
            }
        }

    }

    /**
     * 把ITEM里面的所有IMAGE重设为默认的图片，
     * ViewHolder复用的特性，导致ITEM的图片在未读取完成之前会显示复用前ITEM的图片，
     * 所以要先将图片还原做默认的图片.
     * 如果图片visible已经设置为GONE，则设置后也不会导致图片重新显示出来.
     *
     * @param imgList ITEM里面ImageView的List集合
     * @param resId 要设置成的图片，这个必须是本地图片，否则就达不到重置的目的
     */
    private void resetImages(List<ImageView> imgList, int resId) {
        if (imgList != null) {
            for (ImageView img : imgList) {
                resetImage(img, resId);
//                resetDimens(img);
            }
        }
    }

    private void resetImage(ImageView img, int resId) {
        if (img != null) {
            img.setImageResource(resId);
            img.setVisibility(View.VISIBLE);
        }
    }

    private void resetDimens(ImageView img) {
        img.getLayoutParams().height = mImgH;
        img.getLayoutParams().width = mImgW;
    }

    Gson gson = new Gson();
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