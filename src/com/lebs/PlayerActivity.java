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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
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
    String shownText = songText;

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
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
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
                shownText = songText;
                //shownText = songText.replaceAll("[a-zA-Z\']", "*");
//                shownText = processInputWord("love", songText, shownText);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        songTextView.setText(shownText);
                    }
                });
            }
        }).start();
    }

    public String processInputWord(String word, String songText, String shownText) {

        int ind = songText.toLowerCase().indexOf(word);

        while(ind != -1)
        {
            shownText = shownText.substring(0, ind) +
                    songText.substring(ind, ind + word.length()) +
                    shownText.substring(ind + word.length());
            ind = songText.toLowerCase().indexOf(word, ind + 1);
        }

        return shownText;
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
        s = s.toLowerCase().trim();

        if(s.startsWith("the ")) // TODO: remove this, back-end's responsibility!
            s = s.substring(4);

        return s.replaceAll("[^a-zA-Z0-9]", "");
    }

    public String getUri(Song song) {
        //return "http://www.azlyrics.com/lyrics/" + norm(song.artist) + "/" + norm(song.name) + ".html";
//        String songUrl = "http://10.0.3.2/web/app_dev.php/lyrics/" + norm(song.artist) + "/" + norm(song.name); //10.0.3.2 for genymotion, 10.0.2.2
        String songUrl = "http://llbs.eu1.frbit.net/lyrics/" + norm(song.artist) + "/" + norm(song.name); // TODO: to manifest, or other constants
        Log.e("SONG_URL = ", songUrl);
        return songUrl;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getPageHtml(String uri) throws IOException {    // TODO: to class

        if(!isNetworkAvailable())
            return "We need the Internet to find a text of the song:(";

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 7000); // 7s max for connection
        HttpConnectionParams.setSoTimeout(httpParameters, 9000); // 9s max to get data

        HttpClient client = new DefaultHttpClient(httpParameters);
        HttpGet request = new HttpGet(uri);

        HttpResponse response = client.execute(request);

        if(response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK)
            return "We can not find any text:(";

        String html;
        InputStream in = response.getEntity().getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder str = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null)
            str.append(line);
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
        return pageHtml;
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