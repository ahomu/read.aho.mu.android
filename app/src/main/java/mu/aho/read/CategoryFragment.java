package mu.aho.read;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import com.nineoldandroids.animation.ObjectAnimator;
import mu.aho.read.adapter.EntriesArrayAdapter;
import mu.aho.read.loader.EntriesLoader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ahomu on 3/26/14.
 */
public class CategoryFragment extends ListFragment {

    private final String TAG = getClass().getSimpleName();

    private ArrayList<HashMap> list = new ArrayList<HashMap>();

    private LoaderManager mLoaderManager;

    private OnEntrySelectedListener mListener;

    /**
     * ＼＼\\٩( 'ω' )و //／／
     * @param sampleText String
     * @param entriesUrl String
     */
    public static final CategoryFragment newInstance(String sampleText, String entriesUrl) {
        CategoryFragment frag = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString("category", sampleText);
        args.putString("entriesUrl", entriesUrl);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Articleが選択されたときのリスナーインターフェース
     * @see 'http://y-anz-m.blogspot.jp/2011/05/androidfragment_19.html'
     */
    public interface OnEntrySelectedListener {
        public void onEntrySelected(String articleUri, String title);
    }

    /**
     * Activity が OnEntrySelectedListenerを実装してるかチェック
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnEntrySelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnEntrySelectedListener");
        }
    }

    /**
     * アイテムがクリックされたら、ActivityのonEntrySelectedに処理を委譲する
     * @param listView ListView
     * @param view View
     * @param pos Integer
     * @param id Long
     */
    public void onListItemClick(ListView listView, View view, int pos, long id) {
        Log.d(TAG, pos + " position clicked");

        // !!! PullToRefreshListView の addHeaderView で追加されてるのでposがひとつズレてる
        if (pos == 0) {
            return;
        }
        pos--;

        HashMap<String, String> item = list.get(pos);
        mListener.onEntrySelected(item.get("url"), item.get("title"));
    }

    /**
     * ListFragmentが内包しているListViewをPullToRefreshListViewと置換する
     * @see 'https://github.com/johannilsson/android-pulltorefresh/issues/30'
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
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

        // @see http://stackoverflow.com/questions/21211870/android-view-shadow
        listView.setBackgroundResource(R.drawable.abc_menu_dropdown_panel_holo_dark);
        listView.setPadding(12, 12, 12, 0);

        FrameLayout parent = (FrameLayout) lvOld.getParent();

        parent.removeView(lvOld);
        lvOld.setVisibility(View.GONE);

        parent.addView(listView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return viewGroup;
    }

    /**
     * 色々アレする（主に初期化処理な）
     * @param savedInstanceState Bundle
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "CategoryFragment onActivityCreated");

        setListAdapter(new EntriesArrayAdapter(getActivity(), list));
        setListShown(false);

        mLoaderManager = getLoaderManager();
        Bundle argsForLoader = new Bundle();
        argsForLoader.putString("entriesUrl", getArguments().getString("entriesUrl"));
        mLoaderManager.initLoader(EntriesLoader.FETCH, argsForLoader, getEntriesLoader());

        ((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                Log.d(TAG, "PULL TO REFRESH");
                mLoaderManager.getLoader(EntriesLoader.FETCH).forceLoad();
            }
        });
    }

    /**
     * EntriesLoaderを、コールバック渡しながら取得する
     * @return EntriesLoader
     */
    private EntriesLoader getEntriesLoader() {
        return new EntriesLoader(getActivity(), new EntriesLoader.CompleteCallback() {
            public void onSuccess(ArrayList<HashMap> result) {

                setListShown(true);
                list.clear();
                list.addAll(result);

                ((PullToRefreshListView) getListView()).onRefreshComplete();
                ObjectAnimator animator = ObjectAnimator.ofFloat(getListView(), "alpha", 0.50f, 1, 1);
                animator.setDuration(500);
                animator.start();
            }
        });
    }
}

