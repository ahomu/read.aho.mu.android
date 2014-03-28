package mu.aho.read;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import mu.aho.read.loader.HttpAsyncTaskLoader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by ahomu on 3/26/14.
 */

public class CategoryFragment extends ListFragment implements LoaderCallbacks<JSONObject> {

    private final String TAG = getClass().getSimpleName();

    ArrayList<JSONObject> list = new ArrayList<JSONObject>();

    LoaderManager mLoaderManager;

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
        try {
            JSONObject item = list.get(pos);
            mListener.onArticleSelected(item.getString("url"), item.getString("title"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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

        setListAdapter(new EntriesAdapter(getActivity(), list));

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
    public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
        AsyncTaskLoader loader;
        switch (id) {
            case 0:
                Log.d(TAG, "ON CREATE LOADER :" + args.getString("entriesUrl"));
                loader = new HttpAsyncTaskLoader(getActivity(), args.getString("entriesUrl"));
                return loader;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<JSONObject> loader, JSONObject result) {
        Log.d(TAG, "list size -> " + list.size());
        Log.d(TAG, result.toString());

        list.clear();

        try {
            // TODO 最初からArrayListとかふんわりとした型で取得できないか？
            JSONArray entries = result.getJSONArray("entries");
            JSONObject entry;
            Integer iz = entries.length();
            for (int i = 0; i < iz; i++) {
                entry = entries.getJSONObject(i);
                list.add(entry);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        ((PullToRefreshListView) getListView()).onRefreshComplete();
    }

    @Override
    public void onLoaderReset(Loader<JSONObject> loader) {}

    public static class EntriesAdapter extends ArrayAdapter {

        private final String TAG = getClass().getSimpleName();

        LayoutInflater inflater;
        ArrayList<JSONObject> entries;

        public EntriesAdapter(Context context, ArrayList<JSONObject> list) {
            super(context, R.layout.item, list);
            entries = list;
            inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public View getView(int pos, View convertView, ViewGroup parent) {
            Log.d(TAG, "GET LIST ITEM VIEW " + pos);

            JSONObject item = entries.get(pos);
            View view;

            if (null != convertView) {
                view = convertView;
            } else {
                view = inflater.inflate(R.layout.item, null);
            }
            try {
                TextView titleText = (TextView) view.findViewById(R.id.item_title);
                titleText.setText(item.getString("title"));
                TextView urlText = (TextView) view.findViewById(R.id.item_url);
                urlText.setText(item.getString("url"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return view;
        }

    }
}

