package com.lebs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class PlayListItemArrayAdapter extends ArrayAdapter<Song> {
    private final Context context;
    private final ArrayList<Song> values;

    public PlayListItemArrayAdapter(Context context, ArrayList<Song> values) {
        super(context, R.layout.playlist_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.playlist_item, parent, false);

        TextView songNameTextView = (TextView) itemView.findViewById(R.id.songName);
        TextView songArtistTextView = (TextView) itemView.findViewById(R.id.songArtist);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.icon);

        songNameTextView.setText(values.get(position).name);
        songArtistTextView.setText(values.get(position).artist);
        imageView.setImageResource(R.drawable.play_icon);

        return itemView;
    }
}
