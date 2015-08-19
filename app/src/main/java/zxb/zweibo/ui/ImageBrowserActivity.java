package zxb.zweibo.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import zxb.zweibo.GlobalApp;
import zxb.zweibo.R;
import zxb.zweibo.adapter.ImageBrowserAdapter;
import zxb.zweibo.bean.ImageBrowserBean;
import zxb.zweibo.bean.ImgBrowserWeiBoItem;
import zxb.zweibo.bean.PicUrls;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.ImageUtil;
import zxb.zweibo.listener.PageTracker;
import zxb.zweibo.ui.fragment.ImageBrowserFragment;


/**
 * 大图浏览的Activity.
 *
 * Created by rex on 15-8-11.
 */
public class ImageBrowserActivity extends FragmentActivity{

    @Bind(R.id.vpImgBrowser)
    ViewPager mViewPager;

    /**
     * 该条微博的内容.
     */
    private StatusContent mStatusContent;
    /**
     * ViewPager每页的内容.
     */
    private List<Fragment> mTabs;
    /**
     * 图像载入.
     */
    private ImageUtil mImgUtil;
    /**
     * 点击了哪张图片.
     */
    private int mPosition;

    /**
     * 底部导航条.
     */
    @Bind({R.id.vPage1, R.id.vPage2,R.id.vPage3,
                R.id.vPage4,R.id.vPage5,R.id.vPage6,
                R.id.vPage7,R.id.vPage8,R.id.vPage9})
    List<View> navigatorBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftl_image_browser_activity);
        ButterKnife.bind(this);
        // 自定义Application
        GlobalApp app = (GlobalApp) getApplication();
        mImgUtil = app.getmImageUtil();

        initView();
    }

    /**
     * 检查传入的参数，需要传入微博里面的图片地址对象.
     * 若对象为空，则关闭
     */
    private void checkParams() {
        ImgBrowserWeiBoItem imgItem = EventBus.getDefault().getStickyEvent(ImgBrowserWeiBoItem.class);
        this.mStatusContent = imgItem.getSc();
        this.mPosition = imgItem.getPosition();

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
    public void initView() {

        mTabs = new ArrayList<>();

        checkParams();
        initDatas();
        initEvents();
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

    private void initEvents(){
        initPages();
        ImageBrowserAdapter mAdapter = new ImageBrowserAdapter(getSupportFragmentManager(), mTabs);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new PageTracker() {
            @Override
            public void resetState(int position) {
                reset();
                setSelect(position);
            }
        });

        mViewPager.setCurrentItem(mPosition);
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

    // ---------------底部进度条的操作---------------------

    /**
     * 初始化进度条颜色
     */
    public void initPages(){
        reset();
        setSelect(mPosition);
        reSizePages(mTabs);
    }

    /**
     * 设定哪个被选中.
     *
     * @param pos position
     */
    public void setSelect(int pos){
        navigatorBar.get(pos).setBackgroundColor(0xFF4271B2);
    }

    /**
     * 重围进度条颜色
     */
    public void reset(){
        for (View view : navigatorBar){
            view.setBackgroundColor(0xFFFFFFFF);
        }
    }

    /**
     * 把不需要的隐藏，使用weight属性会自动适应屏幕宽度.
     *
     * @param mTabs 每页的内容
     */
    public void reSizePages(List<Fragment> mTabs) {
        for (int i=mTabs.size(); i<9; i++){
            navigatorBar.get(i).setVisibility(View.GONE);
        }
    }

    // 底部进度条
}
