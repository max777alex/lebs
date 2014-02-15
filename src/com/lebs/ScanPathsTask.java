package com.lebs;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class ScanPathsTask extends AsyncTask <Void, Void, Void>{
    private final String audioMimeType[]        = new String[]{"audio/*"};
    private final String primaryStoragePath     = Environment.getExternalStorageDirectory().getAbsolutePath();
    private final String secondaryStoragePath   = System.getenv("SECONDARY_STORAGE");

    private PlayListActivity activity;
    private ListView listView;
    private ArrayList<Song> songList;
    private PlayListItemArrayAdapter adapter;
    private Context context = null;
    private ProgressDialog dialog;
    private String pathsToScan[];
    private int pathsToScanCount = 0;

    ScanPathsTask(Context context,  PlayListActivity activity, ListView listView,
                                    ArrayList<Song> songList, PlayListItemArrayAdapter adapter )
    {
        this.context = context;
        this.activity = activity;
        this.listView = listView;
        this.songList = songList;
        this.adapter = adapter;
        dialog = new ProgressDialog(context);
    }

    private void updateListView() {
        FileSystemManager manager = new FileSystemManager(context);
        songList.clear();
        songList.addAll(manager.getSongs());

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Refreshing...");
        dialog.show();


        activity.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

        listView.post(new Runnable() {
            @Override
            public void run() {
                if( dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
    }

    public void setPathToScan(String[] pathsToScan) {
        this.pathsToScan = pathsToScan;
        pathsToScanCount = pathsToScan.length;
    }

    public void setFullScanning() {
        ArrayList <String> fullScanningPaths = new ArrayList<String>();
        pathsToScanCount = 0;
        if( primaryStoragePath != null ) {
            fullScanningPaths.add(primaryStoragePath);
            ++pathsToScanCount;
        }
        if( secondaryStoragePath != null ) {
            fullScanningPaths.add(secondaryStoragePath);
            ++pathsToScanCount;
        }
        this.pathsToScan = new String[fullScanningPaths.size()];
        this.pathsToScan = fullScanningPaths.toArray(this.pathsToScan);
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Scanning...");
        this.dialog.show();
        this.dialog.setCancelable(false);
        for( int pathInd = 0; pathInd < pathsToScan.length; ++pathInd ) {
            Log.i("ExtendedMediaScanner", String.format("Start scannning %s", pathsToScan[pathInd]));
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        for( int pathInd = 0; pathInd < pathsToScan.length; ++pathInd ) {
            MediaScannerConnection.scanFile(context,
                                            new String[]{pathsToScan[pathInd]},
                                            audioMimeType,
                                            new MediaScannerConnection.OnScanCompletedListener()
            {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExtendedMediaScanner", String.format("Scanned %s", path));
                    --pathsToScanCount;
                    if( dialog.isShowing() && pathsToScanCount == 0 ) {
                        dialog.dismiss();
                    }
                }
            });
        }
        while( pathsToScanCount > 0 );
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        updateListView();
    }
}
