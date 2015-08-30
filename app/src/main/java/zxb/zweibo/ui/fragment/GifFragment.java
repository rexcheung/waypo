package zxb.zweibo.ui.fragment;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import de.greenrobot.event.EventBus;
import zxb.zweibo.R;
import zxb.zweibo.bean.ImageBrowserBean;
import zxb.zweibo.common.ImageUtil;

/**
 * 大图浏览里面ViewPager包含的每个图片为一个Fragment
 * Created by rex on 15-8-11.
 */
public class GifFragment extends Fragment{

    ImageBrowserBean mBean;
//    StatusContent

//    private ImageUtil mImageUtil;

    View view;

    public GifFragment() {
        ImageBrowserBean stickyEvent = EventBus.getDefault().getStickyEvent(ImageBrowserBean.class);
        this.mBean = stickyEvent;
//        mImageUtil = stickyEvent.getImgUtil();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.item_gif_fragment, null);
        SimpleDraweeView img = (SimpleDraweeView) view.findViewById(R.id.imgContent);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        Uri uri = Uri.parse(mBean.getMiddlePic());
        img.setImageURI(uri);
//        mImageUtil.showImage(img, mBean.getMiddlePic());

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(true)
                .build();
        img.setController(controller);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
//        mImageUtil.removeFromMemory(mBean.getMiddlePic());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view = null;
//        mImageUtil.clearMemoryCache();
        mBean = null;

    }
}
