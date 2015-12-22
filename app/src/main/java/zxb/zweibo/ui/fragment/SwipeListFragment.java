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

import zxb.zweibo.R;
import zxb.zweibo.listener.OnBottomListener;

/**
 * 包含一个SwipeRefreshLayout与RecyclerView的基类
 * 继承此基类后需要重写
 * Created by rex on 15-12-20.
 */
public abstract class SwipeListFragment extends Fragment{

    protected Context mContext;
    protected View mView;
    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeLayout;
    protected LinearLayoutManager llm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.ftl_fragment, null);

        mRecyclerView = inject(R.id.listContent);
        llm = initLayoutManager();
        mRecyclerView.setLayoutManager(llm);
        // 设置底部加载事件
        mRecyclerView.setOnScrollListener(new OnBottomListener(llm) {
            @Override
            public void onBottom() {
                onBottomAction();
            }
        });

        // 下拉刷新
        mSwipeLayout = inject(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(mRefreshSwipe);


        initEvent();

        return mView;
    }

    /**
     * 下拉刷新监听器。
     */
    protected SwipeRefreshLayout.OnRefreshListener mRefreshSwipe
            = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            onSwipeRefresh();
        }
    };

    protected abstract LinearLayoutManager initLayoutManager();
    protected abstract void onSwipeRefresh();
    protected abstract void onBottomAction();
    protected abstract void initEvent();
    /**
     * 简化版的findViewById,
     * 自动进行类型转换。
     * @param viewId
     * @param <T>
     * @return
     */
    protected <T extends View> T inject(int viewId){
        if (mView == null){
            return null;
        }
        return (T)mView.findViewById(viewId);
    }
}
