package edu.usfca.cs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Album extends Entity {
    protected ArrayList<Song> songs;
    protected Artist artist;
    protected String AudioDB_ID;
    protected String releaseDate;
    protected String albumGenre;

    public Album(String name) {
        super(name);
        songs = new ArrayList<>();
    }

    public Album() { songs = new ArrayList<>(); }

    public Album(int ID) {
        super(ID);
        songs = new ArrayList<>();
        artist = new Artist();
    }

    public Album(int ID, String name) {
        super(ID, name);
        songs = new ArrayList<>();
        artist = new Artist();
    }

    public String getName() { return name; }

    public boolean equals(Album otherAlbum) {
        if ((this.artist.equals(otherAlbum.getArtist())) &&
                (this.name.equals(otherAlbum.getName()))) {
            return true;
        } else {
            return false;
        }
    }

    public String getAlbumGenre() {
        return albumGenre;
    }

    public void setAlbumGenre(String albumGenre) {
        this.albumGenre = albumGenre;
    }

    public String getAudioDB_ID() { return AudioDB_ID; }

    public void setAudioDB_ID(String audioDB_ID) { AudioDB_ID = audioDB_ID;}

    public String getReleaseDate() { return releaseDate; }

    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public void addSong(Song s) {
        songs.add(s);
    }

    protected ArrayList<Song> getSongs() {
        return songs;
    }

    protected void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String toXML() { return "<album id=\"" + this.getEntityID() + "\"> <title>" + this.getName() + "</title>" + " </album>"; }

    public String toSQL() {
        int nSongs = 0;
        if (this.getSongs() != null) {
            nSongs = this.getSongs().size();
        }
        return "insert into albums (id, name, AudioDB_ID, artist, nSongs, albumReleaseDate, albumGenre) values " +
                "('" + this.getEntityID() + "','" + this.getName() + "','" + this.getAudioDB_ID() + "','" + this.getArtist().getEntityID() + "','" +
                nSongs + "','" + this.getReleaseDate() + "','" + this.getAlbumGenre() + "')";
    }

    public void fromSQL(ResultSet rs, ArrayList<Artist> artists) throws SQLException {
        this.setEntityID(rs.getInt("id"));
        this.setName(rs.getString("name"));
        this.setAudioDB_ID(rs.getString("AudioDB_ID"));
        this.setReleaseDate(rs.getString("albumReleaseDate"));
        this.setAlbumGenre(rs.getString("albumGenre"));
        for (Artist artist: artists) {
            if (rs.getInt("artist") == artist.getEntityID()) {
                this.setArtist(artist);
                break;
            }
        }
    }

    public void setArtistForAlbum(Library library, String artistAudioDB_ID) {
        for (Artist artist: library.getArtists()) {
            if (artist.getAudioDB_ID().equals(artistAudioDB_ID)) {
                this.setArtist(artist);
            }
        }
    }
}
