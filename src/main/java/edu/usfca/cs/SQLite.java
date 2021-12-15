package edu.usfca.cs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class SQLite {
    Library library;
    public String artistName;
    public String songName;
    public ArrayList<Song> importedSongs;
    public ArrayList<Artist> importedArtists;
    public ArrayList<Album> importedAlbums;
    public Scanner input = new Scanner(System.in);
    String userInput;
    MusicBrainz musicBrainz = new MusicBrainz();
    AudioDB audioDB = new AudioDB();
    UserPrompt userPrompt;
    Display display;


    public void initiateLibrary(Statement statement) throws SQLException {
        ArrayList<Song> songs = new ArrayList<>();
        ArrayList<Artist> artists = new ArrayList<>();
        ArrayList<Album> albums = new ArrayList<>();
        createMusicObjectsFromSQL(statement, songs, artists, albums);
        this.library = new Library(songs, artists, albums);
    }

    public void artistNameRequest() {
        System.out.println("Please enter the name of your favorite artist here, and then we will automatically fill in some details of that artist for you.");
        artistName = input.nextLine();
    }

    public void mainMenu() {
        Connection connection = null;
        String userInput;

        System.out.println("------------------------MAIN MENU------------------------");
        System.out.println("-1- Check all songs, artists, albums in the library.");
        System.out.println("-2- Check a specific part of the library.");
        System.out.println("-3- Import a song into the library using SONG NAME only.");
        System.out.println("-4- Import a song into the library using both SONG NAME and ARTIST NAME.");
        System.out.println("-5- Import an artist into the library.");
        System.out.println("-6- Import an album into the library.");
        System.out.println("-7- Generate a playlist.");
        System.out.println("-8- Exit.");
        System.out.print("Your choice: ");
        userInput = this.input.nextLine();

        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:music.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            //sqLite.createSQLTables(statement);
            this.initiateLibrary(statement);

            switch (userInput) {
                case "1":
                    System.out.println("All stuffs in the current library:");
                    display.displaySongs(this.library.getSongs());
                    display.displayArtists(this.library.getArtists());
                    display.displayAlbums(this.library.getAlbums());
                    this.mainMenu();
                    break;
                case "2":
                    display.displayPartOfLibrary(library);
                    break;
                case "3":
                    System.out.println("Please enter the name of a song here, and then we will automatically fill in details of that song for you: ");
                    userInput = input.nextLine();
                    musicBrainz.insertSongsFromMusicBrainz(statement, userInput, library);
                    this.mainMenu();
                    break;
                default:
                    System.out.println("Invalid input. Please enter a number from 1~8.");
                    this.mainMenu();
                    break;
            }
            this.artistNameRequest();
            audioDB.insertArtistFromAudioDB(statement, this.artistName, library);

            display.displaySongs(this.library.getSongs());
            display.displayArtists(this.library.getArtists());
            display.displayAlbums(this.library.getAlbums());

        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
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

    public static void main(String[] args) {
        SQLite sqLite = new SQLite();

        System.out.println("Hello! Welcome to our Music Garden.");
        System.out.println("There are a number of options you can choose to play with it.");
        sqLite.mainMenu();
    }

    public void createSQLTables(Statement statement) throws SQLException {
        statement.executeUpdate("drop table if exists songs");
        statement.executeUpdate("create table songs (id string NOT NULL PRIMARY KEY, name string NOT NULL, MusicBrainz_ID string Not null, artist integer, album integer, songGenre string, songReleaseDate string, songLanguage string)");
        statement.executeUpdate("drop table if exists artists");
        statement.executeUpdate("create table artists (id string NOT NULL PRIMARY KEY, name string NOT NULL, AudioDB_ID string Not null, nAlbums integer, nSongs integer, mood string, artistStyle string, artistArea string)");
        statement.executeUpdate("drop table if exists albums");
        statement.executeUpdate("create table albums (id string NOT NULL PRIMARY KEY, name string NOT NULL, AudioDB_ID string Not null, artist integer, nSongs integer, albumReleaseDate string,  albumLanguage string)");
    }

    public void insertIntoTableSongs(Statement statement, ArrayList<Song> songs) throws SQLException {
        for (Song s : songs) {
            statement.executeUpdate(s.toSQL());
        }
    }

    public void insertIntoTablesArtists(Statement statement, ArrayList<Artist> artists) throws SQLException {
        for (Artist a : artists) {
            statement.executeUpdate(a.toSQL());
        }
    }

    public void insertIntoTablesAlbums(Statement statement, ArrayList<Album> albums) throws SQLException {
        for (Album a : albums) {
            statement.executeUpdate(a.toSQL());
        }
    }

    public void displaySQLTableSongs(ResultSet rsSong) throws SQLException {
        System.out.println(" * * * TABLE songs * * *");
        while (rsSong.next()) {
            System.out.println(rsSong.getInt("id") + " | " + rsSong.getString("name") + " | "
                    + rsSong.getInt("artist") + " | " + rsSong.getInt("album"));
        }
    }

    public void displaySQLTableArtists(ResultSet rsArtist) throws SQLException {
        System.out.println(" * * * TABLE artists * * *");
        while (rsArtist.next()) {
            System.out.println(rsArtist.getInt("id") + " | " + rsArtist.getString("name") + " | "
                    + rsArtist.getInt("nAlbums") + " | " + rsArtist.getInt("nSongs"));
        }
    }

    public void displaySQLTableAlbums(ResultSet rsAlbum) throws SQLException {
        System.out.println(" * * * TABLE albums * * *");
        while (rsAlbum.next()) {
            System.out.println(rsAlbum.getInt("id") + " | " + rsAlbum.getString("name") + " | "
                    + rsAlbum.getInt("artist") + " | " + rsAlbum.getInt("nSongs"));
        }
    }

    /* Create music objects from SQL database*/
    public void createMusicObjectsFromSQL(Statement statement, ArrayList<Song> songsCreatedFromSQL,
                                          ArrayList<Artist> artistsCreatedFromSQL,
                                          ArrayList<Album> albumsCreatedFromSQL) throws SQLException {
        ResultSet rsArtist1 = statement.executeQuery("select * from artists");
        while (rsArtist1.next()) {
            Artist a = new Artist();
            a.fromSQL(rsArtist1);
            artistsCreatedFromSQL.add(a);
        }
        ResultSet rsAlbum1 = statement.executeQuery("select * from albums");
        while (rsAlbum1.next()) {
            Album a = new Album();
            a.fromSQL(rsAlbum1, artistsCreatedFromSQL);
            albumsCreatedFromSQL.add(a);
        }
        ResultSet rsSong1 = statement.executeQuery("select * from songs");
        while (rsSong1.next()) {
            Song s = new Song();
            s.fromSQL(rsSong1, artistsCreatedFromSQL, albumsCreatedFromSQL);
            songsCreatedFromSQL.add(s);
        }

        /* add certain song and album to certain artist from table songs */
        for (Artist artist : artistsCreatedFromSQL) {
            ResultSet rsSongArtistFilter = statement.executeQuery("select * from songs where artist == " + artist.getEntityID());
            while (rsSongArtistFilter.next()) {
                for (Song s : songsCreatedFromSQL) {
                    if (s.getEntityID() == rsSongArtistFilter.getInt("id")) {
                        artist.addSong(s);
                    }
                }
                for (Album a : albumsCreatedFromSQL) {
                    if (a.getEntityID() == rsSongArtistFilter.getInt("album")) {
                        if (!artist.getAlbums().contains(a)) {
                            artist.addAlbum(a);
                        }
                    }
                }
            }
        }

        /* add certain song and artist to certain album from table songs */
        for (Album album : albumsCreatedFromSQL) {
            ResultSet rsSongAlbumFilter = statement.executeQuery("select * from songs where album == " + album.getEntityID());
            while (rsSongAlbumFilter.next()) {
                for (Song s : songsCreatedFromSQL) {
                    if (s.getEntityID() == rsSongAlbumFilter.getInt("id")) {
                        album.addSong(s);
                    }
                }
                for (Artist a : artistsCreatedFromSQL) {
                    if (a.getEntityID() == rsSongAlbumFilter.getInt("artist")) {
                        album.setArtist(a);
                        break;
                    }
                }
            }
        }
    }
}