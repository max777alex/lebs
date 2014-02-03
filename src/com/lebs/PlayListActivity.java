package com.lebs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class PlayListActivity extends Activity {
    private ListView listView;
    private PlayListItemArrayAdapter adapter;
    private ProgressDialog scanningDialog = null;
    private ArrayList<Song> songList;

    private void updatePlaylist(String filePath) {
        File selectedFile = new File(filePath);
        String fileDirectory = selectedFile.getParent();

        MediaScannerConnection.scanFile(PlayListActivity.this,
            new String[]{fileDirectory},
            new String[]{"audio/*"},
            new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(final String path, final Uri uri) {
                    Log.i(  "Directory of selected file is scanned",
                            String.format("Scanned path %s -> URI = %s", path, uri.toString()));

                    FileSystemManager manager = new FileSystemManager(PlayListActivity.this);
                    songList.clear();
                    songList.addAll(manager.getSongs());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            scanningDialog.dismiss();
                        }
                    });

                }
            });
        scanningDialog = ProgressDialog.show(PlayListActivity.this, "", "Scanning...", true);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        listView = (ListView) findViewById(R.id.listView);

        FileSystemManager manager = new FileSystemManager(this);
        songList = manager.getSongs();
        adapter = new PlayListItemArrayAdapter(this, songList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Song song = (Song) parent.getItemAtPosition(position);
                if( !Utilities.fileExists(song.path) ){
                    new AlertDialog.Builder(PlayListActivity.this)
                        .setTitle("Read error")
                        .setMessage("Selected file does not exist! It is recommended to rescan media files")
                        .setPositiveButton("Rescan", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                updatePlaylist(song.path);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
    }
}