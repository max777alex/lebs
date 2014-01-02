package com.lebs;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ListView listView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        listView = (ListView) findViewById(R.id.listView);

        FileSystemManager manager = new FileSystemManager(this);

        ArrayList<Song> songs = manager.getSongs();

        ItemArrayAdapter adapter = new ItemArrayAdapter(this, songs);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Song song = (Song) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, SongLyricsActivity.class);
                intent.putExtra("song", song.name);
                intent.putExtra("path", song.path);

                MainActivity.this.startActivity(intent);
            }
        });
    }
}

