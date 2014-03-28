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
public class EntriesLoader implements LoaderManager.LoaderCallbacks<HttpAsyncTaskResult<HashMap>> {

    public static final int FETCH = 0;

    private final String TAG = getClass().getSimpleName();

    private Context context;

    private CompleteCallback callback;

    public interface CompleteCallback {
        void onSuccess(ArrayList<HashMap> result);
    }

    public EntriesLoader(Context context, CompleteCallback callback) {
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
                Log.d(TAG, "ON CREATE LOADER :" + args.getString("entriesUrl"));
                loader = new JsonHttpAsyncTaskLoader(context, args.getString("entriesUrl"));
                return loader;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<HttpAsyncTaskResult<HashMap>> loader, HttpAsyncTaskResult<HashMap> result) {
        Log.d(TAG, result.toString());

        Exception exception = result.getException();
        if (exception != null) {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
        ArrayList<HashMap> entries = (ArrayList) result.getData().get("entries");
        callback.onSuccess(entries);
    }

    @Override
    public void onLoaderReset(Loader<HttpAsyncTaskResult<HashMap>> loader) {}
}
