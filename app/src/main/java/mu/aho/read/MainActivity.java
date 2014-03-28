package mu.aho.read;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
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
import mu.aho.read.adapter.CategoriesPagerAdapter;
import mu.aho.read.loader.CategoriesLoader;
import mu.aho.read.transformer.SlidePageTransformer;
import mu.aho.read.view.CategoryTabView;

/**
 * Created by ahomu on 3/26/14.
 */
public class MainActivity extends FragmentActivity
        implements OnTabChangeListener, OnPageChangeListener, CategoryFragment.OnEntrySelectedListener {

    private final String TAG = getClass().getSimpleName();

    private ViewPager mViewPager;
    private FragmentTabHost mTabHost;
    private HorizontalScrollView mScroller;
    private LoaderManager mLoaderManager;
    private CategoriesPagerAdapter mPageAdapter;
    private View mDivider;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    /**
     * ＼＼\\٩( 'ω' )و //／／
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // load categories
        mLoaderManager = getSupportLoaderManager();
        mLoaderManager.initLoader(CategoriesLoader.FETCH, null, getCategoriesLoader());

        // first item
        addTabAndFragment("ALL FEEDS", "http://read.aho.mu/top/index.json", "#FFFFFF");
        ((CategoryTabView) mTabHost.getTabWidget().getChildAt(0)).setActiveColor();
        mPageAdapter.notifyDataSetChanged();
    }

    /**
     * CategoriesLoaderを、コールバック渡しながら取得する
     * @return CategoriesLoader
     */
    private CategoriesLoader getCategoriesLoader() {
        return new CategoriesLoader(this, new CategoriesLoader.CompleteCallback() {
            public void onSuccess(ArrayList<HashMap> result) {
                HashMap<String, String> category;

                for (int i = 0; i < result.size(); i++) {
                    category = result.get(i);
                    addTabAndFragment(category.get("name"), category.get("url"), category.get("color"));
                }

                mPageAdapter.notifyDataSetChanged();
                getSupportLoaderManager().destroyLoader(0);
            }
        });
    }

    /**
     * カテゴリーのタブとフラグメントまとめて作る
     * @param categoryName String
     * @param entriesUrl String
     * @param colorHex String
     */
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

    /**
     * エントリーが選択されたときにBrowseActivityを始める
     * @param url String
     * @param title String
     */
    public void onEntrySelected(String url, String title) {
        Log.d(TAG, url);
        final Intent intent = new Intent(MainActivity.this, BrowseActivity.class);

        intent.putExtra("url", url);
        intent.putExtra("title", title);
        animatedStartActivity(intent);
    }

    /**
     * タブ位置が変化したときに、ページ遷移を同期させる
     * タブ周りのUI表現も更新する
     * @param tag String
     */
    @Override
    public void onTabChanged(String tag) {
        // FIXME 処理がゴリゴリすぎだぃ
        TabWidget tabWidget = mTabHost.getTabWidget();

        int pos = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(pos);

        // タブ下の色
        String color = ((CategoryTabView) tabWidget.getChildAt(pos)).originalColor;
        mDivider.setBackgroundColor(Color.parseColor(color));

        // タブ位置
        int delta = 0;
        for (int i = 0; i < pos; i++) {
            delta += tabWidget.getChildAt(i).getWidth();
        }
        delta += (tabWidget.getChildAt(pos).getWidth() / 2);
        delta -= (mScroller.getWidth() / 2);
        mScroller.smoothScrollTo(delta, 0);

        // 選択中タブ
        CategoryTabView tab;
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            tab = (CategoryTabView) tabWidget.getChildAt(i);
            tab.setInactiveColor();
        }
        tab = (CategoryTabView) tabWidget.getChildAt(pos);
        tab.setActiveColor();
    }

    /**
     * つかってません( ՞ٹ՞)
     * @param state Integer
     */
    @Override
    public void onPageScrollStateChanged(int state) {}

    int previousDelta = 0;

    /**
     * ページがスクロールしたときに...何かをしたい
     * @param pos Integer
     * @param offset Float
     * @param offsetPixel Integer
     */
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

    /**
     * ページが遷移したときに、タブ位置を同期させる
     * @param pos Integer
     */
    @Override
    public void onPageSelected(int pos) {
        mTabHost.setCurrentTab(pos);
    }

    /**
     * Activityが復帰したときにアニメーションで入る
     * 三(((((( ՞ٹ՞)
     */
    @Override
    protected void onResume() {
        ActivitySwitcher.animationIn(findViewById(android.R.id.tabhost), getWindowManager());
        super.onResume();
    }

    /**
     * 別のActivityを始めるときにアニメーションで出て行く
     * 三(((((( ՞ٹ՞)
     * @param intent Intent
     */
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
