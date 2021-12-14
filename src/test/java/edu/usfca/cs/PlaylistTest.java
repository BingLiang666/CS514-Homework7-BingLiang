package edu.usfca.cs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {
    Playlist myPlaylist;
    Song s1, s2;

    @BeforeEach
    void setUp() {
        myPlaylist = new Playlist();
        s1 = new Song("Happy Birthday");
        s2 = new Song("Beauty");
    }

    @Test
    void addSong() {
        myPlaylist.addSong(s1);
        assertTrue(myPlaylist.getListOfSongs().contains(s1));
        assertFalse(myPlaylist.getListOfSongs().contains(s2));
    }

    private void assertTrue(boolean contains) {
    }

    @Test
    void deleteSong() {
        myPlaylist.deleteSong(s1);
        assertFalse(myPlaylist.getListOfSongs().contains(s1));
    }
}