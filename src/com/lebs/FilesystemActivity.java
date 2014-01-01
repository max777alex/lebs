package com.lebs;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Райан on 01.01.14.
 */
public class FilesystemActivity extends Activity {
    private Button showFilesButton;
    private ListView filesListView;

    private int FILENAME_IND = 0;
    private int PATH_IND = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filesystem_layout);

        showFilesButton = (Button) findViewById(R.id.showFilesButton);
        filesListView = (ListView) findViewById(R.id.filesListView);


        showFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                ArrayList<String> songs = new ArrayList<String>();
                ArrayList<String> songsPaths = new ArrayList<String>();
                while(cursor.moveToNext()){
                    songs.add(cursor.getString(FILENAME_IND));
                    songsPaths.add(cursor.getString(PATH_IND));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.item_layout,
                                                                                                R.id.label,
                                                                                                songs);
                filesListView.setAdapter(adapter);
            }
        });

        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        showFilesButton.performClick();
    }
}
