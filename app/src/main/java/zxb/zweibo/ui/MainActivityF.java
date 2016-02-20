package zxb.zweibo.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import zxb.zweibo.GlobalApp;
import zxb.zweibo.R;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.Utils.Snack;
import zxb.zweibo.common.AccessTokenKeeper;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.service.CheckUpdateIntentService;
import zxb.zweibo.ui.fragment.FTLMVP;
import zxb.zweibo.ui.fragment.FavFragment;
import zxb.zweibo.ui.fragment.view.IFTLView;
import zxb.zweibo.widget.AppManager;

/**
 * Created by rex on 15-8-27.
 */
public class MainActivityF extends BasicActivity {

    @Bind(R.id.container)
    FrameLayout mContainer;

    @Bind(R.id.dl_main_drawer)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nv_main_navigation)
    NavigationView mNavigation;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private FragmentManager mFragmentManager;

//    @Bind(R.id.toolbar)Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		Fabric.with(this, new Crashlytics());
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        WeiboAPIUtils.initWeiboAPI();

        initMenu();
        initContiner();
    }

    private void initContiner() {
        mFragmentManager = getSupportFragmentManager();
//        replaceFragment(FTLFragmentNew.newInstance(MainActivityF.this));
//        FTLFragmentMVP ftl = FTLFragmentMVP.newInstance(MainActivityF.this);
        FTLMVP ftl = FTLMVP.newInstance(MainActivityF.this);
        mFragment = ftl;
        replaceFragment(ftl);
    }

    private void replaceFragment(Fragment fragment) {
        mFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private long preClickTime;
    private void initMenu() {
        mToolbar.setTitle("最新微博");

        /*mToolbar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                refreshList();
                return false;
            }
        });*/
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - preClickTime > 1000) {
                    preClickTime = currentTime;
                } else {
                    refreshList();
                }
            }
        });

        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // 菜单开关的动画
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        NavigationView navigationView =
                (NavigationView) findViewById(R.id.nv_main_navigation);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    private void refreshList() {
//        mFragment.refresh();
        mFragment.toTop();
    }

    private void init() {

    }

    IFTLView mFragment;

    /**
     * 左拉菜单项的点击事件监听
     *
     * @param navigationView
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        int itemId = menuItem.getItemId();
                        switch (itemId) {
                            case R.id.nav_home:
                                Logger.i("nav_home");
                                mToolbar.setTitle("最新微博");
//                            replaceFragment(FTLFragmentNew.newInstance(MainActivityF.this));
//                                FTLFragmentMVP ftl = FTLFragmentMVP.newInstance(MainActivityF.this);
                                FTLMVP ftl = FTLMVP.newInstance(MainActivityF.this);
                                mFragment = ftl;
                                replaceFragment(ftl);
                                break;
                            case R.id.nav_fav:
                                mToolbar.setTitle("我的收藏");
//                                replaceFragment(FavoritesFragment.newInstance(MainActivityF.this));
//                                FavoritesFragmentMVP fav = FavoritesFragmentMVP.newInstance(MainActivityF.this);
                                FavFragment fav = FavFragment.newInstance(MainActivityF.this);
                                mFragment = fav;
                                replaceFragment(fav);
                                break;
                            case R.id.nav_my:
                                Snack.show(mDrawerLayout, "即将上线，敬请期待");
                                break;
                            /*case R.id.nav_like:
                                Snack.show(mDrawerLayout, "即将上线，敬请期待");
                                break;*/
                            case R.id.nav_logout:
                                Snack.show(mDrawerLayout, "注销");
                                AccessTokenKeeper.clear(GlobalApp.getInstance());
                                AppManager.getAppManager().AppExit();
                                break;
                            case R.id.nav_android:
                                Snack.show(mDrawerLayout, "即将上线，敬请期待");
                                break;
                            case R.id.nav_ios:
                                Snack.show(mDrawerLayout, "即将上线，敬请期待");
                                break;
                        }
                    /*if (itemId == R.id.nav_home) {
                        Logger.i("nav_home");
                        replaceFragment(FTLFragment.newInstance(MainActivityF.this));
                    } else if (itemId == R.id.nav_fav){
                        replaceFragment(FavoritesFragment.newInstance(MainActivityF.this));
                    }*/

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelNotifi();
        Intent updateIntent = new Intent(this, CheckUpdateIntentService.class);
        updateIntent.putExtra(CheckUpdateIntentService.STOP_SERVICE, true);
        startService(updateIntent);
        Logger.i("MainActivityF onDestory");
    }

    private void cancelNotifi() {
        NotificationManager notifi =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifi.cancelAll();
    }
}
