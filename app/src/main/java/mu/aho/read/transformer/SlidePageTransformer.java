package mu.aho.read.transformer;

import android.support.v4.view.ViewPager.PageTransformer;
import android.util.Log;
import android.view.View;

/**
 * Created by ayumusato on 3/27/14.
 */
public class SlidePageTransformer implements PageTransformer {

    private final String TAG = getClass().getSimpleName();

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        // @see http://stackoverflow.com/questions/19492564/overlapping-fragments-and-touch-events
        if (position >= 1 || position <= -1) {
            view.setTranslationX(0);

        } else if (position <= 1) { // (0,1]
            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setTranslationX(0);

        }

    }
}