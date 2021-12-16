package edu.usfca.cs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MusicBrainzTest {
    SQLite sqLite;
    ArrayList<Song> songs;
    ArrayList<Artist> artists;
    ArrayList<Album> albums;
    Statement statement;
    Connection connection;
    MusicBrainz musicBrainz;
    StringBuilder response;
    Library library;
    Song song1, song2;
    Album album1, album2;
    Artist artist1, artist2, newArtist;

    @BeforeEach
    void setUp() {
        library = new Library();
        sqLite = new SQLite();
        songs = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();
        connection = null;
        musicBrainz = new MusicBrainz();
        response = new StringBuilder();
        song1 = new Song(101, "happy birthday");
        song2 = new Song(102, "good day");
        artist1 = new Artist(201, "adele");
        artist2 = new Artist(202, "lim");
        artist1.setAudioDB_ID("111493");
        artist2.setAudioDB_ID("11112");
        album1 = new Album(301, "little prince");
        album2 = new Album(302, "little princess");
        song1.setPerformer(artist1);
        song2.setPerformer(artist2);
        song1.setAlbum(album1);
        song2.setAlbum(album2);
        artist1.addSong(song1);
        artist1.addSong(song2);
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
    void insertSongsFromMusicBrainz() {
    }

    @Test
    void buildConnectionWithMusicBrainz() {
        StringBuilder response = new StringBuilder();
        String songName = "red";
        musicBrainz.buildConnectionWithMusicBrainz(songName, response);
    }

    @Test
    void grabSongs() {
    }

    @Test
    void selectSong() {
    }

    @Test
    void testGrabSongs() {
        String songName = "red";
        musicBrainz.buildConnectionWithMusicBrainz(songName, response);
        musicBrainz.grabSongs(response, songName, songs, library);
    }
}