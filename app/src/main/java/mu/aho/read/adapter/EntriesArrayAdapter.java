package mu.aho.read.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import mu.aho.read.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ayumusato on 3/28/14.
 */
public class EntriesArrayAdapter extends ArrayAdapter {

    private final String TAG = getClass().getSimpleName();

    LayoutInflater inflater;
    ArrayList<HashMap> entries;

    public EntriesArrayAdapter(Context context, ArrayList<HashMap> list) {
        super(context, R.layout.item, list);
        entries = list;
        inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        Log.d(TAG, "GET LIST ITEM VIEW " + pos);

        HashMap<String, String> item = entries.get(pos);
        View view;

        if (null != convertView) {
            view = convertView;
        } else {
            view = inflater.inflate(R.layout.item, null);
        }

        TextView titleText = (TextView) view.findViewById(R.id.item_title);
        titleText.setText(item.get("title"));
        TextView urlText = (TextView) view.findViewById(R.id.item_url);
        urlText.setText(item.get("url"));

        return view;
    }

}
