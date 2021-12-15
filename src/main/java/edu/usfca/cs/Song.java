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
    protected UserPrompt userPrompt = new UserPrompt();
    AudioDB audioDB = new AudioDB();
    MusicBrainz musicBrainz = new MusicBrainz();

    /**
     * This is a constructor.
     */
    public Song() { }

    /**
     *
     * @param ID ID of song
     *
     * This is a constructor.
     *
     */
    public Song(int ID) { super(ID); }

    /**
     *
     * @param ID ID of song
     * @param name name of song
     *
     * This is a constructor.
     *
     */
    public Song(int ID, String name) {
        super(ID, name);
    }

    /**
     *
     * @param name name of song
     *
     * This is a constructor.
     *
     */
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

    /**
     *
     * @return
     *
     * This returns the release date of a song.
     *
     */
    public String getSongReleaseDate() {
        return songReleaseDate;
    }

    /**
     *
     * @param songReleaseDate release date of a song
     *
     * This sets the release date for a song.
     *
     */
    public void setSongReleaseDate(String songReleaseDate) {
        this.songReleaseDate = songReleaseDate;
    }

    /**
     *
     * @return
     *
     * This returns the language of a song.
     *
     */
    public String getSongLanguage() {
        return songLanguage;
    }

    /**
     *
     * @param songLanguage language of song
     *
     * This sets the language for a song.
     *
     */
    public void setSongLanguage(String songLanguage) {
        this.songLanguage = songLanguage;
    }

    /**
     *
     * @return
     *
     * This returns the genre of a song.
     *
     */
    public String getGenre() { return genre; }

    /**
     *
     * @param genre
     *
     * This sets the genre for a song.
     *
     */
    public void setGenre(String genre) { this.genre = genre; }

    /**
     *
     * @return
     *
     * This returns the musicBrainz_ID of a song.
     *
     */
    public String getMusicBrainzID() { return musicBrainz_ID; }

    /**
     *
     * @param musicBrainzID
     *
     * This sets the musicBrainz_ID for a song.
     *
     */
    public void setMusicBrainzID(String musicBrainzID) { this.musicBrainz_ID = musicBrainzID; }

    /**
     *
     * @param length duration of song
     *
     * This sets the duration for a song.
     *
     */
    public void setLength(int length) { duration = new SongInterval(length); }

    /**
     *
     * @return
     *
     * This returns the duration of a song.
     *
     */
    public String showLength() { return duration.toString(); }

    /**
     *
     * @return
     *
     * This returns the album that stores the song.
     *
     */
    protected Album getAlbum() { return album; }

    /**
     *
     * @param album
     *
     * This sets the album that stores the song.
     *
     */
    protected void setAlbum(Album album) { this.album = album; }

    /**
     *
     * @return
     *
     * This returns the artist who owns the song.
     *
     */
    public Artist getPerformer() { return performer; }

    /**
     *
     * @param performer
     *
     * This sets the artist who owns the song.
     *
     */
    public void setPerformer(Artist performer) { this.performer = performer; }

    /**
     *
     * @return String
     *
     * This returns some basic information of a song along with its artist and album.
     *
     */
    public String toString() {
        return super.toString() + " " + this.performer + " " + this.album + " " + this.duration;
    }

    /**
     *
     * @param song
     * @return boolean
     *
     * This compares two songs, and returns true if two songs are definitely equal, otherwise returns false.
     *
     */
    public boolean definitelyEquals(Song song) {
        if (this.name.equals(song.name) && this.performer.equals(song.getPerformer()) && this.album.equals(song.getAlbum())) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param song
     * @return boolean
     *
     * This compares two songs, and returns true if two songs are possibly equal, otherwise returns false.
     *
     */
    public boolean possiblyEquals(Song song) {
        if (this.possiblyEqualsCondition1(song) || this.possiblyEqualsCondition2(song)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param song
     * @return
     *
     * This is one of the two conditions that two songs are possibly equal.
     * Two songs have the same name and either artist or album is the same.
     * It returns true if two songs meet this condition, otherwise returns false.
     *
     */
    public boolean possiblyEqualsCondition1(Song song) {
        if ((this.name.equals(song.name))
                && (this.performer.equals(song.getPerformer()) || this.album.equals(song.getAlbum()))) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param song
     * @return
     *
     * This is one of the two conditions that two songs are possibly equal.
     * Two songs have the same artist and album, and the names are the same if converted to lower case and punctuation is ignored.
     * It returns true if two songs meet this condition, otherwise returns false.
     *
     */
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

    /**
     *
     * @param song
     * @return
     *
     * This converts the name of a song to lower case and eliminate punctuations.
     *
     */
    public String formatName(Song song) {
        return song.name.toLowerCase(Locale.ROOT).replaceAll("[\\W]", "");
    }

    /**
     *
     * @return
     *
     * This returns the likes of a song.
     *
     */
    public int getLikes() { return likes; }

    /**
     *
     * @param likes likes of song
     *
     * This sets the likes for a song.
     *
     */
    public void setLikes(int likes) { this.likes = likes; }

    /**
     *
     * @param song
     * @return
     *
     * This compares two songs based on their likes.
     *
     */
    @Override
    public int compareTo(Song song) { return Integer.compare(this.getLikes(), song.getLikes()); }

    /**
     *
     * @return
     *
     * This returns the JSON format of a song
     *
     */
    public String toJSON() {
        return "{" + "\"id\": \"" + this.getEntityID() + "\"," + "\"title\": \"" + this.getName() + "\"," +
                "\"artist\": " + this.getPerformer().toJSON() + "," + "\"album\": " + this.getAlbum().toJSON() + "}";
    }

    /**
     *
     * @return
     *
     * This returns the XML format of a song
     *
     */
    public String toXML() {
        return "<song id=\"" + this.getEntityID() + "\"> <title>" + this.getName() + "</title> " +
                this.getPerformer().toXML() + " " + this.getAlbum().toXML() + " </song>";
    }

    /**
     *
     * @return
     *
     * This returns the SQLite insertion command for a song
     *
     */
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

    /**
     *
     * @param rs data selected from SQLite database
     * @param artists arrayList of artists in the library
     * @param albums  arrayList of albums in the library
     * @throws SQLException
     *
     * This links artist and album with a song.
     *
     */
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

    /**
     *
     * @param library the music library
     * @return
     *
     * This finds the artist for a piece of song and set relation on the artist and song
     * if there is such artist in the library or AudioD when user want to import a piece of song.
     * It returns true if the artist is linked to the song, return false otherwise.
     *
     */
    public boolean setArtistForSong(Library library) {
        int flag = 0;
        Connection connection = null;
        Artist newArtist;
        for (Artist a: library.getArtists()) {
            if (this.getPerformer().getName().equalsIgnoreCase(a.getName())) {
                System.out.println("We have found an artist in the library that might be the artist of the song.");
                System.out.println("Artist INFO");
                System.out.format("Artist Name:%-40sAudioDB_ID:%-18sArtist Style:%-10sArtist Area:%-10s\n", a.getName(), a.getAudioDB_ID(),
                        a.getStyle(), a.getArtistArea());
                if (userPrompt.keepArtistInLibraryForSongOrNot()) {         // if there is the artist of the song in the library, we prompt user if this artist is the correct one
                    System.out.println("Great! We will link this artist with the song.");
                    this.setPerformer(a);       // if they confirm, we directly link the song and artist together
                    a.addSong(this);
                    flag = 1;
                    return false;
                }
            }
        }
        if (flag == 0) {                        // if the artist is not in the library or users want to see other albums, we search the artist in the AudioDB
            System.out.println("Ops...The artist of this song is currently not in library.");
            System.out.println("But don't panic:) We could try to find that artist for you right now!");
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:music.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
                newArtist = audioDB.insertArtistFromAudioDB(statement, this.getPerformer().getName(), library);
                if (newArtist != null) {
                    if (userPrompt.keepArtistInAudioDBForSongOrNot()) {
                        System.out.println("Great! We will link this artist with the song.");
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

    /**
     *
     * @param library the music library
     * @return
     *
     * This finds album for a piece of song and set relation on the album and song if there is such album in the library or AudioDB when user want to import a piece of song.
     * Since the album searching process from AudioDB is based on the artist, when there is no such artist in AudioDB the album searching process will not be conducted.
     * It returns true if the album is linked to the song, otherwise returns false.
     *
     */
    public boolean setAlbumForSong(Library library) {
        int flag = 0;
        Connection connection = null;
        Album newAlbum;
        for (Album a: library.getAlbums()) {
            if (this.getPerformer().getAudioDB_ID().equalsIgnoreCase(a.getArtist().getAudioDB_ID())) {
                if (this.getSongReleaseDate().substring(0,4).equalsIgnoreCase(a.getReleaseDate().substring(0,4))) {
                    System.out.println("We have found an album in the library that might be the artist of the song.");
                    System.out.println("Album INFO");
                    System.out.format("Album Name:%-40sAudioDB_ID:%-18sAlbum Genre:%-10sAlbum ReleaseDate:%-10s\n", a.getName(), a.getAudioDB_ID(),
                            a.getAlbumGenre(), a.getReleaseDate());
                    System.out.println("Is this album the one that stores the song?");
                    if(userPrompt.promptUserForYesOrNO().equalsIgnoreCase("y")) {
                        this.setAlbum(a);                    // if there is the album of the song in the library, we ask users if this album is the right one
                        this.getPerformer().addAlbum(a);     // if they answer yes then we directly link the song, artist, album together
                        a.setArtist(this.getPerformer());
                        a.addSong(this);
                        flag = 1;
                        return true;
                    }
                }
            }
        }
        if (flag == 0) {                // if the album is not in library or users want to see other albums, we will find it in the AudioDB
            System.out.println("Ops...The album of this song is currently not in library.");
            System.out.println("But don't panic:) We could try to find that album for you right now!");
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:music.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
                newAlbum = audioDB.insertAlbumFromAudioDB(statement, this.getPerformer(), this.getSongReleaseDate(), library);
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