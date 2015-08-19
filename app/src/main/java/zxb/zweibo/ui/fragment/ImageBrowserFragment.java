package zxb.zweibo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.greenrobot.event.EventBus;
import zxb.zweibo.R;
import zxb.zweibo.bean.ImageBrowserBean;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.ImageUtil;
import zxb.zweibo.common.VolleyHelper;

/**
 * 大图浏览里面ViewPager包含的每个图片为一个Fragment
 * Created by rex on 15-8-11.
 */
public class ImageBrowserFragment extends Fragment{

    ImageBrowserBean mBean;
//    StatusContent

    private ImageUtil mImageUtil;

    View view;

    public ImageBrowserFragment() {
        ImageBrowserBean stickyEvent = EventBus.getDefault().getStickyEvent(ImageBrowserBean.class);
        this.mBean = stickyEvent;
        mImageUtil = stickyEvent.getImgUtil();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.item_img_browser, null);
        ImageView img = (ImageView) view.findViewById(R.id.imgContent);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mImageUtil.showImage(img, mBean.getMiddlePic());

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
