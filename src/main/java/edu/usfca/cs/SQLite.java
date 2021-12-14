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

    public void initiateLibrary(Statement statement) throws SQLException {
        ArrayList<Song> songs = new ArrayList<>();
        ArrayList<Artist> artists = new ArrayList<>();
        ArrayList<Album> albums = new ArrayList<>();
        createMusicObjectsFromSQL(statement, songs, artists, albums);
        this.library = new Library(songs, artists, albums);
    }

    public boolean isInteger(String input) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e ) {
            return false;
        }
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
                    this.displaySongs(this.library.getSongs());
                    this.displayArtists(this.library.getArtists());
                    this.displayAlbums(this.library.getAlbums());
                    this.mainMenu();
                    break;
                case "2":
                    this.displayPartOfLibrary();
                    break;
                case "3":
                    System.out.println("Please enter the name of a song here, and then we will automatically fill in details of that song for you: ");
                    userInput = input.nextLine();
                    this.insertSongsFromMusicBrainz(statement, userInput);
                    this.requestForAnotherInsertOfSongsWithName(statement);
                    this.mainMenu();
                    break;
                default:
                    System.out.println("Invalid input. Please enter a number from 1~8.");
                    this.mainMenu();
                    break;
            }
            this.artistNameRequest();
            this.insertArtistFromAudioDB(statement, this.artistName);

            this.displaySongs(this.library.getSongs());
            this.displayArtists(this.library.getArtists());
            this.displayAlbums(this.library.getAlbums());

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

    public void displayPartOfLibrary() {
        System.out.println("What kind of information would you prefer to see?(Please enter 1, 2, 3, or 4.)");
        System.out.println("-1- Song");
        System.out.println("-2- Artist");
        System.out.println("-3- Album");
        System.out.println("-4- Back to main menu.");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displayPartOfSongsInLibrary();
            case "2":
                this.displayPartOfArtistsInLibrary();
            case "3":
                this.displayPartOfAlbumsInLibrary();
            case "4":
                this.mainMenu();
        }
    }

    public void displayPartOfSongsInLibrary() {
        System.out.println("What kind of songs would you prefer to see?(Please enter 1, 2, or 3.)");
        System.out.println("-1- Songs after certain year.");
        System.out.println("-2- Songs from certain artist.");
        System.out.println("-3- Songs from certain album.");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displaySongsAfterCertainYear();
            case "2":
                this.displaySongsFromCertainArtist();
            case "3":
                this.displaySongsFromCertainAlbum();
            default:
                System.out.println("Invalid input. Please enter a number from 1~3.");
                this.displayPartOfSongsInLibrary();
                break;
        }
    }

    public void displaySongsAfterCertainYear() {
        System.out.println("Please enter that year.(Please enter a number smaller than 2021 in 4-digit format, eg, 2020.)");
        userInput = input.nextLine();
        int year;
        while (true) {
            if (isInteger(userInput)) {
                year = Integer.parseInt(userInput);
                if (year <= 2021) {
                    break;
                }
            } else {
                System.out.println("Mal-format of year you just entered. Please enter again.");
                userInput = input.nextLine();
            }
        }
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songGenre", "songReleaseDate", "songLanguage");
        for(Song s: library.getSongs()) {
            String yearOfSong = s.getSongReleaseDate().substring(0,4);
            if (Integer.parseInt(yearOfSong) <= year) {
                System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                        s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getGenre(), s.getSongReleaseDate(), s.getSongLanguage());
                index++;
            }
        }
        if (index == 0) {
            System.out.println("No qualified artists in the current database. You can add such artists later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        this.requestForAnotherSearch();
        this.mainMenu();
    }

    public void displaySongsFromCertainArtist () {
        System.out.println("Please enter the last name of an artist.");
        userInput = input.nextLine();
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songGenre", "songReleaseDate", "songLanguage");
        for(Song s: library.getSongs()) {
            String artistName = s.getPerformer().getName().toLowerCase(Locale.ROOT);
            if (artistName.contains(userInput.toLowerCase(Locale.ROOT))) {
                System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                        s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getGenre(), s.getSongReleaseDate(), s.getSongLanguage());
                index++;
            }
        }
        if (index == 0) {
            System.out.println("No qualified artists in the current database. You can add such artists later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        this.requestForAnotherSearch();
        this.mainMenu();
    }

    public void displaySongsFromCertainAlbum() {
        System.out.println("Please enter the name of an album.");
        userInput = input.nextLine();
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songGenre", "songReleaseDate", "songLanguage");
        for(Song s: library.getSongs()) {
            String albumName = s.getAlbum().getName().toLowerCase(Locale.ROOT);
            if (albumName.contains(userInput.toLowerCase(Locale.ROOT))) {
                System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                        s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getGenre(), s.getSongReleaseDate(), s.getSongLanguage());
                index++;
            }
        }
        if (index == 0) {
            System.out.println("No qualified artists in the current database. You can add such artists later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        this.requestForAnotherSearch();
        this.mainMenu();
    }

    public void requestForAnotherSearch() {
        System.out.println("Do you prefer to do another round of search? (Please enter Y/N)");
        String userChoice = input.nextLine();
        while (true) {
            if (userChoice.toLowerCase(Locale.ROOT).equals("y")) {
                this.displayPartOfLibrary();
            } else if (userChoice.toLowerCase(Locale.ROOT).equals("n")) {
                System.out.println("Great! Let's jump to the main menu.");
                break;
            } else {
                System.out.println("Invalid input. Please enter again.");
                userChoice = input.nextLine();
            }
        }
    }

    public void displayPartOfArtistsInLibrary() {
        System.out.println("What kind of artists would you prefer to see?(Please enter 1 or 2.)");
        System.out.println("-1- Artists with certain style.");
        System.out.println("-2- Artists from certain area.");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displayArtistsWithCertainStyle();
            case "2":
                this.displayArtistsFromCertainArea();
            default:
                System.out.println("Invalid input. Please enter a number from 1~2.");
                this.displayPartOfArtistsInLibrary();
                break;
        }
    }

    public void displayArtistsWithCertainStyle() {
        System.out.println("Please enter the style.");
        userInput = input.nextLine();
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-10s | %-12s | %s\n", "id", "name", "AudioDB_ID", "nAlbums",
                "nSongs", "mood", "artistStyle", "artistArea");
        for(Artist a: library.getArtists()) {
            String artistStyle = a.getStyle().toLowerCase(Locale.ROOT);
            if (artistStyle.contains(userInput.toLowerCase(Locale.ROOT))) {
                System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-10s | %-12s | %s\n", a.getEntityID(), a.getName(), a.getAudioDB_ID(),
                        a.getAlbums().size(), a.getSongs().size(), a.getMood(), a.getStyle(), a.getArtistArea());
                index++;
            }
        }
        if (index == 0) {
            System.out.println("No qualified artists in the current database. You can add such artists later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        this.requestForAnotherSearch();
        this.mainMenu();
    }

    public void displayArtistsFromCertainArea() {
        System.out.println("Please enter the area name.(ex. USA/UK...)");
        userInput = input.nextLine();
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-10s | %-12s | %s\n", "id", "name", "AudioDB_ID", "nAlbums",
                "nSongs", "mood", "artistStyle", "artistArea");
        for(Artist a: library.getArtists()) {
            String artistsArea = a.getArtistArea().toLowerCase(Locale.ROOT);
            if (artistsArea.contains("america")) {
                artistsArea = artistsArea.replace("america", "usa");
            } else if (artistsArea.contains("united states")) {
                artistsArea = artistsArea.replace("united states", "usa");
            } else if (artistsArea.contains("england")) {
                artistsArea = artistsArea.replace("england", "uk");
            }
            if (artistsArea.contains(userInput.toLowerCase(Locale.ROOT))) {
                System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-10s | %-12s | %s\n", a.getEntityID(), a.getName(), a.getAudioDB_ID(),
                        a.getAlbums().size(), a.getSongs().size(), a.getMood(), a.getStyle(), a.getArtistArea());
                index++;
            }
        }
        if (index == 0) {
            System.out.println("No qualified artists in the current database. You can add such artists later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        this.requestForAnotherSearch();
        this.mainMenu();
    }

    public void displayPartOfAlbumsInLibrary() {
        System.out.println("What kind of albums would you prefer to see?(Please enter 1 or 2.)");
        System.out.println("-1- Albums from certain artists.");
        System.out.println("-2- Albums in certain genre.");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displayAlbumsFromCertainArtists();
            case "2":
                this.displayAlbumsInCertainGenre();
            default:
                System.out.println("Invalid input. Please enter a number from 1~2.");
                this.displayPartOfAlbumsInLibrary();
                break;
        }
    }

    public void displayAlbumsFromCertainArtists() {
        System.out.println("Please enter the last name of an artist.");
        userInput = input.nextLine();
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-16s | %s\n", "id", "name", "AudioDB_ID",
                "artistID", "nSongs", "albumReleaseDate", "albumGenre");
        for(Album a: library.getAlbums()) {
            String artistName = a.getArtist().getName().toLowerCase(Locale.ROOT);
            if (artistName.contains(userInput.toLowerCase(Locale.ROOT))) {
                System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-16s | %s\n", a.getEntityID(), a.getName(), a.getAudioDB_ID(),
                        a.getArtist().getEntityID(), a.getSongs().size(), a.getName(), a.getAlbumGenre());
                index++;
            }
        }
        if (index == 0) {
            System.out.println("No qualified artists in the current database. You can add such artists later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        this.requestForAnotherSearch();
        this.mainMenu();
    }

    public void displayAlbumsInCertainGenre() {
        System.out.println("Please enter the last name of an artist.");
        userInput = input.nextLine();
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-16s | %s\n", "id", "name", "AudioDB_ID",
                "artistID", "nSongs", "albumReleaseDate", "albumGenre");
        for(Album a: library.getAlbums()) {
            String genre = a.getAlbumGenre().toLowerCase(Locale.ROOT);
            if (genre.contains(userInput.toLowerCase(Locale.ROOT))) {
                System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-16s | %s\n", a.getEntityID(), a.getName(), a.getAudioDB_ID(),
                        a.getArtist().getEntityID(), a.getSongs().size(), a.getReleaseDate(), a.getAlbumGenre());
                index++;
            }
        }
        if (index == 0) {
            System.out.println("No qualified artists in the current database. You can add such artists later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        this.requestForAnotherSearch();
        this.mainMenu();
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

    public void displaySongs(ArrayList<Song> songs) {
        System.out.println("--------------------------------------------------------------SONGS--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songGenre", "songReleaseDate", "songLanguage");
        for (Song s: songs) {
            System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                    s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getGenre(), s.getSongReleaseDate(), s.getSongLanguage());
        }
    }

    public void displayArtists(ArrayList<Artist> artists) {
        System.out.println("--------------------------------------------------------------ARTISTS--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-10s | %-12s | %s\n", "id", "name", "AudioDB_ID", "nAlbums",
                "nSongs", "mood", "artistStyle", "artistArea");
        for (Artist a: artists) {
            System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-10s | %-12s | %s\n", a.getEntityID(), a.getName(), a.getAudioDB_ID(),
                    a.getAlbums().size(), a.getSongs().size(), a.getMood(), a.getStyle(), a.getArtistArea());
        }
    }

    public void displayAlbums(ArrayList<Album> albums) {
        System.out.println("--------------------------------------------------------------ALBUMS--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-16s | %s\n", "id", "name", "AudioDB_ID",
                "artistID", "nSongs", "albumReleaseDate", "albumGenre");
        for (Album a: albums) {
            System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-15s | %s\n", a.getEntityID(), a.getName(), a.getAudioDB_ID(),
                    a.getArtist().getEntityID(), a.getSongs().size(), a.getReleaseDate(), a.getAlbumGenre());
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

    /* Create music objects from SQL data*/
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

    public Artist insertArtistFromAudioDB(Statement statement, String artistName) {
        String requestURL = "https://www.theaudiodb.com/api/v1/json/2/search.php?s=";
        StringBuilder response = new StringBuilder();
        int id = 201 + library.getArtists().size();
        Artist newArtist = new Artist(id, artistName);
        URL u;
        try {
            u = new URL(requestURL + artistName);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
            return null;
        }
        try {
            URLConnection connection = u.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();

            String message = httpConnection.getResponseMessage();
            System.out.println(code + " " + message);
            if (code != HttpURLConnection.HTTP_OK) {
                return null;
            }
            InputStream inStream = connection.getInputStream();
            Scanner in = new Scanner(inStream);
            while (in.hasNextLine()) {
                response.append(in.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading response");
            return null;
        }
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray artists = (JSONArray) jsonObject.get("artists");
            if (artists == null) {
                System.out.println("Ops...No matched artist in AudioDB database.");
                return null;
            } else {
                JSONObject requestedArtist = (JSONObject) artists.get(0);
                String AudioDB_ID = (String) requestedArtist.get("idArtist");
                newArtist.setAudioDB_ID(AudioDB_ID);
                String mood = (String) requestedArtist.get("strMood");
                newArtist.setMood(mood);
                String artistStyle = (String) requestedArtist.get("strStyle");
                newArtist.setStyle(artistStyle);
                String artistArea = (String) requestedArtist.get("strCountry");
                newArtist.setArtistArea(artistArea);
                library.addArtist(newArtist);
                //statement.executeUpdate(newArtist.toSQL());
                return newArtist;
            }
        } catch (ParseException e) {
            System.out.println("Error parsing JSON");
            return null;
        }
    }

    public void insertSongsFromMusicBrainz(Statement statement, String songName) throws SQLException {
        StringBuilder response = new StringBuilder();
        ArrayList<Song> duplicatedSongs = new ArrayList<>();
        this.buildConnectionWithMusicBrainz(songName, response);
        Song newSong = this.grabSongs(statement, response, songName, duplicatedSongs);
        if (newSong != null) {
            statement.executeUpdate(newSong.toSQL());
            if (newSong.getPerformer() != null) {
                statement.executeUpdate(newSong.getPerformer().toSQL());
            }
            if (newSong.getAlbum() != null) {
                statement.executeUpdate(newSong.getAlbum().toSQL());
            }
            System.out.println("The song \"" + newSong.getName() + "\" has been successfully imported into the database.");

        }
    }

    public void buildConnectionWithMusicBrainz(String songName, StringBuilder response) {
        String initialURL = "https://musicbrainz.org/ws/2/release?query=";
        String endURL = "&limit=10&fmt=json";
        String wholeURL = initialURL + songName + endURL;
        wholeURL = wholeURL.replaceAll(" ", "%12");
        URL u;
        try {
            u = new URL(wholeURL);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
            return;
        }
        try {
            URLConnection connection = u.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();

            String message = httpConnection.getResponseMessage();
            System.out.println(code + " " + message);
            if (code != HttpURLConnection.HTTP_OK) {
                return;
            }
            InputStream inStream = connection.getInputStream();
            Scanner in = new Scanner(inStream);
            while (in.hasNextLine()) {
                response.append(in.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading response");
            return;
        }
    }

    public Song grabSongs(Statement statement, StringBuilder response, String songName, ArrayList<Song> duplicatedSongs) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray releases = (JSONArray) jsonObject.get("releases");
            if (releases.size() == 1) {
                JSONObject requestedSong = (JSONObject) releases.get(0);
                String id = (String) requestedSong.get("idArtist");
                String date = (String) requestedSong.get("date");
                String ArtistStyle = (String) requestedSong.get("strStyle");
                statement.executeUpdate("insert into songs (id, name, mood, date) values ('" + id + "','" +
                        artistName + "','" + date + "','" + ArtistStyle + "')");
                System.out.println("The song has been successfully imported to the library.");
            } else {
                System.out.println("We have found various of songs containing \"" + songName +
                        "\".\nFollowings are the top 5 matched searching results.");
                for (int i = 0; i < releases.size() && i < 5; i++) {
                    Song tempSong = new Song();
                    Artist tempArtist = new Artist();
                    JSONObject currentRelease = (JSONObject) releases.get(i);
                    String songTitle = (String) currentRelease.get("title");
                    tempSong.setName(songTitle);
                    String MusicBrainz_ID = (String) currentRelease.get("id");
                    tempSong.setMusicBrainzID(MusicBrainz_ID);
                    JSONArray artists = (JSONArray) currentRelease.get("artist-credit");
                    JSONObject currentArtist = (JSONObject) artists.get(0);
                    String currentArtistName = (String) currentArtist.get("name");
                    tempArtist.setName(currentArtistName);
                    tempSong.setPerformer(tempArtist);
                    JSONObject text = (JSONObject) currentRelease.get("text-representation");
                    if (text != null) {
                        String songLanguage = (String) text.get("language");
                        tempSong.setSongLanguage(songLanguage);
                    } else {
                        tempSong.setSongLanguage("null");
                    }
                    String releaseDate = (String) currentRelease.get("date");
                    tempSong.setSongReleaseDate(releaseDate);
                    duplicatedSongs.add(tempSong);
                }
                return this.selectSong(duplicatedSongs);
            }
        } catch (ParseException e) {
            System.out.println("Error parsing JSON");
            return null;
        } catch (SQLException e) {
            System.out.println("SQLite updates failed");
            return null;
        }
        return null;
    }

    public Song selectSong(ArrayList<Song> duplicatedSongs) {
        Song tempSong = null;
        Artist tempArtist = new Artist();
        int id = 101 + library.getSongs().size();
        Song newSong = new Song(id);
        for (int i = 0; i < duplicatedSongs.size(); i++) {
            System.out.format("#%-3dSong Name:%-40sMusicBrainz_ID:%-40sArtist Name:%-25sRelease Date:%-15sLanguage:%-10s\n", i, duplicatedSongs.get(i).getName(), duplicatedSongs.get(i).getMusicBrainzID(),
                    duplicatedSongs.get(i).getPerformer().getName(), duplicatedSongs.get(i).getSongReleaseDate(), duplicatedSongs.get(i).getSongLanguage());
        }
        System.out.println("Is there any song among those 5 songs that you might want to import into the library? (Enter Y/N)");
        userInput = input.nextLine();
        while (true) {
            if (userInput.toLowerCase(Locale.ROOT).equals("y")) {
                System.out.println("Great! Now please choose the song which is closest to the song that you are looking for." +
                        "\n(Input the exact number after # to import the chosen one:)");
                userInput = input.nextLine();
                while (!((userInput.equals("0")) || (userInput.equals("1")) || (userInput.equals("2")) || (userInput.equals("3")) || (userInput.equals("4")))) {
                    System.out.println("Invalid input. Please enter an integer from 0~4.");
                    userInput = input.nextLine();
                }
                switch (userInput) {
                    case "0":
                        tempSong = duplicatedSongs.get(0);
                        break;
                    case "1":
                        tempSong = duplicatedSongs.get(1);
                        break;
                    case "2":
                        tempSong = duplicatedSongs.get(2);
                        break;
                    case "3":
                        tempSong = duplicatedSongs.get(3);
                        break;
                    case "4":
                        tempSong = duplicatedSongs.get(4);
                        break;
                    default:
                        System.out.println("Bad input.");
                        break;
                }
                newSong.setName(tempSong.getName());
                newSong.setMusicBrainzID(tempSong.getMusicBrainzID());
                newSong.setSongReleaseDate(tempSong.getSongReleaseDate());
                newSong.setSongLanguage(tempSong.getSongLanguage());
                tempArtist.setName(tempSong.getPerformer().getName());
                newSong.setPerformer(tempArtist);
                boolean artistFound = newSong.setArtistForSong(library, this);
                if (artistFound) {         // if there is an artists added to the song, we can further add album for the song, otherwise we stop finding album for the song
                    boolean albumFound = newSong.setAlbumForSong(library, this);
                }
                if (library.findDuplicates(newSong) != null) {
                    library.addSong(newSong);
                    return newSong;
                }
                return null;
            } else if (userInput.toLowerCase(Locale.ROOT).equals("n")) {
                System.out.println("Gotcha!");
                return null;
            } else {
                System.out.println("Invalid input. Please enter again.");
                userInput = input.nextLine();
            }
        }
    }

    public void requestForAnotherInsertOfSongsWithName(Statement statement) throws SQLException {
        System.out.println("Do you prefer to do another round of song import with song name? (Please enter Y/N)");
        userInput = input.nextLine();
        while (true) {
            if (userInput.toLowerCase(Locale.ROOT).equals("y")) {
                System.out.println("Please enter the name of a song here, and then we will automatically fill in details of that song for you: ");
                userInput = input.nextLine();
                this.insertSongsFromMusicBrainz(statement, userInput);
            } else if (userInput.toLowerCase(Locale.ROOT).equals("n")) {
                System.out.println("Great! Let's jump to the main menu.");
                break;
            } else {
                System.out.println("Invalid input. Please enter again.");
                userInput = input.nextLine();
            }
        }
    }

    public Album insertAlbumFromAudioDB(Statement statement, Artist artist, String releaseDate) {
        String requestURL = "https://theaudiodb.com/api/v1/json/2/album.php?i=";
        StringBuilder response = new StringBuilder();
        int id = 301 + library.getAlbums().size();
        Album newAlbum = new Album(id);
        newAlbum.setArtist(artist);
        ArrayList<Album> duplicatedAlbums = new ArrayList<>();
        URL u;
        try {
            u = new URL(requestURL + artist.getAudioDB_ID());
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
            return null;
        }
        try {
            URLConnection connection = u.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();

            String message = httpConnection.getResponseMessage();
            System.out.println(code + " " + message);
            if (code != HttpURLConnection.HTTP_OK) {
                return null;
            }
            InputStream inStream = connection.getInputStream();
            Scanner in = new Scanner(inStream);
            while (in.hasNextLine()) {
                response.append(in.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading response");
            return null;
        }
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray albums = (JSONArray) jsonObject.get("album");
            if (albums == null) {
                System.out.println("Ops...This artist doesn't have any album recorded in AudioDB database.");
                return null;
            } else {
                int numberOfAlbums = albums.size();
                for (int i = 0; i < numberOfAlbums; i++) {
                    JSONObject requestedArtist = (JSONObject) albums.get(i);
                    String releaseYear = (String) requestedArtist.get("intYearReleased");
                    if (releaseYear.equals(releaseDate.substring(0,4))) {             // if the release year matches, then this album is possibly what we are looking for
                        Album tempAlbum = new Album();
                        tempAlbum.setReleaseDate(releaseYear);
                        String AudioDB_ID = (String) requestedArtist.get("idAlbum");
                        tempAlbum.setAudioDB_ID(AudioDB_ID);
                        String albumName = (String) requestedArtist.get("strAlbum");
                        tempAlbum.setName(albumName);
                        String albumGenre = (String) requestedArtist.get("strGenre");
                        tempAlbum.setAlbumGenre(albumGenre);
                        duplicatedAlbums.add(tempAlbum);
                        if (duplicatedAlbums.size() == 5) {              // show maximum 5 albums to user
                            break;
                        }
                    }
                }
                if (duplicatedAlbums.size() > 1) {
                    Album tempAlbum = this.selectAlbum(duplicatedAlbums);
                    newAlbum.setAudioDB_ID(tempAlbum.getAudioDB_ID());
                    newAlbum.setReleaseDate(tempAlbum.getReleaseDate());
                    newAlbum.setAlbumGenre(tempAlbum.getAlbumGenre());
                    library.addAlbum(newAlbum);
                    //statement.executeUpdate(newAlbum.toSQL());
                } else {
                    newAlbum.setName(duplicatedAlbums.get(0).getName());
                    newAlbum.setAudioDB_ID(duplicatedAlbums.get(0).getAudioDB_ID());
                    newAlbum.setReleaseDate(duplicatedAlbums.get(0).getReleaseDate());
                    newAlbum.setAlbumGenre(duplicatedAlbums.get(0).getAlbumGenre());
                    library.addAlbum(newAlbum);
                    //statement.executeUpdate(newAlbum.toSQL());
                }
                return newAlbum;
            }
        } catch (ParseException e) {
            System.out.println("Error parsing JSON");
            return null;
        } /*catch (SQLException e) {
            System.out.println("SQLite updates failed");
            return null;
        } */
    }

    public Album selectAlbum(ArrayList<Album> duplicatedAlbums) {
        Album tempAlbum= null;
        for (int i = 0; i < duplicatedAlbums.size(); i++) {
            System.out.format("#%-3dSong Name:%-40sMusicBrainz_ID:%-40sArtist Name:%-25sRelease Date:%-15sGenre:%-10s\n", i, duplicatedAlbums.get(i).getName(), duplicatedAlbums.get(i).getAudioDB_ID(),
                    duplicatedAlbums.get(i).getArtist().getName(), duplicatedAlbums.get(i).getReleaseDate(), duplicatedAlbums.get(i).getAlbumGenre());
        }
        System.out.println("Is there any album among those albums that you might want to import into the library? (Enter Y/N)");
        userInput = input.nextLine();
        while (true) {
            if (userInput.toLowerCase(Locale.ROOT).equals("y")) {
                System.out.println("Great! Now please choose the album which is closest to the album that you are looking for." +
                        "\n(Input the exact number after # to import the chosen one:)");
                userInput = input.nextLine();
                while (!((userInput.equals("0")) || (userInput.equals("1")) || (userInput.equals("2")) || (userInput.equals("3")) || (userInput.equals("4")))) {
                    System.out.println("Invalid input. Please enter an integer from 0~4.");
                    userInput = input.nextLine();
                }
                switch (userInput) {
                    case "0":
                        tempAlbum = duplicatedAlbums.get(0);
                        break;
                    case "1":
                        tempAlbum = duplicatedAlbums.get(1);
                        break;
                    case "2":
                        tempAlbum = duplicatedAlbums.get(2);
                        break;
                    case "3":
                        tempAlbum = duplicatedAlbums.get(3);
                        break;
                    case "4":
                        tempAlbum = duplicatedAlbums.get(4);
                        break;
                    default:
                        System.out.println("Bad input.");
                        break;
                }
                return tempAlbum;
            } else if (userInput.toLowerCase(Locale.ROOT).equals("n")) {
                System.out.println("Gotcha!");
                break;
            } else {
                System.out.println("Invalid input. Please enter again.");
                userInput = input.nextLine();
            }
        }
        return null;
    }

}