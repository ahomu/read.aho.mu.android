package mu.aho.read.loader;

/**
 * Created by ayumusato on 3/28/14.
 */
public class HttpAsyncTaskResult<T> {

    private Exception exception;

    private T data;

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

}