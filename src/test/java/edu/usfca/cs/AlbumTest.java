package edu.usfca.cs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {
    Album album1, album2, album3;
    Artist artist1, artist2;
    Song song1, song2;

    @BeforeEach
    void setUp() {
        album1 = new Album("Blue Sky");
        album2 = new Album("Baby Rabbit");
        album3 = new Album("Blue Sky");
        album1.songs = new ArrayList<>();
        artist1 = new Artist("Lily");
        artist2 = new Artist("Robert");
        album1.setArtist(artist1);
        album2.setArtist(artist2);
        album3.setArtist(artist1);
        song1 = new Song("Wonderful days", 200);
        song2 = new Song("Peace", 90);

    }


    @Test
    void addSong() {
        album1.addSong(song1);
        album1.addSong(song2);
        assertTrue(album1.songs.size() == 2);
    }

    @Test
    void equalsTest() {
        System.out.println(album1.getArtist().entityID);
        System.out.println(album2.getArtist().entityID);
        System.out.println(album3.getArtist().entityID);
        assertFalse(album1.equals(album2));
        assertTrue(album1.equals(album3));
    }

    @Test
    void testToString() {
        System.out.println(album1.toString());
        System.out.println(album2.toString());
    }

    @Test
    void toHTML() {
        album1.addSong(song1);
        System.out.println(album1.toHTML());
        System.out.println(album2.toHTML());
    }

    @Test
    void toXML() {
        System.out.println(album1.toXML());
        System.out.println(album2.toXML());
    }
}