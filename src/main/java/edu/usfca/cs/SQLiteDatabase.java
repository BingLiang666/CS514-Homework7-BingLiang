package edu.usfca.cs;/* from the sqlite-jdbc github repo */

import java.sql.*;
import java.util.ArrayList;

public class SQLiteDatabase {

    public static void main(String[] args) {
        SQLite sqLite = new SQLite();
        ArrayList<Song> songs = new ArrayList<>();
        ArrayList<Artist> artists = new ArrayList<>();
        ArrayList<Album> albums = new ArrayList<>();

        ArrayList<Album> albumsCreatedFromSQL = new ArrayList<>();
        ArrayList<Artist> artistsCreatedFromSQL = new ArrayList<>();
        ArrayList<Song> songsCreatedFromSQL = new ArrayList<>();

        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:music.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            System.out.println("-----------------------------");
            System.out.println("------SQL_TABLES_SCHEMA------");
            System.out.println("songs: id|name|artist|album");
            System.out.println("artists: id|name|nAlbums|nSongs");
            System.out.println("albums: id|name|artist|nSongs");
            System.out.println("-----------------------------");

            sqLite.createSQLTables(statement);

            // create music objects from SQL
            sqLite.createMusicObjectsFromSQL(statement, songsCreatedFromSQL, artistsCreatedFromSQL, albumsCreatedFromSQL);
            System.out.println("-----------INFO(3)------------");
            System.out.println("---OBJECTS CREATED FROM SQL---");
            sqLite.displaySongs(songsCreatedFromSQL);
            sqLite.displayArtists(artistsCreatedFromSQL);
            sqLite.displayAlbums(albumsCreatedFromSQL);


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

    public void createSQLTables(Statement statement) throws SQLException {
        statement.executeUpdate("drop table if exists songs");
        statement.executeUpdate("create table songs (id string NOT NULL PRIMARY KEY, name string NOT NULL, artist integer, album integer, songGenre string)");
        statement.executeUpdate("drop table if exists artists");
        statement.executeUpdate("create table artists (id string NOT NULL PRIMARY KEY, name string NOT NULL, nAlbums integer, nSongs integer, mood string, style string)");
        statement.executeUpdate("drop table if exists albums");
        statement.executeUpdate("create table albums (id string NOT NULL PRIMARY KEY, name string NOT NULL, artist integer, nSongs integer, releaseDate string)");
    }

    public void insertIntoTableSongs(Statement statement, ArrayList<Song> songs) throws SQLException {
        for (Song s: songs) {
            statement.executeUpdate(s.toSQL());
        }
    }

    public void insertIntoTablesArtists(Statement statement, ArrayList<Artist> artists) throws SQLException {
        for(Artist a: artists) {
            statement.executeUpdate(a.toSQL());
        }
    }

    public void insertIntoTablesAlbums(Statement statement, ArrayList<Album> albums) throws SQLException {
        for(Album a: albums) {
            statement.executeUpdate(a.toSQL());
        }
    }

    public void displaySongs(ArrayList<Song> songs) {
        System.out.println(" * * * SONGS * * *");
        for (Song song: songs) {
            System.out.println(song.getEntityID() + " | " + song.getName() + " | " + song.getPerformer().getEntityID()
                    + " | " + song.getAlbum().getEntityID());
        }
    }

    public void displayArtists(ArrayList<Artist> artists) {
        System.out.println(" * * * ARTISTS * * *");
        for (Artist artist: artists) {
            System.out.println(artist.getEntityID() + " | " + artist.getName() + " | " + artist.getAlbums().size()
                    + " | " + artist.getSongs().size());
        }
    }

    public void displayAlbums(ArrayList<Album> albums) {
        System.out.println(" * * * ALBUMS * * *");
        for (Album album: albums) {
            System.out.println(album.getEntityID() + " | " + album.getName() + " | " + album.getArtist().getEntityID()
                    + " | " + album.getSongs().size());
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
        for (Artist artist: artistsCreatedFromSQL) {
            ResultSet rsSongArtistFilter = statement.executeQuery("select * from songs where artist == " + artist.getEntityID());
            while (rsSongArtistFilter.next()) {
                for (Song s: songsCreatedFromSQL) {
                    if (s.getEntityID() == rsSongArtistFilter.getInt("id")) {
                        artist.addSong(s);
                    }
                }
                for (Album a: albumsCreatedFromSQL) {
                    if (a.getEntityID() == rsSongArtistFilter.getInt("album")) {
                        if (!artist.getAlbums().contains(a)) {
                            artist.addAlbum(a);
                        }
                    }
                }
            }
        }

        /* add certain song and artist to certain album from table songs */
        for (Album album: albumsCreatedFromSQL) {
            ResultSet rsSongAlbumFilter = statement.executeQuery("select * from songs where album == " + album.getEntityID());
            while (rsSongAlbumFilter.next()) {
                for (Song s: songsCreatedFromSQL) {
                    if (s.getEntityID() == rsSongAlbumFilter.getInt("id")) {
                        album.addSong(s);
                    }
                }
                for (Artist a: artistsCreatedFromSQL) {
                    if (a.getEntityID() == rsSongAlbumFilter.getInt("artist")) {
                        album.setArtist(a);
                        break;
                    }
                }
            }
        }
    }
}
