package mu.aho.read.loader;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ayumusato on 3/28/14.
 */
public class JsonHttpAsyncTaskLoader extends HttpAsyncTaskLoader<JSONObject> {

    public JsonHttpAsyncTaskLoader(Context context, String url) {
        super(context, url);
    }

    public JSONObject parseResponse(String rawResponse) {
        JSONObject ret = null;
        try {
            ret = new JSONObject(rawResponse);
        } catch(JSONException e) {
            result.setException(e);
        }
        return ret;
    }
}
