package zxb.zweibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import zxb.zweibo.R;
import zxb.zweibo.bean.ImageBrowserBean;
import zxb.zweibo.bean.PicUrls;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.bean.User;
import zxb.zweibo.common.ImageUtil;
import zxb.zweibo.common.JsonCacheUtil;
import zxb.zweibo.common.Utils;
import zxb.zweibo.ui.ImageBrowserActivity;
import zxb.zweibo.widget.AppManager;

/**
 * FriendsTimeLine Fragment里面RecyclerView的Adapter.
 * Created by rex on 15-8-4.
 */
public class FTimeLinsAdapter extends RecyclerView.Adapter<FTimeLinsAdapter.Holder> {

    Context mContext;
    List<StatusContent> mStatusesList;
    private ImageUtil imageUtil;

    private JsonCacheUtil jsonCacheUtil;

    private FTimeLinsAdapter(){}

    private FTimeLinsAdapter(Context context, List<StatusContent> statusesList){
        this.mContext = context;
        this.mStatusesList = statusesList;
        imageUtil = new ImageUtil(mContext);
        jsonCacheUtil = new JsonCacheUtil(mContext);

        getScreenSize(context);
    }

    public static FTimeLinsAdapter newInstance(Context context, List<StatusContent> statusesList) {
        FTimeLinsAdapter adapter = new FTimeLinsAdapter(context,statusesList);
        return adapter;
    }


    private int mImgH;
    private int mImgW;
    private int mSingleImgH;
    private int mSingleImgW;

    /**
     * 检测当前设备的屏幕尺寸
     * @param context 必须要Activity的实体才能获取WindowManager，所以需要传入Activity
     */
    private void getScreenSize(Context context){
        DisplayMetrics metric = Utils.getMetrics((Activity) context);
//        Activity activity = (Activity) context;
//        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）
//        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）

        mImgH = height / 6;
        mImgW = width / 6;
        mSingleImgH = height / 3;
        mSingleImgW = width / 3;
    }

    @Override
    public FTimeLinsAdapter.Holder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_timeline, viewGroup, false);
        Holder holder = new Holder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final FTimeLinsAdapter.Holder viewHolder, int position) {
        ArrayList<StatusContent> list = (ArrayList<StatusContent>) mStatusesList;
        final StatusContent statusContent = mStatusesList.get(position);

        initImage(viewHolder, statusContent);

        initWord(viewHolder, statusContent);

//        initAttitudes(viewHolder, statusContent);
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
    private void initWord(Holder viewHolder, StatusContent statusContent) {
        viewHolder.tvScreenName.setText(statusContent.getUser().getScreen_name());

//        StringBuilder source = new StringBuilder(statusContent.getSource());
//        int begin = source.indexOf(">");
//        int end = source.indexOf("</a");

        //XX分钟前，发自iPhoneX
        viewHolder.tvFrom.setText(statusContent.getCreated_at()/*+"  " + source.substring(begin+1, end-1)*/);

        viewHolder.tvContent.setText(statusContent.getText());

        //如果为转发
        StatusContent retweeted_status = statusContent.getRetweeted_status();
//        User reUser;
        //判断是否原创微博
        if (retweeted_status != null) {
            if (retweeted_status.getUser() != null) {
                User reUser = retweeted_status.getUser();
                viewHolder.tvReUser.setVisibility(View.VISIBLE);
                viewHolder.tvReUser.setText(reUser.getScreen_name()+": ");
            }
            viewHolder.tvReContent.setVisibility(View.VISIBLE);
            viewHolder.tvReContent.setText(retweeted_status.getText());

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
    private void initAttitudes(Holder viewHolder, StatusContent statusContent) {
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
    private void initImage(Holder viewHolder, final StatusContent statusContent) {
        /*if(mVolleyHelper == null){
            mVolleyHelper = new VolleyHelper(mContext);
        }*/
        resetImage(viewHolder.imgAvatar, R.drawable.icon_github);
        resetImages(viewHolder.imgList, R.drawable.icon_github);

        User user = statusContent.getUser();

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

        for (ImageView iv : viewHolder.imgList){
            if (iv.getVisibility() == View.VISIBLE){
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().postSticky(statusContent);
                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
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

    public void cleanCache(){
        imageUtil.clearMemoryCache();
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

        View layDiver;

        ImageView imgAvatar;
        ArrayList<ImageView> imgList;

        ImageView img1;
        ImageView img2;
        ImageView img3;
        ImageView img4;
        ImageView img5;
        ImageView img6;
        ImageView img7;
        ImageView img8;
        ImageView img9;

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
            layDiver = itemView.findViewById(R.id.layDiver);

            imgAvatar = (ImageView) itemView.findViewById(R.id.imgAvatar);

            imgList = new ArrayList<>();
            imgList.add(img1 = (ImageView) itemView.findViewById(R.id.img1));
            imgList.add(img2 = (ImageView) itemView.findViewById(R.id.img2));
            imgList.add(img3 = (ImageView) itemView.findViewById(R.id.img3));
            imgList.add(img4 = (ImageView) itemView.findViewById(R.id.img4));
            imgList.add(img5 = (ImageView) itemView.findViewById(R.id.img5));
            imgList.add(img6 = (ImageView) itemView.findViewById(R.id.img6));
            imgList.add(img7 = (ImageView) itemView.findViewById(R.id.img7));
            imgList.add(img8 = (ImageView) itemView.findViewById(R.id.img8));
            imgList.add(img9 = (ImageView) itemView.findViewById(R.id.img9));

        }
    }
}

