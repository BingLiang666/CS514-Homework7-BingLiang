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
    Display display;

    @BeforeEach
    void setUp() {
        display = new Display();
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
            display.displaySongs(sqLite.library.getSongs());
            display.displayArtists(sqLite.library.getArtists());
            display.displayAlbums(sqLite.library.getAlbums());
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

}