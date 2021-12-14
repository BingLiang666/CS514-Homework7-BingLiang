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

class SQLiteTest {
    SQLite sqLite;
    ArrayList<Song> songs;
    ArrayList<Artist> artists;
    ArrayList<Album> albums;
    Statement statement;
    Connection connection;

    @BeforeEach
    void setUp() {
        sqLite = new SQLite();
        songs = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();
        connection = null;
    }

    @Test
    void initiateLibrary() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:music.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            sqLite.initiateLibrary(statement);
            sqLite.displaySongs(sqLite.library.getSongs());
            sqLite.displayArtists(sqLite.library.getArtists());
            sqLite.displayAlbums(sqLite.library.getAlbums());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Test
    void insertArtistFromAudioDB() {
    }

    @Test
    void insertSongsFromMusicBrainz() {
    }

    @Test
    void displaySongsAfterCertainYear() {
        sqLite.displaySongsAfterCertainYear();
    }

    @Test
    void buildConnectionWithMusicBrainz() {
        StringBuilder response = new StringBuilder();
        String songName = "red";
        sqLite.buildConnectionWithMusicBrainz(songName, response);
    }


    @Test
    void testInsertArtistFromAudioDB() {
        String artistName = "Taylor Swift";
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:test.db");
            statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            sqLite.createSQLTables(statement);
            sqLite.initiateLibrary(statement);
            sqLite.insertArtistFromAudioDB(statement, artistName);
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
    }
}