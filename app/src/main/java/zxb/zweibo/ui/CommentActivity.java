package zxb.zweibo.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import zxb.zweibo.R;
import zxb.zweibo.Utils.Snack;
import zxb.zweibo.adapter.CommentAdapter;
import zxb.zweibo.bean.CommentJson;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.common.WeiboAPIUtils;
import zxb.zweibo.presenter.CommentPresenter;
import zxb.zweibo.ui.view.IComment;
import zxb.zweibo.widget.AppManager;

/**
 * 查看指定微博的评论.
 *
 * Created by rex on 15-8-20.
 */
public class CommentActivity extends AppCompatActivity implements IComment{

    StatusContent sc;

    CommentAdapter mAdapter;

    List<CommentJson> mCommentList;

    CommentPresenter mPresenter;

    private boolean init = true;

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Bind(R.id.listComment)RecyclerView mRecyclerView;

    LinearLayoutManager llm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        setContentView(R.layout.comment_activity);
        ButterKnife.bind(this);
        sc = EventBus.getDefault().getStickyEvent(StatusContent.class);

        mPresenter = new CommentPresenter(this, sc);
        mPresenter.requestComments();
        initMenu();

        llm = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(llm);

        AppManager.getAppManager().addActivity(this);
    }

    private void initMenu(){
        toolbar.setTitle("Comments");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new Toolbar.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.favorite:
                        mPresenter.add2Favorite();
                        break;
                    case R.id.forward:
                        mPresenter.forward("");
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_comment_activity, menu);
        return true;
    }

    private void initAdapter() {
        mAdapter = CommentAdapter.newInstance(this, mCommentList, sc);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        AppManager.getAppManager().finishActivity();
    }

    //---------------------------Interface override method-----------------------------------
    @Override
    public void close() {
        AppManager.getAppManager().finishActivity();
    }

    @Override
    public void favoriteResponse(int responseCode) {
        if (responseCode == IComment.FAVORUTE_SUCCESS){
            Snack.show(mRecyclerView, "收藏成功");
        } else if (responseCode == IComment.FAVORUTE_FAIL){
            Snack.show(mRecyclerView, "收藏过了");
        }
    }

    @Override
    public void fowardResponse(int response) {
        if (response == IComment.FORWARD_SUCCESS){
            Snack.show(mRecyclerView, "转发成功");
        } else if (response == IComment.FORWARD_FAIL){
            Snack.show(mRecyclerView, "转发过了");
        }
    }

    @Override
    public void updateList(List<CommentJson> commentList) {
//        initAdapter();
        this.mCommentList = commentList;
        if (init){
            initAdapter();
            init = false;
        }
    }
}
