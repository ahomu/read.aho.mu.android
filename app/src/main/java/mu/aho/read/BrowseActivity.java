package mu.aho.read;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

/**
 * Created by ayumusato on 3/26/14.
 */
public class BrowseActivity extends ActionBarActivity {

    private final String TAG = getClass().getSimpleName();

    ActionBar mActionBar;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(extras.getString("title"));

        WebViewFragment webViewFrag = WebViewFragment.newInstance();
        webViewFrag.setArguments(extras);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, webViewFrag).commit();
    }

    @Override
    protected void onResume() {
        ActivitySwitcher.animationIn(findViewById(android.R.id.content), getWindowManager());
        super.onResume();
    }
}
