package mu.aho.read.listener;

/**
 * Created by ayumusato on 3/27/14.
 */
public class PageChangeListener {
    private static PageChangeListener ourInstance = new PageChangeListener();

    public static PageChangeListener getInstance() {
        return ourInstance;
    }

    private PageChangeListener() {
        // TODO should implements (MainActivityから移植
    }
}
