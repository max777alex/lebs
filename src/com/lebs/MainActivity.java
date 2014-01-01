package com.lebs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {
    private ListView listView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        listView = (ListView) findViewById(R.id.listView);

        String[] songNames = {"aaa", "asd asdSS sdf", "adsfsd", "asdd", "ssss",
                "aaa", "asd asdSS sdf", "adsfsd", "asdd", "ssss",
                "aaa", "asd asdSS sdf", "adsfsd", "asdd", "ssss",
                "aaa", "asd asdSS sdf", "adsfsd", "asdd", "ssss"};

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, songNames);

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
    }
}