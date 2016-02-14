package zxb.zweibo.bean.holder;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zxb.zweibo.R;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.bean.User;

/**
 * Created by rex on 15-8-22.
 */
public class CommentHolder extends RecyclerView.ViewHolder{
    public View view;
    /** 屏幕上显示的名字 */
    @Bind(R.id.tvScreenName)
    public TextView tvScreenName;
    /** 发自XX */
    @Bind(R.id.tvFrom) public TextView tvFrom;
    /** 作者的话 */
    @Bind(R.id.tvContent) public TextView tvContent;
    /** 补转发者的内容 */
    @Bind(R.id.tvReContent) public TextView tvReContent;
    @Bind(R.id.tvReUser) public TextView tvReUser;
    @Bind(R.id.tvRepostCount) public TextView tvRepostCount;
    @Bind(R.id.tvCommentCount) public TextView tvCommentCount;
    @Bind(R.id.tvLikeCount) public TextView tvLikeCount;
    @Bind(R.id.layDiver) public View layDiver;
    @Bind(R.id.imgAvatar) public SimpleDraweeView imgAvatar;
    @Bind(R.id.imgV) public ImageView imgV;

    @Bind({R.id.img1, R.id.img2,R.id.img3,
            R.id.img4,R.id.img5,R.id.img6,
            R.id.img7,R.id.img8,R.id.img9})
    public List<SimpleDraweeView> imgList;

    public CommentHolder(View itemView) {
        super(itemView);
        this.view = itemView;
        ButterKnife.bind(this, itemView);
        tvScreenName.setMovementMethod(LinkMovementMethod.getInstance());
        tvFrom.setMovementMethod(LinkMovementMethod.getInstance());
        tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        tvReUser.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
