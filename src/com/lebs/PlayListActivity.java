package com.lebs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class PlayListActivity extends Activity {
    private final String FIRST_RUN = "FIRST_RUN";

    private SharedPreferences preferences;
    private ListView listView;
    private PlayListItemArrayAdapter adapter;
    private ArrayList<Song> songList;

    private void updatePlaylist() {
        ScanPathsTask task = new ScanPathsTask(PlayListActivity.this, this, listView, songList, adapter);
        task.setFullScanning();
        task.execute();
    }

    private void updatePlaylist(String filePath) {
        File selectedFile = new File(filePath);
        String selectedFileDirectory = selectedFile.getParent();

        ScanPathsTask task = new ScanPathsTask(PlayListActivity.this, this, listView, songList, adapter);
        task.setPathToScan(new String[]{selectedFileDirectory});
        task.execute();
    }

    private void removeSongFromList(int position) {
        songList.remove(position);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void tryToRunMediaScanner() {
        boolean firstRun = preferences.getBoolean(FIRST_RUN, true);
        if( firstRun ) {
            preferences.edit().putBoolean(FIRST_RUN, false).commit();
            new AlertDialog.Builder(PlayListActivity.this)
                    .setTitle("Application first run")
                    .setMessage("Run media scanner to find all your music?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            updatePlaylist();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .show();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
        preferences = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);

        listView = (ListView) findViewById(R.id.listView);

        FileSystemManager manager = new FileSystemManager(this);
        songList = manager.getSongs();
        adapter = new PlayListItemArrayAdapter(this, songList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Song song = (Song) parent.getItemAtPosition(position);
                if( !Utilities.fileExists(song.path) ){
                    new AlertDialog.Builder(PlayListActivity.this)
                        .setTitle("Read error")
                        .setMessage("Rescan media files to repair playlist?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                updatePlaylist(song.path);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeSongFromList(position);
                            }
                        })
                        .show();
                    return;
                }

                Intent intent = new Intent(PlayListActivity.this, PlayerActivity.class);
                intent.putExtra("name", song.name); // TODO: make it with Song object
                intent.putExtra("path", song.path);
                intent.putExtra("artist", song.artist);

                PlayListActivity.this.startActivity(intent);
            }
        });
        tryToRunMediaScanner();
    }
}