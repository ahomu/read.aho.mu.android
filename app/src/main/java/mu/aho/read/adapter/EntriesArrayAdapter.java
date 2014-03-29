package mu.aho.read.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

        final ViewHolder holder;

        HashMap<String, String> item = entries.get(pos);

        if (null != convertView) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            holder.url = (TextView) convertView.findViewById(R.id.item_url);
            convertView.setTag(holder);
        }

        holder.title.setText(item.get("title"));
        holder.url.setText(item.get("url"));

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView url;
    }
}
