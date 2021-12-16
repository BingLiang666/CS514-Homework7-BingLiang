package edu.usfca.cs;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SQLite {
    protected Library library;
    protected Scanner input = new Scanner(System.in);
    protected MusicBrainz musicBrainz = new MusicBrainz();
    protected AudioDB audioDB = new AudioDB();
    protected Display display = new Display();
    protected IsInteger isInteger = new IsInteger();
    Playlist playlist = new Playlist();

    public static void main(String[] args) {
        SQLite sqLite = new SQLite();
        System.out.println("Hello! Welcome to our Music Garden.");
        System.out.println("There are a number of options you can choose to play with it.");
        sqLite.mainMenu();
    }

    /**
     *
     * @param statement  SQLite statement
     * @throws SQLException
     *
     * This creates a library which contains three arrayLists of songs, artists and albums from the SQLite data.
     *
     */
    public void initiateLibrary(Statement statement) throws SQLException {
        ArrayList<Song> songs = new ArrayList<>();
        ArrayList<Artist> artists = new ArrayList<>();
        ArrayList<Album> albums = new ArrayList<>();
        createMusicObjectsFromSQL(statement, songs, artists, albums);
        this.library = new Library(songs, artists, albums);
    }

    /**
     * This is the user interface that give users options to choose from.
     */
    public void mainMenu() {
        Connection connection = null;
        String userInput;
        Artist tempArtist;
        Album tempAlbum;

        System.out.println("------------------------MAIN MENU------------------------");
        System.out.println("-1- Check All Songs, Artists, Albums In The Library");
        System.out.println("-2- Check A Specific Part Of The Library");
        System.out.println("-3- Import A Song Into The Library Using SONG NAME");
        System.out.println("-4- Import An Artist Into The Library Using ARTIST NAME");
        System.out.println("-5- Import An Album Into The Library Using ARTIST NAME And ALBUM RELEASE DATE");
        System.out.println("-6- Generate A Playlist");
        System.out.println("-7- Shuffle A Playlist");
        System.out.println("-8- Exit");
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
                case "2":
                    display.displayPartOfLibrary(library);
                    this.mainMenu();
                case "3":
                    System.out.println("Please enter the name of a song here, and then we will automatically fill in details of that song for you: ");
                    userInput = input.nextLine();
                    musicBrainz.insertSongsFromMusicBrainz(statement, userInput, library);
                    this.mainMenu();
                case "4":
                    System.out.println("Please enter the name of an artist here, and then we will automatically fill in details of that artist for you: ");
                    userInput = input.nextLine();
                    tempArtist = audioDB.insertArtistFromAudioDB(userInput, library);
                    if (tempArtist != null) {
                        statement.executeUpdate(tempArtist.toSQL());
                        System.out.println("The artist \"" + tempArtist.getName() + "\" has been successfully imported into the library." );
                    } else {
                        System.out.println("So there is not any new artist needed to be imported this time.");
                    }
                    this.mainMenu();
                case "5":
                    System.out.println("Please enter the ARTIST NAME and ALBUM RELEASE DATE here, and then we will automatically fill in details of that album for you: ");
                    System.out.print("ARTIST NAME: ");
                    userInput = input.nextLine();
                    tempArtist = audioDB.insertArtistFromAudioDB(userInput, library);
                    if (tempArtist != null) {
                        System.out.println("The artist of the album is currently not inside the library. Let's import the artist first!");
                        statement.executeUpdate(tempArtist.toSQL());
                        System.out.println("The artist \"" + tempArtist.getName() + "\" has been successfully imported into the library.");
                    } else {
                        for (Artist a: library.getArtists()) {
                            if (a.getName().equalsIgnoreCase(userInput)) {
                                tempArtist = a;
                            }
                        }
                    }
                    if (tempArtist != null) {
                        System.out.println("Now please enter the ALBUM RELEASE DATE. (Note: The number represented for year should be smaller than or equal to 2021 and in 4-digit format, eg, 2020.)");
                        userInput = input.nextLine();
                        while (!isInteger.isInteger(userInput) && userInput.length() != 4) {
                            System.out.println("Mal-format of year you just entered. Please enter again.");
                            userInput = input.nextLine();
                        }
                        tempAlbum = audioDB.insertAlbumFromAudioDB(tempArtist, userInput, library);
                        if (tempAlbum != null) {
                            if (tempAlbum.getName() != null) {
                                statement.executeUpdate("update artists set nAlbums = \'" + tempAlbum.getArtist().getAlbums().size() + "\' where id = \'" + tempAlbum.getArtist().getEntityID() + "\';");
                                statement.executeUpdate(tempAlbum.toSQL());
                                System.out.println("The album \"" + tempAlbum.getName() + "\" has been successfully imported into the library." );
                            } else {
                                System.out.println("So there is not any new album needed to be imported this time.");
                            }
                        } else {
                            System.out.println("So there is not any new album needed to be imported this time.");
                        }
                    } else {
                        System.out.println("So there is not any new album needed to be imported this time.");
                    }
                    this.mainMenu();
                case "6":
                    playlist = playlist.generatePlaylist(library);
                    this.mainMenu();
                case "7" :
                    if (playlist.getListOfSongs().size() == 0) {
                        System.out.println("Ops...It seems like you have not created a playlist yet. Please create it first!");
                    } else {
                        playlist = playlist.shufflePlaylist(library, input);
                    }
                    this.mainMenu();
                case "8" :
                    break;
                default:
                    System.out.println("Invalid input. Please enter a number from 1~8.");
                    this.mainMenu();
            }
            System.out.println("It's so fun to play with you. See you next time!");
            System.out.println("Good luck!");
            System.out.println("\uD83D\uDE00");
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


    /**
     *
     * @param statement SQLite statement
     * @throws SQLException
     *
     * This initiates the tables of songs, albums and artists in SQLite.
     *
     */
    public void createSQLTables(Statement statement) throws SQLException {
        statement.executeUpdate("drop table if exists songs");
        statement.executeUpdate("create table songs (id string NOT NULL PRIMARY KEY, name string NOT NULL, MusicBrainz_ID string Not null, artist integer, album integer, songGenre string, songReleaseDate string, songLanguage string)");
        statement.executeUpdate("drop table if exists artists");
        statement.executeUpdate("create table artists (id string NOT NULL PRIMARY KEY, name string NOT NULL, AudioDB_ID string Not null, nAlbums integer, nSongs integer, mood string, artistStyle string, artistArea string)");
        statement.executeUpdate("drop table if exists albums");
        statement.executeUpdate("create table albums (id string NOT NULL PRIMARY KEY, name string NOT NULL, AudioDB_ID string Not null, artist integer, nSongs integer, albumReleaseDate string,  albumLanguage string)");
    }

    /**
     *
     * @param statement SQLite statement
     * @param songs arrayList of songs
     * @throws SQLException
     *
     * This inserts songs into SQLite.
     *
     */
    public void insertIntoTableSongs(Statement statement, ArrayList<Song> songs) throws SQLException {
        for (Song s : songs) {
            statement.executeUpdate(s.toSQL());
        }
    }

    /**
     *
     * @param statement SQLite statement
     * @param artists arrayList of artists
     * @throws SQLException
     *
     * This inserts artists into SQLite.
     *
     */
    public void insertIntoTablesArtists(Statement statement, ArrayList<Artist> artists) throws SQLException {
        for (Artist a : artists) {
            statement.executeUpdate(a.toSQL());
        }
    }

    /**
     *
     * @param statement SQLite statement
     * @param albums arrayList of albums
     * @throws SQLException
     *
     * This inserts albums into SQLite.
     *
     */
    public void insertIntoTablesAlbums(Statement statement, ArrayList<Album> albums) throws SQLException {
        for (Album a : albums) {
            statement.executeUpdate(a.toSQL());
        }
    }

    /**
     *
     * @param rsSong selection result of songs from SQLite
     * @throws SQLException
     *
     * This displays the information of songs in the SQLite table.
     *
     */
    public void displaySQLTableSongs(ResultSet rsSong) throws SQLException {
        System.out.println(" * * * TABLE songs * * *");
        while (rsSong.next()) {
            System.out.println(rsSong.getInt("id") + " | " + rsSong.getString("name") + " | "
                    + rsSong.getInt("artist") + " | " + rsSong.getInt("album"));
        }
    }

    /**
     *
     * @param rsArtist selection result of artists from SQLite
     * @throws SQLException
     *
     * This displays the information of artists in the SQLite table.
     *
     */
    public void displaySQLTableArtists(ResultSet rsArtist) throws SQLException {
        System.out.println(" * * * TABLE artists * * *");
        while (rsArtist.next()) {
            System.out.println(rsArtist.getInt("id") + " | " + rsArtist.getString("name") + " | "
                    + rsArtist.getInt("nAlbums") + " | " + rsArtist.getInt("nSongs"));
        }
    }

    /**
     *
     * @param rsAlbum selection result of albums from SQLite
     * @throws SQLException
     *
     * This displays the information of albums in the SQLite table.
     *
     */
    public void displaySQLTableAlbums(ResultSet rsAlbum) throws SQLException {
        System.out.println(" * * * TABLE albums * * *");
        while (rsAlbum.next()) {
            System.out.println(rsAlbum.getInt("id") + " | " + rsAlbum.getString("name") + " | "
                    + rsAlbum.getInt("artist") + " | " + rsAlbum.getInt("nSongs"));
        }
    }

    /**
     *
     * @param statement SQLite statement
     * @param songsCreatedFromSQL arrayList of songs
     * @param artistsCreatedFromSQL arrayList of artists
     * @param albumsCreatedFromSQL arrayList of albums
     * @throws SQLException
     *
     * This creates music objects from SQL database.
     *
     */
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
