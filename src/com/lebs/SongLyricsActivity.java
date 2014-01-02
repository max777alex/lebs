package com.lebs;


import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class SongLyricsActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_lyrics_layout);

        LinearLayout vg = (LinearLayout)findViewById(R.id.horizLayout);
        Spinner[] spinners = new Spinner[40];

        for(int i = 0; i < 40; i++) {
            spinners[i] = new Spinner(this);
            ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item,
                    new String[] { "Apple" + i, "Peach" + i, "Banana" + i });
            spinners[i].setAdapter(spinnerArrayAdapter);
            spinners[i].setLayoutParams(new ViewGroup.LayoutParams(150,150));
            vg.addView(spinners[i]);
        }
    }
}
