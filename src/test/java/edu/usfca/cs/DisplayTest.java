package edu.usfca.cs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DisplayTest {
    Display display;
    SQLite sqLite;
    ArrayList<Song> songs;
    ArrayList<Artist> artists;
    ArrayList<Album> albums;
    Statement statement;
    Connection connection;
    AudioDB audioDB;
    Library library;
    Song song1, song2;
    Album album1, album2;
    Artist artist1, artist2, newArtist;

    @BeforeEach
    void setUp() {
        display = new Display();
        sqLite = new SQLite();
        songs = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();
        connection = null;
        audioDB = new AudioDB();
        library = new Library();
        song1 = new Song(101, "happy birthday");
        song2 = new Song(102, "good day");
        artist1 = new Artist(201, "adele");
        artist2 = new Artist(202, "KAT-TUN");
        artist1.setAudioDB_ID("111493");
        artist2.setAudioDB_ID("135856");
        album1 = new Album(301, "little prince");
        album2 = new Album(302, "little princess");
        song1.setSongReleaseDate("1998");
        song2.setSongReleaseDate("1999");
        song1.setPerformer(artist1);
        song2.setPerformer(artist2);
        song1.setAlbum(album1);
        song2.setAlbum(album2);
        artist1.addSong(song1);
        artist2.addSong(song2);
        artist1.addAlbum(album1);
        artist2.addAlbum(album2);
        album1.setArtist(artist1);
        album2.setArtist(artist2);
        album1.addSong(song1);
        album2.addSong(song2);
        library.addSong(song1);
        library.addSong(song2);
        library.addArtist(artist1);
        library.addArtist(artist2);
        library.addAlbum(album1);
        library.addAlbum(album2);
    }

    @Test
    void songsFromCertainYear() {
        display.songsFromCertainYear(library, 2000);
    }
}