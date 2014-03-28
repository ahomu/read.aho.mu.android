package mu.aho.read;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

/**
 * Created by ayumusato on 3/26/14.
 */
public class BrowseActivity extends ActionBarActivity {

    private final String TAG = getClass().getSimpleName();

    private ActionBar mActionBar;
    /**
     * ＼＼\\٩( 'ω' )و //／／
     * @param savedInstanceState Bundle
     */
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

    /**
     * Activityが復帰したときにアニメーションで入る
     * 三(((((( ՞ٹ՞)
     */
    @Override
    protected void onResume() {
        ActivitySwitcher.animationIn(findViewById(android.R.id.content), getWindowManager());
        super.onResume();
    }
}
