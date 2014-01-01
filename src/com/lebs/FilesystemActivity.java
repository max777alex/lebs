package com.lebs;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Райан on 01.01.14.
 */
public class FilesystemActivity extends Activity {
    private String rootPath = "/"; //Environment.getExternalStorageDirectory().getAbsolutePath();
    private String currentPath = rootPath;
    private String previousPath = currentPath;

    private Button showFilesButton;
    private ListView filesListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filesystem_layout);

        showFilesButton = (Button) findViewById(R.id.showFilesButton);
        filesListView = (ListView) findViewById(R.id.filesListView);

        showFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(currentPath);
                List<File> currentFilesList = new ArrayList<File>();
                if(!rootPath.equals(previousPath)) {
                    currentFilesList.add(new File(rootPath));
                    currentFilesList.add(new File(previousPath));
                } else {
                    currentFilesList.add(new File(rootPath));
                }
                currentFilesList.addAll(Arrays.asList(f.listFiles()));
                ArrayAdapter<File> adapter = new ArrayAdapter<File>(getApplicationContext(),
                                                                R.layout.item_layout,
                                                                R.id.label, currentFilesList);
                filesListView.setAdapter(adapter);

            }
        });

        filesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File currentFile = (File)parent.getItemAtPosition(position);
                if( currentFile.isDirectory() ) {
                    previousPath = currentPath;
                    currentPath = currentFile.getAbsolutePath();
                    showFilesButton.performClick();
                }
            }
        });
    }
}
