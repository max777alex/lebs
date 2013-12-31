package com.lebs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public class ActionActivity extends Activity {
    EditText editText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_layout);

        editText = (EditText) findViewById(R.id.editText);

        Intent myIntent = getIntent();
        String song = myIntent.getStringExtra("song");

        editText.setText(song);
    }
}