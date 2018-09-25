package de.aitronic.scannerdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by T.Reike on 05.12.2016.
 */

public class MyArrayAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater = null;
    List<String[]> objects;
    int resource;

    public MyArrayAdapter(Context context, int resource, List<String[]> objects) {
        this.context = context;
        this.objects = objects;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public String[] getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null) {
            inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        View view = inflater.inflate(resource, parent, false);
        ((TextView)view.findViewById(R.id.type)).setText(objects.get(position)[0]);
        ((TextView)view.findViewById(R.id.value)).setText(objects.get(position)[1]);
        return view;
    }
}
