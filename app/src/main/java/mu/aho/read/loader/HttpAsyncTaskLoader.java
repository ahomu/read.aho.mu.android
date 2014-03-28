package mu.aho.read.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import mu.aho.read.handler.HttpResponseHandler;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by ayumusato on 3/27/14.
 */
// @see http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html
public abstract class HttpAsyncTaskLoader<T> extends AsyncTaskLoader {

    private final String TAG = getClass().getSimpleName();

    protected HttpAsyncTaskResult<T> result;

    protected String requestUrl;

    public HttpAsyncTaskLoader(Context context, String url) {
        super(context);
        requestUrl = url;
    }

    @Override
    public void onStartLoading() {
        forceLoad();
    }

    public abstract T parseResponse(String result);

    @Override
    public HttpAsyncTaskResult loadInBackground() {
        result = new HttpAsyncTaskResult();
        String rawResponse;

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(requestUrl);

        try {
            rawResponse = (String) httpClient.execute(request, new HttpResponseHandler());

            Log.d(TAG, rawResponse);

            result.setData(this.parseResponse(rawResponse));

        } catch(ClientProtocolException e) {
            result.setException(e);
        } catch (IOException e) {
            result.setException(e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        return result;
    }
}
