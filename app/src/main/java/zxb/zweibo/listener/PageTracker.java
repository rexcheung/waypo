package zxb.zweibo.listener;

import android.support.v4.view.ViewPager;

/**
 * Created by rex on 15-8-13.
 */
public abstract class PageTracker implements ViewPager.OnPageChangeListener {

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
//        Log.i("onPageSelected", String.valueOf(position));
        resetState(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        Log.i("onPageScrollStateChanged", String.valueOf(position));
    }

    public abstract void resetState(int position);

}