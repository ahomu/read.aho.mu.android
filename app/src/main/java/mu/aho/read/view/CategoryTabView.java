package mu.aho.read.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import mu.aho.read.R;

/**
 * Created by ayumusato on 3/26/14.
 */
public class CategoryTabView extends FrameLayout {
    LayoutInflater inflater;

    public CategoryTabView(Context context) {
        super(context);
        inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CategoryTabView(Context context, String title) {
        this(context);
        View child = inflater.inflate(R.layout.tab, null);
        TextView textView = (TextView) child.findViewById(R.id.tag_label);
        textView.setText(title);
        addView(child);
    }

    public void setColor(String hex) {
        TextView textView = (TextView) this.findViewById(R.id.tag_label);
        textView.setBackgroundColor(Color.parseColor(hex));
    }
}