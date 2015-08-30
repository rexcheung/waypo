package zxb.zweibo.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.MissingResourceException;

import butterknife.Bind;
import butterknife.ButterKnife;
import zxb.zweibo.R;
import zxb.zweibo.Utils.Logger;
import zxb.zweibo.Utils.Snack;
import zxb.zweibo.ui.fragment.FTLFragment;
import zxb.zweibo.ui.fragment.FTimeLinsFragment;
import zxb.zweibo.ui.fragment.FavoritesFragment;
import zxb.zweibo.widget.AppManager;

/**
 * Created by rex on 15-8-27.
 */
public class MainActivityF extends AppCompatActivity{

    @Bind(R.id.container) FrameLayout mContainer;

    @Bind(R.id.dl_main_drawer) DrawerLayout mDrawerLayout;

    @Bind(R.id.nv_main_navigation) NavigationView mNavigation;

    private FragmentManager mFragmentManager;

//    @Bind(R.id.toolbar)Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        initMenu();
        initContiner();
        AppManager.getAppManager().addActivity(this);
    }

    private void initContiner() {
        mFragmentManager = getSupportFragmentManager();
//        mFragmentManager.beginTransaction()
//                .replace(R.id.container, FTLFragment.newInstance(MainActivityF.this))
//                .commit();
        replaceFragment(FTLFragment.newInstance(MainActivityF.this));
    }

    private void replaceFragment(Fragment fragment){
        mFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
    }

    private void initMenu() {
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
    }

    private void refreshList() {

    }

    private void init() {

    }

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
                    switch (itemId){
                        case R.id.nav_home:
                            Logger.i("nav_home");
                            replaceFragment(FTLFragment.newInstance(MainActivityF.this));
                        break;
                        case R.id.nav_fav:
                            replaceFragment(FavoritesFragment.newInstance(MainActivityF.this));
                        break;
                        case R.id.nav_my:
                            Snack.show(mDrawerLayout, "即将上线，敬请期待");
                            break;
                        case R.id.nav_like:
                            Snack.show(mDrawerLayout, "即将上线，敬请期待");
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
}
