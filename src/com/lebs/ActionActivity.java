package com.lebs;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ActionActivity extends Activity {
    EditText editText;
    Button buttonPlay;
    Button buttonStop;
    MediaPlayer player;
    Uri myUri;
    String songText = "Please, wait!";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_layout);

        editText = (EditText) findViewById(R.id.editText);
        buttonPlay = (Button) findViewById(R.id.play);
        buttonStop = (Button) findViewById(R.id.stop);

        Intent myIntent = getIntent();
        String name = myIntent.getStringExtra("name");
        String path = myIntent.getStringExtra("path");
        String artist = myIntent.getStringExtra("artist");
        final Song song = new Song(name, path, artist);

        editText.setText(name);

        myUri = Uri.parse(path);

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

        final TextView textView = (TextView) findViewById(R.id.textView);

        new Thread(new Runnable() {
            public void run() {
                String html = getSongTextHtml(song);
                songText = String.valueOf(Html.fromHtml(html));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(songText);
                    }
                });
            }
        }).start();
    }

    public void onPause() {
        super.onPause();
        if(player != null)
            player.release();
    }

    public String norm(String s) {
        return s.replaceAll("[^a-zA-Z0-9]","").toLowerCase();
    }

    public String getUri(Song song) {
        return "http://www.azlyrics.com/lyrics/" + norm(song.artist) + "/" + norm(song.name) + ".html";
    }

    public String getPageHtml(Song song) throws IOException {

        String uri = getUri(song);

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 7000); // 7s max for connection
        HttpConnectionParams.setSoTimeout(httpParameters, 9000); // 9s max to get data

        HttpClient client = new DefaultHttpClient(httpParameters);
        HttpGet request = new HttpGet(uri);

        HttpResponse response = client.execute(request);

        String html = "";
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder str = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null)
        {
            str.append(line);
        }
        in.close();
        html = str.toString();

        return html;
    }

    public String getSongTextHtml(Song song) {
        String pageHtml = "";
        try {
            pageHtml = getPageHtml(song);
        } catch (IOException e) {
            return "";
        }

        int i = pageHtml.indexOf("<!-- start of lyrics -->");
        int j = pageHtml.indexOf("<!-- end of lyrics -->");

        if(i == -1 || j == -1)
            return "";

        return pageHtml.substring(i, j);
    }
}