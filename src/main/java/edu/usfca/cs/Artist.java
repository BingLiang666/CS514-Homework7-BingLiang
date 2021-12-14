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

    public Artist(String name) {
        super(name);
        songs = new ArrayList<>();
        albums = new ArrayList<>();
    }

    public Artist() {
        songs = new ArrayList<>();
        albums = new ArrayList<>();
    }

    public Artist(int ID) {
        super(ID);
    }

    public Artist(int ID, String name) {
        super(ID, name);
    }

    public String getArtistArea() {
        return area;
    }

    public void setArtistArea(String artistArea) {
        this.area = artistArea;
    }

    public String getAudioDB_ID() { return AudioDB_ID; }

    public void setAudioDB_ID(String audioDB_ID) { AudioDB_ID = audioDB_ID; }

    public String getMood() { return mood; }

    public void setMood(String mood) { this.mood = mood; }

    public String getStyle() { return style; }

    public void setStyle(String style) { this.style = style; }

    protected ArrayList<Song> getSongs() { return songs; }

    protected void setSongs(ArrayList<Song> songs) { this.songs = songs; }

    protected ArrayList<Album> getAlbums() { return albums; }

    protected void setAlbums(ArrayList<Album> albums) { this.albums = albums; }

    protected void addAlbum(Album album) { albums.add(album); }

    public void addSong(Song s) { songs.add(s); }

    public boolean equals(Artist a) { return super.equals(a); }

    public String toXML() { return "<artist id=\"" + this.getEntityID() + "\"> <name>" + this.getName() + "</name>" + " </artist>"; }

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

    public void fromSQL(ResultSet rs) throws SQLException {
        this.setEntityID(rs.getInt("id"));
        this.setName(rs.getString("name"));
        this.setAudioDB_ID(rs.getString("AudioDB_ID"));
        this.setMood(rs.getString("mood"));
        this.setStyle(rs.getString("artistStyle"));
        this.setArtistArea(rs.getString("artistArea"));
    }

}