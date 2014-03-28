package mu.aho.read.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by ayumusato on 3/28/14.
 */
public class CategoriesPagerAdapter extends FragmentPagerAdapter {
    private final String TAG = getClass().getSimpleName();

    private ArrayList<Fragment> fragments;

    public CategoriesPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}