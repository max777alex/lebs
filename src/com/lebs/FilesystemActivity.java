package com.lebs;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class FilesystemActivity extends Activity {
    private int FILENAME_IND = 0;
    private int PATH_IND = 1;

    private Button showFilesButton;
    private ListView filesListView;

    ArrayList<String> songs = new ArrayList<String>();
    ArrayList<String> songsPaths = new ArrayList<String>();

    private Cursor getMusicCursor() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
        };

        ContentResolver resolver = FilesystemActivity.this.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);
        return cursor;
    }

    private void listMusic(Cursor cursor) {
        if( cursor == null ) {
            return;
        }
        songs.clear();
        songsPaths.clear();
        while( cursor.getCount() > 0 && cursor.moveToNext() ){
            songs.add(cursor.getString(FILENAME_IND));
            songsPaths.add(cursor.getString(PATH_IND));
        }

        Context appContext = getApplicationContext();
        if( appContext != null ) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(appContext, R.layout.item_layout,
                    R.id.label,
                    songs);
            filesListView.setAdapter(adapter);
        }
    }

    private void getMusic() {
        Cursor cursor = getMusicCursor();
        listMusic(cursor);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filesystem_layout);

        showFilesButton = (Button) findViewById(R.id.showFilesButton);
        filesListView = (ListView) findViewById(R.id.filesListView);

        showFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMusic();
            }
        });

        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String musicFilePath = songsPaths.get(position);
                File currentMusicFile = new File(musicFilePath);
                if( currentMusicFile.canRead() ) {
                    
                }
            }
        });
        showFilesButton.performClick();
    }
}
