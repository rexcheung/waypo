package zxb.zweibo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zxb.zweibo.R;
import zxb.zweibo.bean.StatusContent;
import zxb.zweibo.listener.OnBottomListener;

/**
 * Created by rex on 15-8-28.
 */
public abstract class WeiboFragment extends Fragment{

    @Bind(R.id.listContent)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeLayout;

    static Context mContext;

    LinearLayoutManager llm;
    List<StatusContent> mStatusesList;

    public WeiboFragment(Context context) {
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ftl_fragment, null);
        ButterKnife.bind(this, view);
        init();
        initEvent();
        initData();
        return view;
    }

    private void initEvent() {
//        GlobalApp app = (GlobalApp) mContext.getApplicationContext();
//        ImageUtil imageUtil = app.getmImageUtil();
//        mRecyclerView.setAdapter(FTimeLinsAdapter.newInstance(mContext, mStatusesList, imageUtil));
    }

    private void init() {
        llm = new LinearLayoutManager(mContext);
        mStatusesList = new ArrayList<>();

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setOnScrollListener(new OnBottomListener(llm) {
            @Override
            public void onBottom() {
                loadMore();
            }
        });

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    protected abstract void loadMore();
    protected abstract void refreshData();
    protected abstract void initData();
}
