package zxb.zweibo.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import zxb.zweibo.GlobalApp;
import zxb.zweibo.R;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.adapter.FTimeLinsAdapter;
import zxb.zweibo.bean.EAuth;
import zxb.zweibo.bean.FTLIds;
import zxb.zweibo.bean.LastWeibo;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.JsonCacheUtil;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.listener.OnBottomListener;
import zxb.zweibo.service.CheckUpdateService;
import zxb.zweibo.widget.AppManager;

/**
 * 展示所有关注人的最新微博.
 * 步骤:
 * 首先通过SDK获取最近100条微博的IDS，通过ID从缓存中获取最近10条微博，
 * 如果没有缓存，再通过SDK获取具体的JSON，
 * 每次都请求10条微博，此页面最多显示100条微博。
 * 如果一开始没有网络，获取不了IDS，则会把缓存所有微博展示出来.
 */
public class MainActivityNew extends AppCompatActivity {

    private CharSequence mTitle;

    @Bind(R.id.dl_main_drawer) DrawerLayout mDrawerLayout;

    @Bind(R.id.swipeRefreshLayout) SwipeRefreshLayout mSwipe;

    //--------------------------------------------------

    /**
     * 初始化时传入的父类Activity, LayoutInflater需要使用
     */
//    private static Activity mContext;
    private static int mPosition;

    /**
     * 新浪SDK.
     */
    private Oauth2AccessToken mAccessToken;
    //    private StatusesAPI mStatusesAPI;
    private WeiboAPIUtils mWeiboAPI;
    /**
     * 接收最近10条微博的实体类.
     */
//    private FTimeLine mFTimeLine;

    /**
     * 实体类中的10条微博.
     */
    private List<StatusContent> mStatusesList;

    private String TAG;

    private LinearLayoutManager llm;
    private RecyclerView mRecyclerView;

//    private int currentPage;

    private FTimeLinsAdapter mAdapter;

    /**
     * 是否在初始化.
     */
    private boolean isInit = true;

    /**
     * 判断是否正在刷新列表.
     */
    private boolean isRefresing = false;

    private List<Long> mIds;

    private JsonCacheUtil mJsonUtil;

    private int PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
//        startService(new Intent(this, CheckNewWeiboService.class));
        Logger.i("CurrentThread = " + Thread.currentThread());
        init();

    }

    //-------------------------------初始化---------------------------------
    private void init() {
        initMenu();
        mTitle = getTitle();

        llm = new LinearLayoutManager(this);

        TAG = getClass().getSimpleName();

        mAccessToken = AccessTokenKeeper.readAccessToken(this);

        GlobalApp app = (GlobalApp) getApplication();
        mWeiboAPI = app.getWeiboUtil();
//        mWeiboAPI = new WeiboAPIUtils(this, Constants.APP_KEY, mAccessToken);

        mRecyclerView = (RecyclerView) findViewById(R.id.listContent);
        mRecyclerView.setLayoutManager(llm);

        mJsonUtil = new JsonCacheUtil(this, mAccessToken, mWeiboAPI);

        mStatusesList = new ArrayList<>();
        mIds = new ArrayList<>();

        if(isInit){
            requestIds();
        }

        mSwipe.setOnRefreshListener(mRefreshSwipe);

        AppManager.getAppManager().addActivity(this);
    }

    private void initEvents(){
        GlobalApp app = (GlobalApp) getApplication();
        mAdapter = FTimeLinsAdapter.newInstance(this, mStatusesList, app.getmImageUtil());

        mRecyclerView.setAdapter(mAdapter);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mRecyclerView.setNestedScrollingEnabled(true);
        }*/

        mRecyclerView.setOnScrollListener(new OnBottomListener(llm) {
            @Override
            public void onBottom() {
                if (!isRefresing) {
                    loadMore();
                    Logger.i("Now refreshing...");
                }

                Logger.i( "Bttom");
            }
        });

        
    }

    /**
     * 初始化顶部ToolBar与侧滑菜单.
     */
    private void initMenu(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("最新微博");

        toolbar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                mSwipe.setRefreshing(true);
//                Snackbar.make(mRecyclerView, "LongClick", Snackbar.LENGTH_SHORT).show();
                refreshList();
                return false;
            }
        });
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // 菜单开关的动画
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        NavigationView navigationView =
                (NavigationView) findViewById(R.id.nv_main_navigation);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        /*toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_share:
                        Toast.makeText(MainActivityNew.this, "Click", Toast.LENGTH_SHORT).show();
                        Log.i("Click", "Click");
                        break;
                }
                return true;
            }
        });*/
    }

    //-------------------------------初始化---------------------------------

    //-------------------------------监听---------------------------------
    /**
     * 左拉菜单项的点击事件监听
     * @param navigationView
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.nav_home) {
                            Logger.i( "nav_home");
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

//        getMenuInflater().inflate(R.menu.toolbar_main_activity, menu);
        return true;
    }

    /**
     * ToolBar项目的点击
     * @param item item
     * @return default return;
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
//        return false;
    }

    /**
     * 刷新监听器.
     */
    SwipeRefreshLayout.OnRefreshListener mRefreshSwipe = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
//            Toast.makeText(MainActivityNew.this, "Refresh", Toast.LENGTH_SHORT).show();
            Snackbar.make(mSwipe, "Refreshing", Snackbar.LENGTH_SHORT).show();
//            requestIds();
            refreshList();
        }
    };

    //-------------------------------监听---------------------------------


    //----------------------------------IDS请求------------------------------------------------
//    private int idsPage;
    /**
     * 获取最新的N条微博ID
     */
    private void requestIds(){
        long lastIds = 0;
        if (!mSwipe.isRefreshing()){
            if (mIds.size()!=0) {
                lastIds = mIds.get(mIds.size() - 1);
            }
        }
        mWeiboAPI.imageFTLIds(0L, lastIds > 0 ? lastIds - 1 : 0, 100, 1, false, 0, idsListener);
    }

    Gson mGson = new Gson();
    private RequestListener idsListener = new RequestListener() {
        @Override
        public void onComplete(String json) {
            FTLIds tempIds = mGson.fromJson(json, FTLIds.class);
            if(tempIds!=null){
                if(tempIds.getStatuses().size() != 0){
                    mIds.clear();
                    mIds.addAll(tempIds.getStatuses());
                    //3877868491180738 3877868419452707
                    if (mSwipe.isRefreshing()){
                        mStatusesList.clear();
                        mSwipe.setRefreshing(false);
                    }

//                    Logger.i(mIds.get(0));
                    loadMore();

                } else {
                    Snackbar.make(mSwipe, "暂时没更新，休息下吧骚年", Snackbar.LENGTH_SHORT).show();
                    mSwipe.setRefreshing(false);
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Logger.i( e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(MainActivityNew.this, info != null ? info.toString() : "", Toast.LENGTH_LONG).show();
            mStatusesList = mJsonUtil.readCache(mAccessToken.getUid());
            if (isInit) {
                isInit = false;
                initEvents();
                mAdapter.notifyDataSetChanged();
            }
            if (mSwipe.isRefreshing()) mSwipe.setRefreshing(false);
        }
    };

    /**
     * 从缓存中获取已有的微博，由缓存判断是否需要联网获取数据
     */
    private void loadMore(){
        isRefresing = true;
        long lastId;
        if (mStatusesList.size() > 0){
            lastId = mStatusesList.get(mStatusesList.size()-1).getId();
        } else {
            lastId = mIds.get(0);
        }
//        mJsonUtil.getCacheFrom(mStatusesList, mIds, lastId, PAGE_SIZE, mCacheListener);

        //3877867483611773
        List<Long> tempList = getPageItems(mIds, lastId, PAGE_SIZE);
        if (tempList!=null){
            mJsonUtil.getCacheFrom(mStatusesList, tempList, mCacheListener);
        }

        restartService();
    }

    private void restartService() {
//        stopService(new Intent(getApplicationContext(), CheckUpdateService.class) );
        EventBus.getDefault().getStickyEvent(LastWeibo.class);
        EventBus.getDefault().postSticky(new LastWeibo(mIds.get(0)));
        startService(new Intent(getApplicationContext(), CheckUpdateService.class));
    }

    JsonCacheUtil.CacheListener mCacheListener = new JsonCacheUtil.CacheListener(){
        @Override
        public void OnCacheComplete() {
            if(isInit){
                initEvents();
                isInit = false;
            }
//            List<StatusContent> testList = mStatusesList;
            isRefresing = false;
            mAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 获取需要ITEM的ID列表
     * @param idList IDS总表
     * @param fromId 从哪个ID开始
     * @param page_size 加载多少个ID
     * @return 正常会返回List，否则返回null.
     */
    private List<Long> getPageItems(List<Long> idList, long fromId, int page_size) {
        int index = getIndex(idList, fromId);
        if (index == idList.size()-1 || index < 0) {
            Logger.i("已经没有更多数据了");
            return null;
        }

        // 需要从画面上最后一条数据之后的一条开始查询，否则会出现重复
        return getTargetList(idList, index == 0 ? 0 : index+1, page_size);
    }

    /**
     * 需要获取缓存的IDS
     * @param idList idList
     * @param index 下标
     * @param num 多少个
     * @return List
     */
    private List<Long> getTargetList(List<Long> idList, int index, int num) {
        List<Long> targetList = new ArrayList<>();
        int target = index+num;
        if(target > idList.size()){
            target = idList.size();
        }
        for (int i=index; i<target; i++){
            if (i == 50){
                Logger.i("");
            }
            targetList.add(idList.get(i));
        }
        return targetList;
    }

    /**
     * 获取指定ID在idList里面的下标.
     * @param idList list
     * @param fromId from
     * @return 返回下标，没有，错误或最后一条则返回-1
     */
    private int getIndex(List<Long> idList, long fromId) {
        int size = idList.size();
        if(fromId == idList.get(0).longValue()){
            return 0;
        } else if(fromId == idList.get(idList.size()-1).longValue()){
            return -1;
        }

        for (int i = 0; i < size; i++) {
            if (idList.get(i) == fromId) {
                return i;
            }
        }
        return -1;
    }

    //----------------------------------IDS请求------------------------------------------------



    /**
     * EventBus回调函数，当授权成功后即调用此方法
     * @param auth 回调的对象
     */
    public final void onEventMainThread(final EAuth auth) {
        if(EAuth.SUCCESS == auth.getCode()){
            Toast.makeText(this, "MainActivity.Success", Toast.LENGTH_SHORT).show();
        }
    }

    //-------------------------------生命周期---------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        mJsonUtil.initDB();
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        super.onPause();
        if (mAdapter != null) {
            mAdapter.cleanCache();
        }

        if(mJsonUtil != null){
            mJsonUtil.closeDB();
        }
        Logger.i("onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        stopService(new Intent(this, CheckNewWeiboService.class));
        cancelNotifi();
        stopService(new Intent(getApplicationContext(), CheckUpdateService.class));
    }
    //-------------------------------生命周期---------------------------------

    public void refreshList(){
        mSwipe.setRefreshing(true);
        requestIds();
        cancelNotifi();
    }

    private void cancelNotifi(){
        NotificationManager notifi =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifi.cancelAll();
    }
}
