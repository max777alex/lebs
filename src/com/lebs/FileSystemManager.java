package com.lebs;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

public class FileSystemManager {

    private final int FILENAME_IND = 0;
    private final int PATH_IND = 1;
    private final int ARTIST_IND = 2;

    private Context context;

    public FileSystemManager(Context context) {
        this.context = context;
    }

    public ArrayList<Song> getSongs() {
        ArrayList<Song> songs = new ArrayList<Song>();

        Cursor cursor = getMusicCursor();

        if(cursor == null)
            return songs;

        while( cursor.getCount() > 0 && cursor.moveToNext() ) {
            Song song = new Song(cursor.getString(FILENAME_IND),
                    cursor.getString(PATH_IND),
                    cursor.getString(ARTIST_IND));
            songs.add(song);
        }

        return songs;
    }

    private Cursor getMusicCursor() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };

        ContentResolver resolver = context.getContentResolver();
        return resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);
    }
}
