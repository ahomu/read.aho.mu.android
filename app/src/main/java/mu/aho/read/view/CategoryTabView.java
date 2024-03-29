package mu.aho.read.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import mu.aho.read.R;

/**
 * Created by ayumusato on 3/26/14.
 */
public class CategoryTabView extends FrameLayout {
    public String originalColor;
    private LayoutInflater inflater;
    private TextView labelView;

    public CategoryTabView(Context context) {
        super(context);
        inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CategoryTabView(Context context, Bundle args) {
        this(context);
        View child = inflater.inflate(R.layout.tab, null);
        labelView = (TextView) child.findViewById(R.id.tag_label);
        labelView.setText(args.getString("category"));
        originalColor = args.getString("color");
        addView(child);
    }

    public void setActiveColor() {
        labelView.setTextColor(Color.parseColor(originalColor));
    }

    public void setInactiveColor() {
        labelView.setTextColor(Color.parseColor("#666666"));
    }

}