package mu.aho.read.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ayumusato on 3/28/14.
 */
public class CategoriesLoader implements LoaderManager.LoaderCallbacks<HttpAsyncTaskResult<HashMap>> {

    public static final int FETCH = 0;

    private final String TAG = getClass().getSimpleName();

    private Context context;

    private CompleteCallback callback;

    public interface CompleteCallback {
        void onSuccess(ArrayList<HashMap> result);
    }

    public CategoriesLoader(Context context, CompleteCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    // @see http://stackoverflow.com/questions/10321712/loader-doesnt-start-after-calling-initloader
    // @see http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html
    public Loader<HttpAsyncTaskResult<HashMap>> onCreateLoader(int id, Bundle args) {
        AsyncTaskLoader loader;
        switch (id) {
            case 0:
                Log.d(TAG, "ON CREATE LOADER :" + "http://read.aho.mu/categories.json");
                loader = new JsonHttpAsyncTaskLoader(context, "http://read.aho.mu/categories.json");
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
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<HashMap> categories = (ArrayList) result.getData().get("categories");
        HashMap category;

        for (int i = 0; i < categories.size(); i++) {
            category = categories.get(i);
            category.put("url", "http://read.aho.mu/categories/" + category.get("id") + ".json");
        }

        callback.onSuccess(categories);
    }

    @Override
    public void onLoaderReset(Loader<HttpAsyncTaskResult<HashMap>> loader) {}
}
