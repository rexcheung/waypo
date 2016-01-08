package zxb.zweibo.bean.holder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import zxb.zweibo.R;
import zxb.zweibo.bean.ImgBrowserWeiBoItem;
import zxb.zweibo.bean.PicUrls;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.bean.User;
import zxb.zweibo.common.ImageUtil;
import zxb.zweibo.ui.ImageBrowserActivity;
import zxb.zweibo.widget.WeiboText;

/**
 * FriendsTimeLine Adapter的ViewHolder.
 * <p/>
 * Created by rex on 15-8-22.
 */
public class FTLHolder extends RecyclerView.ViewHolder {
    public View view;
    /**
     * 屏幕上显示的名字
     */
    public WeiboText tvScreenName;
    /**
     * 发自XX
     */
    public TextView tvFrom;
    /**
     * 作者的话
     */
    public WeiboText tvContent;
    /**
     * 补转发者的内容
     */
    public WeiboText tvReContent;
    public TextView tvReUser;
    public TextView tvRepostCount;
    public TextView tvCommentCount;
    public TextView tvLikeCount;
    public View layDiver;
    public ImageView imgAvatar;
    public ImageView imgV;

    ImageView img1,img2,img3,img4,img5,img6,img7,img8,img9;

//    @Bind({R.id.img1, R.id.img2, R.id.img3,
//            R.id.img4, R.id.img5, R.id.img6,
//            R.id.img7, R.id.img8, R.id.img9})
    public List<ImageView> imgList;

    public FTLHolder(View itemView) {
        super(itemView);
        this.view = itemView;
        this.tvScreenName = inject(R.id.tvScreenName);
        this.tvFrom = inject(R.id.tvFrom);
        this.tvContent = inject(R.id.tvContent);
        this.tvReContent = inject(R.id.tvReContent);
        this.tvReUser = inject(R.id.tvReUser);
        this.tvRepostCount = inject(R.id.tvRepostCount);
        this.tvCommentCount = inject(R.id.tvCommentCount);
        this.tvLikeCount = inject(R.id.tvLikeCount);
        this.layDiver = inject(R.id.layDiver);
        this.imgAvatar = inject(R.id.imgAvatar);
        this.imgV = inject(R.id.imgV);
        this.img1 = inject(R.id.img1);
        this.img2 = inject(R.id.img2);
        this.img3 = inject(R.id.img3);
        this.img4 = inject(R.id.img4);
        this.img5 = inject(R.id.img5);
        this.img6 = inject(R.id.img6);
        this.img7 = inject(R.id.img7);
        this.img8 = inject(R.id.img8);
        this.img9 = inject(R.id.img9);
        this.imgList = new ArrayList<>();
        imgList.add(img1);
        imgList.add(img2);
        imgList.add(img3);
        imgList.add(img4);
        imgList.add(img5);
        imgList.add(img6);
        imgList.add(img7);
        imgList.add(img8);
        imgList.add(img9);
    }

    private <T extends View> T inject(int id) {
        return (T) view.findViewById(id);
    }

//    private ImageUtil imageUtil;
    Context mContext;
    public void init(Context context, StatusContent statusContent, ImageUtil imageUtil){
        this.mContext = context;
        initWord(statusContent);
        initImage(statusContent, imageUtil);
    }

    /**
     * 处理文字信息.
     *
     * @param sc 当前Json实体
     */
    private void initWord(StatusContent sc){
        this.tvScreenName.setText(sc.getUser().getScreen_name());

//        StringBuilder source = new StringBuilder(sc.getSource());
//        int begin = source.indexOf(">");
//        int end = source.indexOf("</a");

        //XX分钟前，发自iPhoneX
        this.tvFrom.setText(sc.getCreated_at()/*+"  " + source.substring(begin+1, end-1)*/);

//        SpannableString original = spanHelper.newSpanInstance(sc.getText());
        this.tvContent.setSpanText(sc.getText());
        this.tvContent.setMovementMethod(LinkMovementMethod.getInstance());

        //如果为转发
        StatusContent retweeted_status = sc.getRetweeted_status();
//        User reUser;
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

//            SpannableString reSpan = spanHelper.newSpanInstance(reContentText);
            this.tvReContent.setSpanText(reContentText);
            this.tvReContent.setVisibility(View.VISIBLE);
            this.tvReContent.setMovementMethod(LinkMovementMethod.getInstance());

            this.layDiver.setVisibility(View.VISIBLE);
        } else {
            //原创微博，则把转发者和文字内容TextView隐藏
            this.tvReUser.setVisibility(View.GONE);
            this.tvReContent.setVisibility(View.GONE);

            //隐藏分隔线
            this.layDiver.setVisibility(View.GONE);
        }
    }

    private void initImage(final StatusContent statusContent, ImageUtil imageUtil) {
        /*if(mVolleyHelper == null){
            mVolleyHelper = new VolleyHelper(mContext);
        }*/
        resetImage(this.imgAvatar, R.drawable.icon_github);
        resetImages(this.imgList, R.drawable.icon_github);

        User user = statusContent.getUser();
        if (user.getVerified()) this.imgV.setVisibility(View.VISIBLE);
        else this.imgV.setVisibility(View.GONE);

        //获取头像图片
//        mVolleyHelper.loadImg(this.imgAvatar, user.getProfile_image_url());
        imageUtil.showImage(this.imgAvatar, user.getProfile_image_url());

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
            imageUtil.showImages(this.imgList, oriPicUrls);
//            if(length == 1){
//                this.img1.getLayoutParams().height = mSingleImgH;
//                this.img1.getLayoutParams().width = mSingleImgW;
//            }
        } else if (rePicLength != 0) {
            imageUtil.showImages(this.imgList, rePicUrls);
//            if(rePicLength == 1){
//                this.img1.getLayoutParams().height = mSingleImgH;
//                this.img1.getLayoutParams().width = mSingleImgW;
//            }
        }

        // 把没有图片的ImageView隐藏
        for (int i = (length != 0 ? length : rePicLength); i < 9; i++) {
            this.imgList.get(i).setVisibility(View.GONE);
        }

        for (int i=0; i<this.imgList.size(); i++){
            ImageView iv = this.imgList.get(i);
            if (iv.getVisibility() == View.VISIBLE){
                final int finalI = i;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().postSticky(new ImgBrowserWeiBoItem(statusContent, finalI));
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

    /**
     * 每条微博底部的赞，转和评论数
     * @param statusContent 该条微博的内容
     */
    private void initAttitudes(StatusContent statusContent) {
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
            this.tvLikeCount.setText(like);
        }

        if (!"0".equals(rePost)) {
            this.tvRepostCount.setText(rePost);
        }

        if (!"0".equals(commentCount)) {
            this.tvCommentCount.setText(commentCount);
        }
    }
}
