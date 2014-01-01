package com.lebs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {
    private ListView listView;
    private Button getFilesButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        listView = (ListView) findViewById(R.id.listView);

        String[] songNames = {"aaa", "asd asdSS sdf", "adsfsd", "asdd", "ssss",
                "aaa", "asd asdSS sdf", "adsfsd", "asdd", "ssss",
                "aaa", "asd asdSS sdf", "adsfsd", "asdd", "ssss",
                "aaa", "asd asdSS sdf", "adsfsd", "asdd", "ssss"};

        ItemArrayAdapter adapter = new ItemArrayAdapter(this, songNames);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                String song = (String) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, ActionActivity.class);
                intent.putExtra("song", song);

                MainActivity.this.startActivity(intent);
            }
        });

        getFilesButton = (Button)findViewById(R.id.getFilesButton);
        getFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filesystemIntent = new Intent(MainActivity.this,FilesystemActivity.class);
                startActivity(filesystemIntent);
            }
        });
    }
}