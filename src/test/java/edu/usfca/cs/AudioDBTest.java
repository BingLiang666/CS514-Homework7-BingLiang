package edu.usfca.cs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.jdbc4.JDBC4Statement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AudioDBTest {
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
    void insertAlbumFromAudioDB() {
        String releaseYear = "2007";
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:test.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            sqLite.createSQLTables(statement);
            sqLite.initiateLibrary(statement);
            audioDB.insertAlbumFromAudioDB(statement, artist2, releaseYear, library);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        assertTrue(library.getAlbums().get(library.getAlbums().size() - 1).getName().equalsIgnoreCase("cartoon KAT-TUN II You"));
    }

    @Test
    void selectAlbum() {

    }

    @Test
    void InsertArtistFromAudioDB() {
        String artistName = "Taylor Swift";
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:test.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            sqLite.createSQLTables(statement);
            sqLite.initiateLibrary(statement);
            audioDB.insertArtistFromAudioDB(statement, artistName, library);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
        assertTrue(library.getArtists().get(library.getArtists().size() - 1).getName().equalsIgnoreCase(artistName));
    }
}