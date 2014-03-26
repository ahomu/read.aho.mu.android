package mu.aho.read;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.*;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import java.util.ArrayList;
import java.util.Locale;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;

// @see http://davidjkelley.net/?p=34
// @see http://just-another-blog.net/programming/how-to-implement-horizontal-view-swiping-with-tabs/
// @see http://k-1-ne-jp.blogspot.jp/2013/11/fragmenttabhost.html
// @see http://yyyank.blogspot.jp/2013/07/androidapisummarize-deprecated-class.html
/**
 * Created by ahomu on 3/26/14.
 */
public class MainActivity extends FragmentActivity implements FragmentTabHost.OnTabChangeListener {

    ViewPager mViewPager;
    FragmentTabHost mTabHost;
    private String[] TabTag = { "お気に入りお気に入りお気に入り", "tab2", "tab3", "tab4",
            "tab5" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowHomeEnabled(false);

        final FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
//        final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);

        mTabHost.setup(this, getSupportFragmentManager(), R.id.content);

        for (int i = 0; i < TabTag.length; i++) {

            String name = TabTag[i];

            Log.d("tabSpec", TabTag[i]);

            Bundle bundle = new Bundle();
            bundle.putString("name", name);

            TabSpec tabSpec = mTabHost.newTabSpec(name);
            tabSpec.setIndicator(name);
            mTabHost.addTab(tabSpec, EntriesFragment.class, bundle);
        }

        mTabHost.setOnTabChangedListener(this);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }
    }
    /*
     * タブの選択が変わったときに呼び出される
     *
     * @see
     * android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
     */
    @Override
    public void onTabChanged(String arg0) {
        // TODO Auto-generated method stub
        Log.d("onTabChanged", "tabId:" + arg0);
    }
}
