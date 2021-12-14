package edu.usfca.cs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SongIntervalTest {
    SongInterval song;

    @Test
    public void toStringTest () {
        song = new SongInterval(300);
        System.out.println(song.toString());
    }
}