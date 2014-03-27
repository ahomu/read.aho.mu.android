package mu.aho.read;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ahomu on 3/26/14.
 */

public class CategoryFragment extends Fragment {

    public static final CategoryFragment newInstance(String sampleText, String hex) {
        CategoryFragment f = new CategoryFragment();

        Bundle b = new Bundle();
        b.putString("category", sampleText);
        b.putString("color",    hex);
        f.setArguments(b);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String name = getArguments().getString("category");
        Log.d("TabFragment.onCreateView", name);

        TextView textView = new TextView(getActivity());
        textView.setGravity(Gravity.CENTER);
        textView.setText(getArguments().getString("category"));
        textView.setTextColor(Color.parseColor("black"));
        textView.setBackgroundColor(Color.parseColor(getArguments().getString("color")));

        return textView;
    }
}
