package com.lebs;

import android.app.Activity;
import android.app.AlertDialog;
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
    private ArrayList<Song> songList;

    private boolean checkFile(String filePath) {
        File selectedFile = new File(filePath);
        if( !selectedFile.exists() ) {
            new AlertDialog.Builder(PlayListActivity.this)
                    .setTitle("Read error")
                    .setMessage("Selected file does not exist! It is recommended to rescan media files.")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return false;
        }
        return true;
    }

    private void updatePlaylist(String filePath) {
        File selectedFile = new File(filePath);
        final String fileDirectory = selectedFile.getParent();

        MediaScannerConnection.scanFile(PlayListActivity.this,
            new String[]{fileDirectory}, null,
            new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(final String path, final Uri uri) {
                    Log.i("SD-Card is scanned", String.format("Scanned path %s -> URI = %s", path, uri.toString()));

                    FileSystemManager manager = new FileSystemManager(PlayListActivity.this);
                    songList.clear();
                    songList.addAll(manager.getSongs());

                    runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            });
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
                Song song = (Song) parent.getItemAtPosition(position);
                if( !checkFile(song.path) ){
                    updatePlaylist(song.path);
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