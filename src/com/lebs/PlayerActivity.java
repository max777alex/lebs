package com.lebs;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.*;
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
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class PlayerActivity extends Activity {
    TextView songTitle;
    ImageButton buttonPlay;
    MediaPlayer player;
    Uri myUri;
    String songText = "Please, wait!";
    ListView songTextList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);

        songTitle = (TextView) findViewById(R.id.songTitle);
        buttonPlay = (ImageButton) findViewById(R.id.btnPlay);

        Intent myIntent = getIntent();
        String name = myIntent.getStringExtra("name");
        String path = myIntent.getStringExtra("path");
        String artist = myIntent.getStringExtra("artist");
        final Song song = new Song(name, path, artist);

        songTitle.setText(name);

        myUri = Uri.parse(path);
        player = MediaPlayer.create(this, myUri);

        buttonPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(player.isPlaying()) {
                    if(player != null) {
                        player.pause();
                        // Changing button image to play button
                        buttonPlay.setImageResource(R.drawable.btn_play);
                    }
                } else {
                    // Resume song
                    if(player != null) {
                        player.start();
                        // Changing button image to pause button
                        buttonPlay.setImageResource(R.drawable.btn_pause);
                    }
                }
            }
        });

        songTextList = (ListView) findViewById(R.id.songText);

        new Thread(new Runnable() {
            public void run() {
                String html = getSongTextHtml(song);
                songText = String.valueOf(Html.fromHtml(html));
                final String[] textLines = songText.split("\n");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SongTextArrayAdapter songTextArrayAdapter = new SongTextArrayAdapter(PlayerActivity.this,
                                textLines.length != 0 ? textLines : new String[] {"We can not find any text:("}); // TODO: change this
                        songTextList.setAdapter(songTextArrayAdapter);
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

    public String getPageHtml(String uri) throws IOException {
        HttpParams httpParameters = new BasicHttpParams();             // TODO: Write message, if there is no internet connection
        HttpConnectionParams.setConnectionTimeout(httpParameters, 7000); // 7s max for connection
        HttpConnectionParams.setSoTimeout(httpParameters, 9000); // 9s max to get data

        HttpClient client = new DefaultHttpClient(httpParameters);
        HttpGet request = new HttpGet(uri);

        HttpResponse response = client.execute(request);

        if(response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
            return "<!-- start of lyrics -->We can not find any text:(<!-- end of lyrics -->";

        String html;
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
        String uri = getUri(song);

        String pageHtml;
        try {
            pageHtml = getPageHtml(uri);
        } catch (IOException e) {
            return "";
        }

        int i = pageHtml.indexOf("<!-- start of lyrics -->");
        int j = pageHtml.indexOf("<!-- end of lyrics -->");

        if(i == -1 || j == -1 || i > j)
            return "";

        return pageHtml.substring(i, j);
    }
}