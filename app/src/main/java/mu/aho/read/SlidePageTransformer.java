package mu.aho.read;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

/**
 * Created by ayumusato on 3/27/14.
 */
public class SlidePageTransformer implements PageTransformer {
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            animate(view).translationX(0);
        } else if (position <= 1) { // (0,1]
            // Counteract the default slide transition
            animate(view).translationX(pageWidth * -position);
        }
    }
}