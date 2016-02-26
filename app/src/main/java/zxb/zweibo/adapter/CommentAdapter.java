package zxb.zweibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import zxb.zweibo.R;
import zxb.zweibo.Utils.AnimationUtil;
import zxb.zweibo.Utils.SpanHelper;
import zxb.zweibo.bean.CommentJson;
import zxb.zweibo.bean.ImgBrowserWeiBoItem;
import zxb.zweibo.bean.PicUrls;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.bean.User;
import zxb.zweibo.bean.holder.CommentHolder;
import zxb.zweibo.ui.GifBrowserActivity;

/**
 * 单条微博评论页面的Adapter.
 * <p/>
 * Created by rex on 15-8-22.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {

    private static Context mContext;
    private static List<CommentJson> mCommonList;
    static StatusContent mHeadContent;
    private static SpanHelper spanHelper;

    private CommentAdapter() {
    }

    private int picLength;
    private static boolean isOriginal = false;

    public static CommentAdapter newInstance(Context context, List<CommentJson> commonList, StatusContent header) {
        CommentAdapter adapter = new CommentAdapter();
        mContext = context;
        mCommonList = commonList;
        mHeadContent = header;
        spanHelper = new SpanHelper(context);

        isOriginal = header.getRetweeted_status() == null;
        return adapter;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.comment_item, viewGroup, false);

        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {

        Uri uri = null;
        if (isHead(position)) {
            uri = Uri.parse(mHeadContent.getUser().getProfile_image_url());
            initHeader(holder);

        } else {
            int index = position - 1;
            User user = mCommonList.get(index).user;
            uri = Uri.parse(user.getProfile_image_url());
            holder.tvScreenName.setText(spanHelper.newSpanInstance(user.getScreen_name()));

            //V标志
            if (user.getVerified()) holder.imgV.setVisibility(View.VISIBLE);
            else holder.imgV.setVisibility(View.GONE);

            holder.tvFrom.setText(spanHelper.newSpanInstance(mCommonList.get(index).text));

            hideItem(holder);
        }

        holder.imgAvatar.setImageURI(uri);

		AnimationUtil.alpha(holder.view, 700).start();

//		AnimationUtil.tranY(holder.view, 1000, 200).start();
//		AnimationUtil.rotation(holder.view, 2000);
    }

    private void initHeader(CommentHolder holder) {
        User user = mHeadContent.getUser();
        holder.tvScreenName.setText(user.getScreen_name());

        //V标志
        if (user.getVerified()) holder.imgV.setVisibility(View.VISIBLE);
        else holder.imgV.setVisibility(View.GONE);

        holder.tvFrom.setText(mHeadContent.getCreated_at());

        showImg(holder.imgList);
        checkHideItem(holder);
        initWord(holder);
        showItem(holder);
    }

    private void initWord(CommentHolder holder) {

        holder.tvContent.setText(spanHelper.newSpanInstance(mHeadContent.getText()));
//        holder.tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tvContent.setVisibility(View.VISIBLE);

        if (!isOriginal) {
            holder.tvReContent.setText(spanHelper.newSpanInstance(mHeadContent.getRetweeted_status().getText()));
//            holder.tvReContent.setMovementMethod(LinkMovementMethod.getInstance());
            holder.tvReContent.setVisibility(View.VISIBLE);
            holder.layDiver.setVisibility(View.VISIBLE);
        } else {
            holder.tvReContent.setVisibility(View.GONE);
            holder.layDiver.setVisibility(View.GONE);
        }

    }

    private void checkHideItem(CommentHolder holder) {
        if (isOriginal) {
            holder.layDiver.setVisibility(View.GONE);
            holder.tvReContent.setVisibility(View.GONE);
        }
    }

    private void showImg(List<SimpleDraweeView> imgList) {
        PicUrls[] pic_urls = null;
        if (mHeadContent.getRetweeted_status() == null)
            pic_urls = mHeadContent.getPic_urls();
        else
            pic_urls = mHeadContent.getRetweeted_status().getPic_urls();

        for (int i = 0; i < pic_urls.length; i++) {
            Uri uri = Uri.parse(pic_urls[i].getThumbnail_pic());
            imgList.get(i).setImageURI(uri);
            final int finalI = i;
            imgList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toastutils.s(String.valueOf(finalI));
//                    EventBus.getDefault().postSticky(new ImgBrowserWeiBoItem(mHeadContent, finalI));
                    Intent intent = new Intent(mContext, GifBrowserActivity.class);
                    intent.putExtra(GifBrowserActivity.PUT_ITEM, new ImgBrowserWeiBoItem(mHeadContent, finalI));
                    mContext.startActivity(intent);
                }
            });
        }

        picLength = pic_urls.length;
    }

    private void showItem(CommentHolder holder) {
        for (int i = 0; i < picLength; i++) {
            holder.imgList.get(i).setVisibility(View.VISIBLE);
        }
        for (int i = picLength; i < 9; i++) {
            holder.imgList.get(i).setVisibility(View.GONE);
        }
    }

    private void hideItem(CommentHolder holder) {
        holder.tvContent.setVisibility(View.GONE);
        holder.layDiver.setVisibility(View.GONE);
        holder.tvReContent.setVisibility(View.GONE);

        for (ImageView iv : holder.imgList) {
            iv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mCommonList.size() + 1;
    }

    private boolean isHead(int position) {
        return position == ViewType.HEAD;
    }

    class ViewType {
        private static final int HEAD = 0;
        private static final int NORMAL = 1;
    }
}


