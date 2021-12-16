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

    /**
     *
     * @param name album name
     *
     * This is a constructor.
     *
     */
    public Album(String name) {
        super(name);
        songs = new ArrayList<>();
    }

    /**
     * This is a constructor.
     */
    public Album() { songs = new ArrayList<>(); }

    /**
     *
     * @param ID album ID
     *
     * This is a constructor.
     *
     */
    public Album(int ID) {
        super(ID);
        songs = new ArrayList<>();
        artist = new Artist();
    }

    /**
     *
     * @param ID album ID
     * @param name album name
     *
     * This is a constructor.
     *
     */
    public Album(int ID, String name) {
        super(ID, name);
        songs = new ArrayList<>();
        artist = new Artist();
    }

    /**
     *
     * @return album name
     *
     * This returns the album name.
     *
     */
    public String getName() { return name; }

    /**
     *
     * @param otherAlbum album
     * @return boolean
     *
     * This returns true if the two albums are equal, otherwise returns false.
     *
     */
    public boolean equals(Album otherAlbum) {
        if ((this.artist.equals(otherAlbum.getArtist())) &&
                (this.name.equals(otherAlbum.getName()))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     *
     * This returns the album genre.
     *
     */
    public String getAlbumGenre() {
        return albumGenre;
    }

    /**
     *
     * @param albumGenre album genre
     *
     * This sets certain genre for an album.
     *
     */
    public void setAlbumGenre(String albumGenre) {
        this.albumGenre = albumGenre;
    }

    /**
     *
     * @return album AudioDB_ID
     *
     * This returns the AudioDB_ID of an album.
     *
     */
    public String getAudioDB_ID() { return AudioDB_ID; }

    /**
     *
     * @param audioDB_ID album AudioDB_ID
     *
     * This sets the AudioDB_ID for an albnum.
     *
     */
    public void setAudioDB_ID(String audioDB_ID) { AudioDB_ID = audioDB_ID;}

    /**
     *
     * @return album release date
     *
     * This returns the release date of an album.
     *
     */
    public String getReleaseDate() { return releaseDate; }

    /**
     *
     * @param releaseDate album release date
     *
     * This sets the release date for an album.
     *
     */
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public void addSong(Song s) {
        songs.add(s);
    }

    /**
     *
     * @return
     *
     * This returns an arrayList of songs stored on the album.
     *
     */
    protected ArrayList<Song> getSongs() {
        return songs;
    }

    /**
     *
     * @param songs arrayList of songs
     *
     * This sets an arrayList of songs for an album.
     *
     */
    protected void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    /**
     *
     * @return
     *
     * This returns the artist of an album.
     *
     */
    public Artist getArtist() {
        return artist;
    }

    /**
     *
     * @param artist artist
     *
     * This sets the artist for an album.
     *
     */
    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    /**
     *
     * @return string containing album details in XML format
     *
     * This returns some details of album in string and XML format.
     *
     */
    public String toXML() { return "<album id=\"" + this.getEntityID() + "\"> <title>" + this.getName() + "</title>" + " </album>"; }

    /**
     *
     * @return string containing SQLite insert command for album
     *
     * This returns the SQLite insertion command for an album.
     *
     */
    public String toSQL() {
        int nSongs = 0;
        if (this.getSongs() != null) {
            nSongs = this.getSongs().size();
        }
        return "insert into albums (id, name, AudioDB_ID, artist, nSongs, albumReleaseDate, albumGenre) values " +
                "('" + this.getEntityID() + "','" + this.getName() + "','" + this.getAudioDB_ID() + "','" + this.getArtist().getEntityID() + "','" +
                nSongs + "','" + this.getReleaseDate() + "','" + this.getAlbumGenre() + "')";
    }

    /**
     *
     * @param rs select result from SQLite
     * @param artists arrayList of albums
     * @throws SQLException
     *
     * This generates album object from SQLite select result.
     *
     */
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

    /**
     *
     * @param library music library
     * @param artistAudioDB_ID artist AudioDB_ID
     *
     * This sets artist for an album based on the artist AudioDB_ID.
     *
     */
    public void setArtistForAlbum(Library library, String artistAudioDB_ID) {
        for (Artist artist: library.getArtists()) {
            if (artist.getAudioDB_ID().equals(artistAudioDB_ID)) {
                this.setArtist(artist);
            }
        }
    }
}
