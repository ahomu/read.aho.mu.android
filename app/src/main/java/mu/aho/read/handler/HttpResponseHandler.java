package mu.aho.read.handler;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by ayumusato on 3/27/14.
 */
// @see http://terurou.hateblo.jp/entry/20110702/1309541200
public class HttpResponseHandler implements ResponseHandler {
    private final String TAG = getClass().getSimpleName();

    @Override
    public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

        Log.d(TAG, "HANDLE RESPONSE...");

        // response.getStatusLine().getStatusCode()でレスポンスコードを判定する。
        // 正常に通信できた場合、HttpStatus.SC_OK（HTTP 200）となる。
        switch (response.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_OK:
                // レスポンスデータを文字列として取得する。
                // byte[]として読み出したいときはEntityUtils.toByteArray()を使う。
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            case HttpStatus.SC_NOT_FOUND:
                throw new RuntimeException("データない");
            default:
                throw new RuntimeException("なんかエラー");
        }

    }
}
