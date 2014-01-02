package com.lebs;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ActionActivity extends Activity {
    EditText editText;
    Button button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_layout);

        editText = (EditText) findViewById(R.id.editText);

        Intent myIntent = getIntent();
        String song = myIntent.getStringExtra("song");

        editText.setText(song);

        button = (Button) findViewById(R.id.play);

        button.setOnClickListener(new PlayButtonClickListener());
    }

    class PlayButtonClickListener implements View.OnClickListener {
        MediaPlayer player;

        @Override
        public void onClick(View v) {
            new Thread(new Runnable() {
                public void run() {
                    if(player == null || !player.isPlaying()) {
//                        player = MediaPlayer.create(ActionActivity.this, R.raw.numb_encore);
//                        player.start();
                    }
                    else {
//                        player.stop();
//                        player.release();
                    }
                }
            }).start();
        }
    }
}