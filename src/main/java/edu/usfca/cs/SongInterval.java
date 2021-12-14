package edu.usfca.cs;

public class SongInterval {
    private int length;

    public SongInterval(int l) {
        this.length = l;
    }

    public SongInterval() {}

    public String toString() {
        int minutes = length / 60;
        int seconds = length % 60;
        return String.format("%d:%d", minutes, seconds);
    }
}
