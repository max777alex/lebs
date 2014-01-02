package com.lebs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemArrayAdapter extends ArrayAdapter<Song> {
    private final Context context;
    private final ArrayList<Song> values;

    public ItemArrayAdapter(Context context, ArrayList<Song> values) {
        super(context, R.layout.item_layout, values);
        this.context = context;

        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_layout, parent, false);
        TextView textView = (TextView) itemView.findViewById(R.id.label);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.icon);

        textView.setText(values.get(position).toString());
        imageView.setImageResource(R.drawable.play_icon);

        return itemView;
    }
}
