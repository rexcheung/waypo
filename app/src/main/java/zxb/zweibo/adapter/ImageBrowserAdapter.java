package zxb.zweibo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by rex on 15-8-11.
 */
public class ImageBrowserAdapter extends FragmentPagerAdapter {

    List<Fragment> mTabs;

    public ImageBrowserAdapter(FragmentManager fm, List<Fragment> mTabs) {
        super(fm);
        this.mTabs = mTabs;
    }

    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }


}
