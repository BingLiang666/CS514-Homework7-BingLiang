package edu.usfca.cs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {
    Artist artist1, artist2;
    Song song1, song2;
    Album album1, album2;

    @BeforeEach
    void setUp() {
        artist1 = new Artist("Bob");
        artist2 = new Artist("Wool");
        artist1.songs = new ArrayList<>();
        artist1.albums = new ArrayList<>();
        artist2.songs = new ArrayList<>();
        artist2.albums = new ArrayList<>();
        song1 = new Song("Wonderful days", 200);
        song2 = new Song("Peace", 90);
        album1 = new Album("Blue sky");
        album2 = new Album("Rock World");
    }

    @Test
    void testID() {
        System.out.println(artist1.entityID + " " + artist1.getDateCreated());
        System.out.println(artist2.entityID);
    }

    @Test
    void addSongTest() {
        artist1.addSong(song1);
        artist1.addSong(song2);
        assertTrue(artist1.songs.size() == 2);
    }

    @Test
    void addAlbum() {
        artist1.addAlbum(album1);
        artist1.addAlbum(album2);
        assertTrue(artist1.albums.size() == 2);
    }

    @Test
    void equalsTest() {
        System.out.println(artist1.entityID);
        System.out.println(artist2.entityID);
        assertTrue(artist1.equals(artist1));
        assertFalse(artist1.equals(artist2));
    }

    @Test
    void ToStringTest() {

        System.out.println(artist1.toString());
        System.out.println(artist2.toString());
    }

    @Test
    void toHTMLTest() {
        System.out.println(artist1.toHTML());
        System.out.println(artist2.toHTML());
    }

    @Test
    void toXMLTest() {
        artist1.addSong(song1);
        artist2.addSong(song2);
        artist1.addAlbum(album1);
        artist2.addAlbum(album2);
        System.out.println(artist1.toXML());
        System.out.println(artist2.toXML());
    }
}