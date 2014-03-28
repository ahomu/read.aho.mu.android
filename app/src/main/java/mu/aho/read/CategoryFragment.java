package mu.aho.read;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import com.nineoldandroids.animation.ObjectAnimator;
import mu.aho.read.adapter.EntriesArrayAdapter;
import mu.aho.read.loader.HttpAsyncTaskResult;
import mu.aho.read.loader.JsonHttpAsyncTaskLoader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ahomu on 3/26/14.
 */
public class CategoryFragment extends ListFragment implements LoaderCallbacks<HttpAsyncTaskResult<HashMap>> {

    private final String TAG = getClass().getSimpleName();

    private ArrayList<HashMap> list = new ArrayList<HashMap>();

    private LoaderManager mLoaderManager;

    public static final CategoryFragment newInstance(String sampleText, String entriesUrl) {
        CategoryFragment frag = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString("category", sampleText);
        args.putString("entriesUrl", entriesUrl);
        frag.setArguments(args);
        return frag;
    }

    OnArticleSelectedListener mListener;

    // @see http://y-anz-m.blogspot.jp/2011/05/androidfragment_19.html
    public interface OnArticleSelectedListener {
        public void onArticleSelected(String articleUri, String title);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnArticleSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    public void onListItemClick(ListView listView, View view, int pos, long id) {
        Log.d(TAG, pos + " position clicked");

        // !!! PullToRefreshListView の addHeaderView で追加されてるのでposがひとつズレてる
        if (pos == 0) {
            return;
        }
        pos--;

        HashMap<String, String> item = list.get(pos);
        mListener.onArticleSelected(item.get("url"), item.get("title"));
    }

    @Override
    // ListFragmentが内包しているListViewをPullToRefreshListViewと置換する
    // @see https://github.com/johannilsson/android-pulltorefresh/issues/30
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);

        View lvOld = viewGroup.findViewById(android.R.id.list);

        final PullToRefreshListView listView = new PullToRefreshListView(getActivity());
        listView.setId(android.R.id.list);
        listView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setDrawSelectorOnTop(false);

        // @see http://stackoverflow.com/questions/2372415/how-to-change-color-of-android-listview-separator-line
        listView.setDivider(new ColorDrawable(Color.parseColor("#555555")));
        listView.setDividerHeight(1);

        FrameLayout parent = (FrameLayout) lvOld.getParent();

        parent.removeView(lvOld);
        lvOld.setVisibility(View.GONE);

        parent.addView(listView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return viewGroup;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "CategoryFragment onActivityCreated");
        mLoaderManager = getLoaderManager();
        Bundle argsForLoader = new Bundle();
        argsForLoader.putString("entriesUrl", getArguments().getString("entriesUrl"));
        mLoaderManager.initLoader(0, argsForLoader, this);

        setListAdapter(new EntriesArrayAdapter(getActivity(), list));
        setListShown(false);

        ((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                Log.d(TAG, "PULL TO REFRESH");
                mLoaderManager.getLoader(0).forceLoad();
            }
        });
    }

    @Override
    // @see http://stackoverflow.com/questions/10321712/loader-doesnt-start-after-calling-initloader
    // @see http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html
    public Loader<HttpAsyncTaskResult<HashMap>> onCreateLoader(int id, Bundle args) {
        AsyncTaskLoader loader;
        switch (id) {
            case 0:
                Log.d(TAG, "ON CREATE LOADER :" + args.getString("entriesUrl"));
                loader = new JsonHttpAsyncTaskLoader(getActivity(), args.getString("entriesUrl"));
                return loader;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<HttpAsyncTaskResult<HashMap>> loader, HttpAsyncTaskResult<HashMap> result) {
        Log.d(TAG, result.toString());

        setListShown(true);
        list.clear();

        Exception exception = result.getException();
        if (exception != null) {
            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

        ArrayList<HashMap> entries = (ArrayList) result.getData().get("entries");
        HashMap entry;
        for (int i = 0; i < entries.size(); i++) {
            entry = entries.get(i);
            list.add(entry);
        }

        ((PullToRefreshListView) getListView()).onRefreshComplete();
        ObjectAnimator animator = ObjectAnimator.ofFloat(getListView(), "alpha", 0.50f, 1, 1);
        animator.setDuration(500);
        animator.start();
    }

    @Override
    public void onLoaderReset(Loader<HttpAsyncTaskResult<HashMap>> loader) {}
}

