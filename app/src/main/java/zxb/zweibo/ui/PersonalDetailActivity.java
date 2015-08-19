package zxb.zweibo.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zxb.zweibo.R;

/**
 * 展示某个人发送的微博.
 * 这个亦都作为作为DesignLib的使用试验.
 *
 * 使用CoordinatorLayout需要的步骤:
 * 1. 首先需要在module的 build.gradle加入支持库, compile 'com.android.support:design:22.2.1'
 * 2. XML中加入组件，注意，XML会有自动完成，不要用那个，会异常，要用完整的包加组件名，android.support.design.widget.CoordinatorLayout
 * 3. CoordinatorLayout 加入属性xmlns:app="http://schemas.android.com/apk/res-auto"
 * 4. CoordinatorLayout加入唯一子布局AppBarLayout，AppBarLayout加入CollapsingToolbarLayout，CollapsingToolbarLayout里面只有一个ImageView
 * 5. 设置属性：CollapsingToolbarLayout添加属性app:layout_scrollFlags="scroll|exitUntilCollapsed", ImageView加入app:layout_collapseMode="parallax"
 * 6. 在AppBarLayout外面加入可滚动的组件，NestedScrollView，RecyclerView或者ListView
 * 7. 加入属性app:layout_behavior="@string/appbar_scrolling_view_behavior"
 * 完成
 *
 * 参考文章: http://blog.csdn.net/eclipsexys/article/details/46349721
 * Created by rex on 15-8-19.
 */
public class PersonalDetailActivity extends AppCompatActivity {

    @Bind(R.id.collToolBar)CollapsingToolbarLayout collapsingToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personl_detail_activity);

        ButterKnife.bind(this);

//        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        //因为原来的APP THEME已经包含了ActionBar，所以再设置的时候会报抛异常
//        setSupportActionBar(toolbar);

        collapsingToolbar.setTitle("我的课程");
    }

    @OnClick(R.id.fab)
    public void fabClick(View view){
        Snackbar.make(view, "checkin success!", Snackbar.LENGTH_SHORT).show();
    }
}
