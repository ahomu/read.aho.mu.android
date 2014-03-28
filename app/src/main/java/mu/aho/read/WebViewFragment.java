package mu.aho.read;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by ayumusato on 3/27/14.
 */
public class WebViewFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    private WebView webView;

    /**
     * ＼＼\\٩( 'ω' )و //／／
     */
    public static final WebViewFragment newInstance() {
        WebViewFragment frag = new WebViewFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    /**
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "Fragment-onCreateView");
        View view = inflater.inflate(R.layout.fragment_browse, container ,false);

        webView = (WebView) view.findViewById(R.id.browser);
        webView.setWebViewClient(new WebViewClient() {
            // @see http://www.adakoda.com/android/000291.html
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "WebView:onPageStarted...");
                getActivity().setProgressBarIndeterminateVisibility(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "WebView:onPageFinished...");
                ActionBarActivity activity = (ActionBarActivity) getActivity();
                String title = webView.getTitle();
                if (title != "") {
                    activity.getSupportActionBar().setTitle(title);
                }
                activity.setProgressBarIndeterminateVisibility(false);
            }
        });

        webView.setScrollbarFadingEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getArguments().getString("url"));

        return view;
    }

    /**
     * Viewを破棄するときに、webViewの途中状態もぶっ壊しておく
     * FIXME Errorしてるときある...落ちないけど
     */
    @Override
    public void onDestroyView() {
        webView.destroy();
        super.onDestroyView();
    }

    /**
     * @param menu Menu
     * @param inflater MenuInflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.browse, menu);
    }

    /**
     * @param item MenuItem
     * @return Bool
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload:
                webView.reload();
                break;
            case R.id.back:
                webView.goBack();
                break;
            case R.id.forward:
                webView.goForward();
                break;
            default:
                break;
        }
        return true;
    }
}
