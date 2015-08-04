package zxb.zweibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import zxb.zweibo.R;
import zxb.zweibo.bean.PicUrls;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.bean.User;
import zxb.zweibo.common.VolleyHelper;

/**
 * FriendsTimeLine Fragment里面RecyclerView的Adapter.
 * Created by rex on 15-8-4.
 */
public class FTimeLinsAdapter extends RecyclerView.Adapter<FTimeLinsAdapter.Holder> {

    Context mContext;
    List<StatusContent> mStatusesList;

    private FTimeLinsAdapter(){}

    private FTimeLinsAdapter(Context context, List<StatusContent> statusesList){
        this.mContext = context;
        this.mStatusesList = statusesList;
    }

    public static FTimeLinsAdapter newInstance(Context context, List<StatusContent> statusesList) {
        FTimeLinsAdapter adapter = new FTimeLinsAdapter(context,statusesList);
        return adapter;
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
        //>>>>>>>>>>>>>>>>>>>>文字信息处理>>>>>>>>>>>>>>>>>>>>>>>
        ArrayList<StatusContent> list = (ArrayList<StatusContent>) mStatusesList;
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

        //>>>>>>>>>>>>>>>>>>>赞，转和评论数，这里开始>>>>>>>>>>>>>>>>>>>>
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
        //<<<<<<<<<<<<<<<<<<赞，转和评论数<<<<<<<<<<<<<<<<<<<<<<<<<
        //<<<<<<<<<<<<<<<<<文字信息处理到此<<<<<<<<<<<<<<<<<<<<<<<<<

        //>>>>>>>>>>>>>>>>>>>>图像处理>>>>>>>>>>>>>>>>>>>>>>>>>>>

        PicUrls[] oriPicUrls = statusContent.getPic_urls();
        String thumPic = statusContent.getThumbnail_pic();
            /*if (oriPicUrls.length == 0 || oriPicUrls == null) {
                for(ImageView img : viewHolder.imgList){
                    img.setVisibility(View.GONE);
                }
            }else if(thumPic != null || thumPic.length() != 0){
                viewHolder.imgList.get(0).setVisibility(View.VISIBLE);
                VolleyHelper.loadImg(mContext, viewHolder.imgList.get(0), thumPic);

                for (int i=1; i<9; i++){
                    viewHolder.imgList.get(i).setVisibility(View.GONE);
                }
            }else*/
        if(oriPicUrls.length != 0) {
            VolleyHelper.loadMultiImg(mContext, viewHolder.imgList, oriPicUrls);
        }
        for (int i = oriPicUrls.length; i < 9; i++) {
            viewHolder.imgList.get(i).setVisibility(View.GONE);
        }

        VolleyHelper.loadImg(mContext, viewHolder.imgAvatar, user.getProfile_image_url());

        //<<<<<<<<<<<<<<<<<<图像处理<<<<<<<<<<<<<<<<<<<<<<<<<
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

            imgList = new ArrayList<ImageView>();
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

