package com.lebs;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class ActionActivity extends Activity {
    EditText editText;
    Button buttonPlay;
    Button buttonStop;
    MediaPlayer player;
    Uri myUri;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_layout);

        editText = (EditText) findViewById(R.id.editText);
        buttonPlay = (Button) findViewById(R.id.play);
        buttonStop = (Button) findViewById(R.id.stop);

        Intent myIntent = getIntent();
        String song = myIntent.getStringExtra("song");
        editText.setText(song);
        myUri = Uri.parse(myIntent.getStringExtra("path"));

        buttonPlay.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(player != null)
                    player.reset();
                else
                {
                    player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                }

                try {
                    player.setDataSource(getApplicationContext(), myUri);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(ActionActivity.this, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
                } catch (SecurityException e) {
                    Toast.makeText(ActionActivity.this, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
                } catch (IllegalStateException e) {
                    Toast.makeText(ActionActivity.this, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    player.prepare();
                } catch (IllegalStateException e) {
                    Toast.makeText(ActionActivity.this, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(ActionActivity.this, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
                }
                player.start();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(player != null && player.isPlaying()){
                    player.stop();
                }
            }
        });
    }

    public void onPause() {
        super.onPause();
        if(player != null)
            player.release();
    }
}