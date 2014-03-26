package mu.aho.read;

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

public class EntriesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String name = getArguments().getString("name");
        Log.d("TabFragment.onCreateView", name);

        TextView textView = new TextView(getActivity());
        textView.setGravity(Gravity.CENTER);
        textView.setText(getArguments().getString("name"));

        return textView;
    }
}
