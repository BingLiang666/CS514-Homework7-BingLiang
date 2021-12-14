package edu.usfca.cs;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Song extends Entity implements Comparable<Song> {
    protected Album album;
    protected Artist performer;
    protected SongInterval duration;
    protected String genre;
    protected int likes;
    protected String musicBrainz_ID;
    protected String songReleaseDate;
    protected String songLanguage;
    protected UserPrompt userPrompt;

    public Song() { }

    public Song(int ID) { super(ID); }

    public Song(int ID, String name) {
        super(ID, name);
    }

    public Song(String name) {
        super(name);
        album = new Album();
        performer = new Artist();
        duration = new SongInterval();
        genre = "";
    }

    /**
     *
     * @param name name of song
     * @param length length in seconds.
     * 
     * This is a constructor.
     *
     */
    public Song(String name, int length) {
        super(name);
        duration = new SongInterval(length);
        genre = "";
    }

    public String getSongReleaseDate() {
        return songReleaseDate;
    }

    public void setSongReleaseDate(String songReleaseDate) {
        this.songReleaseDate = songReleaseDate;
    }

    public String getSongLanguage() {
        return songLanguage;
    }

    public void setSongLanguage(String songLanguage) {
        this.songLanguage = songLanguage;
    }

    public String getGenre() { return genre; }

    public void setGenre(String genre) { this.genre = genre; }

    public String getMusicBrainzID() { return musicBrainz_ID; }

    public void setMusicBrainzID(String musicBrainzID) { this.musicBrainz_ID = musicBrainzID; }

    public void setLength(int length) { duration = new SongInterval(length); }

    public String showLength() { return duration.toString(); }


    protected Album getAlbum() { return album; }

    protected void setAlbum(Album album) { this.album = album; }

    public Artist getPerformer() { return performer; }

    public void setPerformer(Artist performer) { this.performer = performer; }

    public String toString() {
        return super.toString() + " " + this.performer + " " + this.album + " " + this.duration;

    }

    public boolean definitelyEquals(Song song) {
        if (this.name.equals(song.name) && this.performer.equals(song.getPerformer()) && this.album.equals(song.getAlbum())) {
            return true;
        }
        return false;
    }

    public boolean possiblyEquals(Song song) {
        if (this.possiblyEqualsCondition1(song) || this.possiblyEqualsCondition2(song)) {
            return true;
        }
        return false;
    }

    /*Two songs have the same name and either artist or album is the same.*/
    public boolean possiblyEqualsCondition1(Song song) {
        if ((this.name.equals(song.name))
                && (this.performer.equals(song.getPerformer()) || this.album.equals(song.getAlbum()))) {
            return true;
        }
        return false;
    }

    /* Two songs have the same artist and album, and the names are the same if converted to lower case and punctuation is ignored.*/
    public boolean possiblyEqualsCondition2(Song song) {
        String formattedSongName1 = formatName(this);
        String formattedSongName2 = formatName(song);
        if (formattedSongName1.equals(formattedSongName2)
                && this.performer.equals(song.getPerformer())
                && this.album.equals(song.getAlbum())) {
            return true;
        }
        return false;
    }

    public String formatName(Song song) {
        return song.name.toLowerCase(Locale.ROOT).replaceAll("[\\W]", "");
    }

    public int getLikes() { return likes; }

    public void setLikes(int likes) { this.likes = likes; }

    @Override
    public int compareTo(Song o) { return Integer.compare(this.getLikes(), o.getLikes()); }


    public String toJSON() {
        return "{" + "\"id\": \"" + this.getEntityID() + "\"," + "\"title\": \"" + this.getName() + "\"," +
                "\"artist\": " + this.getPerformer().toJSON() + "," + "\"album\": " + this.getAlbum().toJSON() + "}";
    }

    public String toXML() {
        return "<song id=\"" + this.getEntityID() + "\"> <title>" + this.getName() + "</title> " +
                this.getPerformer().toXML() + " " + this.getAlbum().toXML() + " </song>";
    }

    public String toSQL() {
        String artistID = "null";
        String albumID = "null";
        if (this.getPerformer() != null) {
            artistID = Integer.toString(this.getPerformer().getEntityID());
        }
        if (this.getAlbum() != null) {
            albumID = Integer.toString(this.getAlbum().getEntityID());
        }
        return "insert into songs (id, name, MusicBrainz_ID, artist, album, songReleaseDate, songLanguage) values " +
                "('" + this.getEntityID() + "','" + this.getName() + "','" + this.getMusicBrainzID() + "','" + artistID + "','" +
                albumID + "','" + this.getSongReleaseDate() + "','" + this.getSongLanguage() + "')";
    }

    public void fromSQL(ResultSet rs, ArrayList<Artist> artists, ArrayList<Album> albums) throws SQLException {
        this.setEntityID(rs.getInt("id"));
        this.setName(rs.getString("name"));
        this.setSongLanguage(rs.getString("songLanguage"));
        this.setMusicBrainzID(rs.getString("MusicBrainz_ID"));
        this.setGenre(rs.getString("songGenre"));
        this.setSongReleaseDate(rs.getString("songReleaseDate"));
        for(Artist artist: artists) {
            if (rs.getInt("artist") == artist.getEntityID()) {
                this.setPerformer(artist);
                break;
            }
        }
        for(Album album: albums) {
            if (rs.getInt("album") == album.getEntityID()) {
                this.setAlbum(album);
                break;
            }
        }
    }

    // find artist for a piece of song and set relation on the artist and song if there is such artist in the library or AudioDB when user want to import a piece of song
    public boolean setArtistForSong(Library library, SQLite sqLite) {
        int flag = 0;
        Connection connection = null;
        Artist newArtist;
        for (Artist a: library.getArtists()) {
            if (this.getPerformer().getName().equalsIgnoreCase(a.getName())) {
                System.out.println("We have found an artist in the library that might be the artist of the song.");
                System.out.println("Artist INFO");
                System.out.format("Artist Name:%-40sAudioDB_ID:%-40sArtist Style%-10sArtist Area%-10s\n", a.getName(), a.getAudioDB_ID(),
                        a.getStyle(), a.getArtistArea());
                if (userPrompt.keepArtistInLibraryForSongOrNot()) {         // if there is the artist of the song in the library, we prompt user if this artist is the correct one
                    this.setPerformer(a);       // if they confirm, we directly link the song and artist together
                    a.addSong(this);
                    flag = 1;
                    return true;
                }                               // if not, we search the artist in the AudioDB
            }
        }
        if (flag == 0) {
            System.out.println("Ops...The artist of this song is currently not in library.");
            System.out.println("But don't panic:) We could try to find that artist for you right now!");
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:music.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
                newArtist = sqLite.insertArtistFromAudioDB(statement, this.getPerformer().getName());
                if (newArtist != null) {
                    if (userPrompt.keepArtistInAudioDBForSongOrNot()) {
                        this.setPerformer(newArtist);       // if we have founded the artist of the song and successfully imported the artist to the library, we link the song and newly imported artist together
                        newArtist.addSong(this);
                        return true;                        // if the artist has been successfully linked with the song, return true. (this will help us decide whether we could search album in AudioDB later)
                    } else {
                        System.out.println("Gotcha! We will not import artist for the song. The artist and album of the song will both be set to null.");
                    }
                }
                return false;                           // if the artist has not been successfully linked with the song, return false
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
        return false;
    }

    // find album for a piece of song and set relation on the album and song if there is such album in the library or AudioDB when user want to import a piece of song
    // since the album searching process from AudioDB is based on the artist, when there is no such artist in AudioDB the album searching process will not be conducted
    public boolean setAlbumForSong(Library library, SQLite sqLite) {
        int flag = 0;
        Connection connection = null;
        Album newAlbum;
        for (Album a: library.getAlbums()) {
            if (this.getPerformer().getAudioDB_ID().equalsIgnoreCase(a.getArtist().getAudioDB_ID())) {
                if (this.getSongReleaseDate().substring(0,4).equalsIgnoreCase(a.getReleaseDate().substring(0,4))) {
                    this.setAlbum(a);                    // if there is the album of the song in the library, we directly link the song, artist, album together
                    this.getPerformer().addAlbum(a);
                    a.setArtist(this.getPerformer());
                    a.addSong(this);
                    flag = 1;
                    return true;
                }
            }
        }
        if (flag == 0) {                // if the album is not in library, we will find it in the AudioDB
            System.out.println("Ops...The album of this song is currently not in library.");
            System.out.println("But don't panic:) We could try to find that album for you right now!");
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:music.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
                newAlbum = sqLite.insertAlbumFromAudioDB(statement, this.getPerformer(), this.getSongReleaseDate());
                if (newAlbum != null) {
                    this.setAlbum(newAlbum);          // if we have found the album of the song and successfully imported the album to the library, we link the song, artist and newly imported album together
                    this.getPerformer().addAlbum(newAlbum);
                    newAlbum.setArtist(this.getPerformer());
                    newAlbum.addSong(this);
                    return true;
                }
                return false;
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
        return false;
    }


}