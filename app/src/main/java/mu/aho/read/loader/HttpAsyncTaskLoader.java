package mu.aho.read.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import mu.aho.read.handler.HttpResponseHandler;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by ayumusato on 3/27/14.
 */
public class HttpAsyncTaskLoader extends AsyncTaskLoader {

    private final String TAG = getClass().getSimpleName();

    private String requestUrl;

    public HttpAsyncTaskLoader(Context context, String url) {
        super(context);
        requestUrl = url;
    }

    @Override
    public void onStartLoading() {
        forceLoad();
    }

    public JSONObject loadInBackground() {
        String jsonString = "";
        JSONObject jsonObject = null;

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(requestUrl);

        try {
            // TODO httpClientのインスタンスを使い回せないか？
            jsonString = (String) httpClient.execute(request, new HttpResponseHandler());
        } catch(ClientProtocolException e) {
            Log.e(TAG, "ClientProtocolException!");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException!");
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }

        try {
            jsonObject = new JSONObject(jsonString);
        } catch(JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jsonObject;
    }
}
