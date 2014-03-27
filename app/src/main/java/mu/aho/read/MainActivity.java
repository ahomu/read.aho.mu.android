package mu.aho.read;

import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import mu.aho.read.view.MyTab;

// @see http://davidjkelley.net/?p=34
// @see http://just-another-blog.net/programming/how-to-implement-horizontal-view-swiping-with-tabs/
// @see http://k-1-ne-jp.blogspot.jp/2013/11/fragmenttabhost.html
// @see http://yyyank.blogspot.jp/2013/07/androidapisummarize-deprecated-class.html
// @see http://stackoverflow.com/questions/17687717/tutorial-to-implement-the-use-of-tabhost-in-android-2-2-viewpager-and-fragment

// TODO ライブラリ使ってもよかったらこっちのが妥当そうだ･･･。
// @see https://github.com/astuetz/PagerSlidingTabStrip
// @see http://developer.android.com/samples/SlidingTabsBasic/project.html
/**
 * Created by ahomu on 3/26/14.
 */
public class MainActivity extends FragmentActivity implements OnTabChangeListener, OnPageChangeListener {

    ViewPager mViewPager;
    FragmentTabHost mTabHost;
    HorizontalScrollView mScroller;
    CategoriesAdapter pageAdapter;
    private String[] TabTag = { "hogeeeeeeee", "fugaaaaa", "piyooooo", "higeeeeeeeeeee" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowHomeEnabled(false);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mScroller = (HorizontalScrollView) findViewById(R.id.scroller);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        List<Fragment> fragments = new ArrayList<Fragment>();

        // fragments
        CategoryFragment a = CategoryFragment.newInstance("hoge", "red");
        CategoryFragment b = CategoryFragment.newInstance("fuga", "blue");
        CategoryFragment c = CategoryFragment.newInstance("piyo", "yellow");
        CategoryFragment d = CategoryFragment.newInstance("hige", "green");

        // tabs
        mTabHost.setup(this, getSupportFragmentManager(), R.id.content);
        for (int i = 0; i < TabTag.length; i++) {

            String tagName = TabTag[i];

            Log.d("tabSpec", TabTag[i]);

            Bundle bundle = new Bundle();
            bundle.putString("category", tagName);

            View view = new MyTab(this, tagName);
            TabSpec tabSpec = mTabHost.newTabSpec(tagName);
            tabSpec.setIndicator(view);

            // ここでのtabcontent用Fragmentは実際には使わないのでダミー
            mTabHost.addTab(tabSpec, Fragment.class, bundle);
        }
        mTabHost.setOnTabChangedListener(this);

        // view pager
        pageAdapter = new CategoriesAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setOnPageChangeListener(this);

        fragments.add(a);
        fragments.add(b);
        fragments.add(c);
        fragments.add(d);
        pageAdapter.notifyDataSetChanged();

    }

    public class CategoriesAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public CategoriesAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

    // Manages the Tab changes, synchronizing it with Pages
    public void onTabChanged(String tag) {
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // Manages the Page changes, synchronizing it with Tabs
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        int pos = this.mViewPager.getCurrentItem();
        this.mTabHost.setCurrentTab(pos);

        Log.v("log", mScroller.getWidth() + "");

        int delta = 0;
        for (int i = 0; i < pos; i++) {
            delta += this.mTabHost.getTabWidget().getChildAt(i).getWidth();
        }
        delta += (this.mTabHost.getTabWidget().getChildAt(pos).getWidth() / 2);
        delta -= (mScroller.getWidth() / 2);
        MyTab tab;

        for (int i = 0; i < 4; i++) {
            tab = (MyTab) this.mTabHost.getTabWidget().getChildAt(i);
            tab.setColor("red");
        }
        tab = (MyTab) this.mTabHost.getTabWidget().getChildAt(pos);
        tab.setColor("blue");

        this.mScroller.scrollTo(delta, 0);
    }
}
