package com.lebs;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
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

public class PlayerActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    TextView songTitle;
    ImageButton buttonPlay;
    MediaPlayer player;
    Uri myUri;
    String songText = "Please, wait!";
    TextView songTextView;
    SeekBar songProgressBar;
    // Handler to update UI timer, progress bar etc,.
    Handler mHandler = new Handler();
    Utilities utils;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        setContentView(R.layout.player);

        songTitle = (TextView) findViewById(R.id.songTitle);
        buttonPlay = (ImageButton) findViewById(R.id.btnPlay);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        utils = new Utilities();

        songProgressBar.setOnSeekBarChangeListener(this); // Important

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

                if(player == null)
                    return;

                // check for already playing
                if(player.isPlaying()) {
                    player.pause();
                    // Changing button image to play button
                    buttonPlay.setImageResource(R.drawable.btn_play);
                } else {
                    // Resume song
                    player.start();
                    // Changing button image to pause button
                    buttonPlay.setImageResource(R.drawable.btn_pause);
                }
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                buttonPlay.setImageResource(R.drawable.btn_play);
            }
        });

        songTextView = (TextView) findViewById(R.id.songText);

        new Thread(new Runnable() {
            public void run() {
                String html = getSongTextHtml(song);
                songText = String.valueOf(Html.fromHtml(html));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        songTextView.setText(songText);
                    }
                });
            }
        }).start();
    }

    public void onDestroy() {
        super.onDestroy();
        if(player != null)
            player.release();
    }

    public void onPause() {
        super.onPause();
        if(player != null)
            player.pause();
        buttonPlay.setImageResource(R.drawable.btn_play);
    }

    public String norm(String s) {
        return s.replaceAll("[^a-zA-Z0-9]","").toLowerCase();
    }

    public String getUri(Song song) {
        return "http://www.azlyrics.com/lyrics/" + norm(song.artist) + "/" + norm(song.name) + ".html";
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getPageHtml(String uri) throws IOException {

        if(!isNetworkAvailable())
            return "<!-- start of lyrics -->We need an internet to find a text of the song:(<!-- end of lyrics -->";

        HttpParams httpParameters = new BasicHttpParams();
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

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = player.getDuration();
            long currentDuration = player.getCurrentPosition();

            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = player.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        player.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }
}