package com.lebs;

public class Song {
    String path;
    String name;
    String artist;

    Song (String name, String path, String artist) {
        this.name = name;
        this.path = path;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return name;
    }
}
