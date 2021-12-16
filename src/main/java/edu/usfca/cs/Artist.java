package edu.usfca.cs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Artist extends Entity {
    protected ArrayList<Song> songs;
    protected ArrayList<Album> albums;
    protected String AudioDB_ID;
    protected String mood;
    protected String style;
    protected String area;

    /**
     *
     * @param name artist name
     *
     * This is a constructor.
     *
     */
    public Artist(String name) {
        super(name);
        songs = new ArrayList<>();
        albums = new ArrayList<>();
    }

    /**
     * This is a constructor.
     */
    public Artist() {
        songs = new ArrayList<>();
        albums = new ArrayList<>();
    }

    /**
     *
     * @param ID artist ID
     *
     * This is a constructor.
     *
     */
    public Artist(int ID) {
        super(ID);
        songs = new ArrayList<>();
        albums = new ArrayList<>();
    }

    /**
     *
     * @param ID artist ID
     * @param name artist name
     *
     * This is a constructor.
     *
     */
    public Artist(int ID, String name) {
        super(ID, name);
        songs = new ArrayList<>();
        albums = new ArrayList<>();
    }

    /**
     *
     * @return artist area
     *
     * This returns the artist area.
     *
     */
    public String getArtistArea() {
        return area;
    }

    /**
     *
     * @param artistArea artist area
     *
     * This sets the area for an artist.
     *
     */
    public void setArtistArea(String artistArea) {
        this.area = artistArea;
    }

    /**
     *
     * @return artist AudioDB_ID
     *
     * This returns the artist AudioDB_ID.
     *
     */
    public String getAudioDB_ID() { return AudioDB_ID; }

    /**
     *
     * @param audioDB_ID artist AudioDB_ID
     *
     * This sets the AudioDB_ID for an artist.
     *
     */
    public void setAudioDB_ID(String audioDB_ID) { AudioDB_ID = audioDB_ID; }

    /**
     *
     * @return artist mood
     *
     * This returns the artist mood.
     *
     */
    public String getMood() { return mood; }

    /**
     *
     * @param mood
     *
     * This sets the mood for an artist.
     *
     */
    public void setMood(String mood) { this.mood = mood; }

    /**
     *
     * @return
     *
     * This returns the artist style.
     *
     */
    public String getStyle() { return style; }

    /**
     *
     * @param style artist style
     *
     * This sets the style for an artist.
     *
     */
    public void setStyle(String style) { this.style = style; }

    /**
     *
     * @return arrayList of songs
     *
     * This returns an arrayList of songs owned by an artist.
     *
     */
    protected ArrayList<Song> getSongs() { return songs; }

    /**
     *
     * @param songs arrayList of songs
     *
     * This sets an arrayList of songs for an artist.
     *
     */
    protected void setSongs(ArrayList<Song> songs) { this.songs = songs; }

    /**
     *
     * @return arrayList of albums
     *
     * This returns an arrayList of albums owned by an artist.
     *
     */
    protected ArrayList<Album> getAlbums() { return albums; }

    /**
     *
     * @param albums
     *
     * This sets an arrayList of albums for an artist.
     *
     */
    protected void setAlbums(ArrayList<Album> albums) { this.albums = albums; }

    /**
     *
     * @param album album
     *
     * This adds an album for an artist.
     *
     */
    protected void addAlbum(Album album) { albums.add(album); }

    /**
     *
     * @param s song
     *
     * This adds a song for an artist.
     *
     */
    public void addSong(Song s) { songs.add(s); }

    /**
     *
     * @param a artist
     * @return
     *
     * This returns true if two artists are equal, otherwise returns false.
     *
     */
    public boolean equals(Artist a) { return super.equals(a); }

    /**
     *
     * @return string containing artist details in XML format
     *
     * This returns some details of artist in string and XML format.
     *
     */
    public String toXML() { return "<artist id=\"" + this.getEntityID() + "\"> <name>" + this.getName() + "</name>" + " </artist>"; }

    /**
     *
     * @return string containing SQLite insert command for artist
     *
     * This returns the SQLite insertion command for an artist.
     *
     */
    public String toSQL() {
        int nAlbums = 0;
        int nSongs = 0;
        if (this.getAlbums() != null) {
            nAlbums = this.getAlbums().size();
        }
        if (this.getSongs() != null) {
            nSongs = this.getSongs().size();
        }
        return "insert into artists (id, name, AudioDB_ID, nAlbums, nSongs, mood, artistStyle, artistArea) values " +
                "('" + this.getEntityID() + "','" + this.getName() + "','" + this.getAudioDB_ID() + "','" + nAlbums + "','" +
                 nSongs + "','" + this.getMood() + "','" + this.getStyle() + "','" + this.getArtistArea() + "')";
    }

    /**
     *
     * @param rs select result from SQLite
     * @throws SQLException
     *
     * This generates album object from SQLite select result.
     *
     */
    public void fromSQL(ResultSet rs) throws SQLException {
        this.setEntityID(rs.getInt("id"));
        this.setName(rs.getString("name"));
        this.setAudioDB_ID(rs.getString("AudioDB_ID"));
        this.setMood(rs.getString("mood"));
        this.setStyle(rs.getString("artistStyle"));
        this.setArtistArea(rs.getString("artistArea"));
    }
}