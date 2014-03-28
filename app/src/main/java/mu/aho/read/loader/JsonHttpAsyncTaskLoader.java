package mu.aho.read.loader;

import android.content.Context;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ayumusato on 3/28/14.
 */
public class JsonHttpAsyncTaskLoader extends HttpAsyncTaskLoader<HashMap> {

    public JsonHttpAsyncTaskLoader(Context context, String url) {
        super(context, url);
    }

    public HashMap parseResponse(String rawResponse) {
        HashMap ret = null;
        try {
            ret = new ObjectMapper().readValue(rawResponse, HashMap.class);
        } catch(IOException e) {
            result.setException(e);
        }
        return ret;
    }
}
