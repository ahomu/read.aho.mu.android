package mu.aho.read;

import android.content.Intent;
import android.support.v4.app.*;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.os.Bundle;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TabWidget;
import mu.aho.read.loader.HttpAsyncTaskLoader;
import mu.aho.read.view.CategoryTabView;
import mu.aho.read.CategoryFragment.OnArticleSelectedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class MainActivity extends FragmentActivity
        implements LoaderCallbacks<JSONObject>, OnTabChangeListener, OnPageChangeListener, OnArticleSelectedListener {

    private final String TAG = getClass().getSimpleName();

    ViewPager mViewPager;
    FragmentTabHost mTabHost;
    HorizontalScrollView mScroller;
    LoaderManager mLoaderManager;
    CategoriesAdapter pageAdapter;
    List<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load categories
        mLoaderManager = getSupportLoaderManager();
        mLoaderManager.initLoader(0, null, this);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mScroller = (HorizontalScrollView) findViewById(R.id.scroller);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());

        // tabs
        mTabHost.setup(this, getSupportFragmentManager(), R.id.content);
        mTabHost.setOnTabChangedListener(this);

        // view pager
        pageAdapter = new CategoriesAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setOnPageChangeListener(this);

        addTabAndFragment("ALL FEEDS", "http://read.aho.mu/top/index.json");
        pageAdapter.notifyDataSetChanged();
    }

    public void addTabAndFragment(String categoryName, String entriesUrl) {
        CategoryFragment frag = CategoryFragment.newInstance(categoryName, entriesUrl);
        CategoryTabView tabView = new CategoryTabView(this, categoryName);

        Bundle bundle = new Bundle();
        bundle.putString("category", categoryName);

        TabSpec tabSpec = mTabHost.newTabSpec(categoryName);
        tabSpec.setIndicator(tabView);

        // ここでのtabcontent用Fragmentは実際には使わないのでダミー
        mTabHost.addTab(tabSpec, Fragment.class, bundle);
        fragments.add(frag);
    }

    public void onArticleSelected(String url, String title) {
        Log.d(TAG, url);
        final Intent intent = new Intent(MainActivity.this, BrowseActivity.class);

        intent.putExtra("url", url);
        intent.putExtra("title", title);
        animatedStartActivity(intent);
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

    @Override
    public void onTabChanged(String tag) {
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        // FIXME 処理がゴリゴリすぎだぃ
        TabWidget tabWidget = this.mTabHost.getTabWidget();

        int pos = this.mViewPager.getCurrentItem();
        this.mTabHost.setCurrentTab(pos);

        int delta = 0;
        for (int i = 0; i < pos; i++) {
            delta += tabWidget.getChildAt(i).getWidth();
        }
        delta += (tabWidget.getChildAt(pos).getWidth() / 2);
        delta -= (mScroller.getWidth() / 2);
        CategoryTabView tab;

        int iz = tabWidget.getChildCount();
        for (int i = 0; i < iz; i++) {
            tab = (CategoryTabView) tabWidget.getChildAt(i);
            tab.setBackgroundColor("black");
        }
        tab = (CategoryTabView) tabWidget.getChildAt(pos);
        tab.setBackgroundColor("blue");

        this.mScroller.scrollTo(delta, 0);
    }

    @Override
    // @see http://stackoverflow.com/questions/10321712/loader-doesnt-start-after-calling-initloader
    // @see http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        AsyncTaskLoader loader;
        switch (id) {
            case 0:
                Log.d(TAG, "ON CREATE LOADER :" + "http://read.aho.mu/categories.json");
                loader = new HttpAsyncTaskLoader(this, "http://read.aho.mu/categories.json");
                return loader;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject result) {
        Log.d(TAG, "onLoadFinished(੭ु˵＞ヮ＜)੭ु⁾⁾");
        Log.d(TAG, result.toString());
        try {
            JSONArray categories = result.getJSONArray("categories");
            JSONObject category;
            Integer iz = categories.length();
            for (int i = 0; i < iz; i++) {
                category = categories.getJSONObject(i);
                addTabAndFragment(
                        category.getString("name").toUpperCase(),
                        "http://read.aho.mu/categories/" + category.getString("id") + ".json"
                );
                Log.d(TAG, category.getString("name") + "Added");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        pageAdapter.notifyDataSetChanged();

        // TODO カテゴリーは起動時に1回読んだら、あとはsavedInstanceで振り回そうと思う次第
        // FIXME ので、ここでasyncLoaderを使い捨てるけど、よくない感じがするー
        getSupportLoaderManager().destroyLoader(0);
    }

    @Override
    public void onLoaderReset(Loader<JSONObject> loader) {}

    @Override
    protected void onResume() {
        ActivitySwitcher.animationIn(findViewById(android.R.id.tabhost), getWindowManager());
        super.onResume();
    }

    private void animatedStartActivity(final Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ActivitySwitcher.animationOut(findViewById(android.R.id.tabhost),
                getWindowManager(),
                new ActivitySwitcher.AnimationFinishedListener() {
                    @Override
                    public void onAnimationFinished() {
                        startActivity(intent);
                    }
                });
    }
}
