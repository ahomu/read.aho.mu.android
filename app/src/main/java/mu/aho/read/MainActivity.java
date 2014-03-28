package mu.aho.read;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.*;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import java.util.ArrayList;
import java.util.HashMap;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TabWidget;
import android.widget.Toast;
import mu.aho.read.adapter.CategoriesPagerAdapter;
import mu.aho.read.loader.HttpAsyncTaskResult;
import mu.aho.read.loader.JsonHttpAsyncTaskLoader;
import mu.aho.read.transformer.SlidePageTransformer;
import mu.aho.read.view.CategoryTabView;
import mu.aho.read.CategoryFragment.OnArticleSelectedListener;

/**
 * Created by ahomu on 3/26/14.
 */
public class MainActivity extends FragmentActivity
        implements LoaderCallbacks<HttpAsyncTaskResult<HashMap>>, OnTabChangeListener, OnPageChangeListener, OnArticleSelectedListener {

    private final String TAG = getClass().getSimpleName();

    private ViewPager mViewPager;
    private FragmentTabHost mTabHost;
    private HorizontalScrollView mScroller;
    private LoaderManager mLoaderManager;
    private CategoriesPagerAdapter mPageAdapter;
    private View mDivider;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // load categories
        mLoaderManager = getSupportLoaderManager();
        mLoaderManager.initLoader(0, null, this);

        mViewPager   = (ViewPager) findViewById(R.id.pager);
        mTabHost     = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mScroller    = (HorizontalScrollView) findViewById(R.id.scroller);
        mDivider     = findViewById(R.id.tab_divider);
        mPageAdapter = new CategoriesPagerAdapter(getSupportFragmentManager(), fragments);

        // tabs
        mTabHost.setup(this, getSupportFragmentManager(), R.id.content);
        mTabHost.setOnTabChangedListener(this);

        // view pager
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setPageTransformer(true, new SlidePageTransformer());

        addTabAndFragment("ALL FEEDS", "http://read.aho.mu/top/index.json", "#FFFFFF");
        ((CategoryTabView) mTabHost.getTabWidget().getChildAt(0)).setActiveColor();
        mPageAdapter.notifyDataSetChanged();
    }

    public void addTabAndFragment(String categoryName, String entriesUrl, String colorHex) {
        Bundle args = new Bundle();
        args.putString("category", categoryName);
        args.putString("color", colorHex);

        CategoryFragment frag = CategoryFragment.newInstance(categoryName, entriesUrl);
        CategoryTabView tabView = new CategoryTabView(this, args);

        TabSpec tabSpec = mTabHost.newTabSpec(categoryName);
        tabSpec.setIndicator(tabView);

        // ここでのtabcontent用Fragmentは実際には使わないのでダミー
        mTabHost.addTab(tabSpec, Fragment.class, args);
        fragments.add(frag);
    }

    public void onArticleSelected(String url, String title) {
        Log.d(TAG, url);
        final Intent intent = new Intent(MainActivity.this, BrowseActivity.class);

        intent.putExtra("url", url);
        intent.putExtra("title", title);
        animatedStartActivity(intent);
    }

    @Override
    public void onTabChanged(String tag) {
        int pos = mTabHost.getCurrentTab();
        String color = ((CategoryTabView) mTabHost.getTabWidget().getChildAt(pos)).originalColor;

        mViewPager.setCurrentItem(pos);
        mDivider.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    int previousDelta = 0;
    @Override
    public void onPageScrolled(int pos, float offset, int offsetPixel) {
        // FIXME 制御バグってる＼(^o^)／
//        Log.d(TAG, "" + pos + ":" + offset + ":" + offsetPixel);
//        Log.d(TAG, "current pos? " + mViewPager.getCurrentItem());
//        if (offset < 0.9 && offset > 0.1) {
//            int amountDelta = Math.round(mScroller.getWidth() * offset);
//            int delta = amountDelta - previousDelta;
//            if (pos < mViewPager.getCurrentItem()) {
//                delta *= -1;
//            }
//            Log.d(TAG, "delta? " + delta);
//            mScroller.scrollBy(delta, 0);
//            previousDelta = amountDelta;
//        } else {
//            previousDelta = 0;
//        }
    }

    @Override
    public void onPageSelected(int pos) {
        // FIXME 処理がゴリゴリすぎだぃ
        TabWidget tabWidget = mTabHost.getTabWidget();
        mTabHost.setCurrentTab(pos);

        int delta = 0;
        for (int i = 0; i < pos; i++) {
            delta += tabWidget.getChildAt(i).getWidth();
        }
        delta += (tabWidget.getChildAt(pos).getWidth() / 2);
        delta -= (mScroller.getWidth() / 2);

        CategoryTabView tab;
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            tab = (CategoryTabView) tabWidget.getChildAt(i);
            tab.setInactiveColor();
        }
        tab = (CategoryTabView) tabWidget.getChildAt(pos);
        tab.setActiveColor();

        mScroller.smoothScrollTo(delta, 0);
    }

    @Override
    // @see http://stackoverflow.com/questions/10321712/loader-doesnt-start-after-calling-initloader
    // @see http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html
    public Loader<HttpAsyncTaskResult<HashMap>> onCreateLoader(int id, Bundle args) {
        AsyncTaskLoader loader;
        switch (id) {
            case 0:
                Log.d(TAG, "ON CREATE LOADER :" + "http://read.aho.mu/categories.json");
                loader = new JsonHttpAsyncTaskLoader(this, "http://read.aho.mu/categories.json");
                return loader;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<HttpAsyncTaskResult<HashMap>> loader, HttpAsyncTaskResult<HashMap> result) {
        Log.d(TAG, "ON LOAD FINISHED");

        Exception exception = result.getException();
        if (exception != null) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<HashMap> categories = (ArrayList) result.getData().get("categories");
        HashMap category;
        for (int i = 0; i < categories.size(); i++) {
            category = categories.get(i);
            addTabAndFragment(
                    (String) category.get("name"),
                    "http://read.aho.mu/categories/" + category.get("id") + ".json",
                    (String) category.get("color")
            );
        }

        mPageAdapter.notifyDataSetChanged();
        getSupportLoaderManager().destroyLoader(0);
    }

    @Override
    public void onLoaderReset(Loader<HttpAsyncTaskResult<HashMap>> loader) {}

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
