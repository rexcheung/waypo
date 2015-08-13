package zxb.zweibo.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import zxb.zweibo.MyApplication;
import zxb.zweibo.R;
import zxb.zweibo.adapter.ImageBrowserAdapter;
import zxb.zweibo.bean.ImageBrowserBean;
import zxb.zweibo.bean.PicUrls;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.Constants;
import zxb.zweibo.common.ImageUtil;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.listener.WeiboRequestListener;
import zxb.zweibo.ui.fragment.ImageBrowserFragment;

import static zxb.zweibo.R.id.vpImgBrowser;

/**
 * Created by rex on 15-8-11.
 */
public class ImageBrowserActivity extends FragmentActivity{

    ViewPager mViewPager;
     StatusContent mStatusContent;
    List<Fragment> mTabs;
    ImageUtil mImgUtil;

    ImageBrowserAdapter mAdapter;

    private Oauth2AccessToken mAccessToken;
//    private WeiboAPIUtils mWeiboAPI;
//    private Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mImgUtil = new ImageUtil(this);

        // 自定义Application
        MyApplication app = (MyApplication) getApplication();
        mImgUtil = app.getmImageUtil();

        initView();
        checkParams();
        initWeibo();
        initDatas();
        initEvents();
//        requestJson();
//        testRequest();
    }

    private void initDatas() {
        List<String> smallPicUrls = getSmallPicUrls();
        List<String> middlePicUrls = getMiddlePicUrls(smallPicUrls);

        int size = smallPicUrls.size();
        for (int i=0; i<size; i++){
            ImageBrowserBean img = new ImageBrowserBean(smallPicUrls.get(i), middlePicUrls.get(i));
            img.setImgUtil(mImgUtil);
            EventBus.getDefault().postSticky(img);
            ImageBrowserFragment f = new ImageBrowserFragment();

            mTabs.add(f);
        }

        smallPicUrls.clear();
        middlePicUrls.clear();
    }

    private void initWeibo() {
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
//        mWeiboAPI = new WeiboAPIUtils(this, Constants.APP_KEY, mAccessToken);
    }

    /**
     * 检查传入的参数，需要传入微博里面的图片地址对象.
     * 若对象为空，则关闭
     */
    private void checkParams() {
        mStatusContent = EventBus.getDefault().getStickyEvent(StatusContent.class);
        if (mStatusContent == null){
            Toast.makeText(this, "参数不正确", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 获取该微博小图片的地址列表.
     * @return 如果无则返回List.size()为0
     */
    private List<String> getSmallPicUrls(){
        List<String> smallPicUrls = new ArrayList<>();
        // 若有原创微博则获取微创的图片地址
        if (mStatusContent.getRetweeted_status() == null){
            for (PicUrls url : mStatusContent.getPic_urls()){
                smallPicUrls.add(url.getThumbnail_pic());
            }

            // 转发则获取转发地址
        } else {
            for (PicUrls url : mStatusContent.getRetweeted_status().getPic_urls()){
                smallPicUrls.add(url.getThumbnail_pic());
            }
        }

        return smallPicUrls;
    }

    /**
     * 获取中等尺寸的图片列表
     * @param smallUrList 小图片列表
     * @return size为0时表示操作不正常
     */
    private List<String> getMiddlePicUrls(List<String> smallUrList){
        final String THUMB = "/thumbnail";
        final String MIDDLE = "/bmiddle";

        List<String> resultList = new ArrayList<>();
        for (String url : smallUrList){
            String[] strArr = url.split(THUMB);

            resultList.add(strArr[0] + MIDDLE + strArr[1]);
        }
        return resultList;
    }

    /**
     * 初始化页面控件
     */
    private void initView() {
        setContentView(R.layout.ftl_image_browser_activity);

        mViewPager = (ViewPager) findViewById(vpImgBrowser);

        mTabs = new ArrayList<>();
    }

    private void initEvents(){
        mAdapter = new ImageBrowserAdapter(getSupportFragmentManager(), mTabs);
        mViewPager.setAdapter(mAdapter);
    }

    public void refreshDatas(){
        mStatusContent = EventBus.getDefault().getStickyEvent(StatusContent.class);
        mTabs.clear();
        initDatas();
        mAdapter.notifyDataSetChanged();
        this.setVisible(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mImgUtil.clearMemoryCache();
//        mImgUtil.destory();
//        mImgUtil = null;
//        mTabs.clear();
//        mTabs = null;

    }
}
