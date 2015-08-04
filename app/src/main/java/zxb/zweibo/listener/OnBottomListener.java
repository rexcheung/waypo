package zxb.zweibo.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * 滑动监听器，当到底部时会调用实现类的onBottom方法
 */
public abstract class OnBottomListener extends RecyclerView.OnScrollListener{

    private LinearLayoutManager llm;

    public OnBottomListener(LinearLayoutManager llm){
        this.llm = llm;
    }

    public abstract void onBottom();

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int firstVisible = llm.findFirstCompletelyVisibleItemPosition();
        int itemCount = llm.getItemCount();
        int visibleCount = recyclerView.getChildCount();

//        Log.i("onBottomListener", "firstVisible = " + firstVisible + ", itemCount = " + itemCount + ", visibleCount = " + visibleCount);

        if (firstVisible + visibleCount >= itemCount) {
            onBottom();
        }
    }
}
