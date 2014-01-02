package com.lebs;

public class Song {
    String path;
    String name;

    Song (String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Override
    public String toString() {
        return name;
    }
}
