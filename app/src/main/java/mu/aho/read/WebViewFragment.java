package mu.aho.read;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by ayumusato on 3/27/14.
 */
public class WebViewFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    public static final WebViewFragment newInstance() {
        WebViewFragment frag = new WebViewFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "Fragment-onCreateView");
        View view = inflater.inflate(R.layout.fragment_browse, container ,false);

        WebView webView = (WebView) view.findViewById(R.id.browser);
        webView.setWebViewClient(new WebViewClient(){
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
                getActivity().setProgressBarIndeterminateVisibility(false);
            }
        });

        webView.setScrollbarFadingEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getArguments().getString("url"));

        return view;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.mainmenu, menu);
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.item1:
//                webView.reload();
//                break;
//            case R.id.item2:
//                webView.goBack();
//                break;
//            default:
//                break;
//        }
//        return true;
//    }
}
