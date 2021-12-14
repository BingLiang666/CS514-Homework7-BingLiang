package edu.usfca.cs;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Playlist {
    private ArrayList<Song> listOfSongs;

    public Playlist(ArrayList<Song> listOfSongs) {
        this.listOfSongs = listOfSongs;
    }

    public Playlist() {
        listOfSongs = new ArrayList<>();
    }

    public boolean findSong(Song s) {
        return listOfSongs.contains(s);
    }

    public ArrayList<Song> getListOfSongs() {
        return listOfSongs;
    }

    public void addSong(Song newSong) {
        listOfSongs.add(newSong);
    }

    public void deleteSong(Song song) {
        listOfSongs.remove(song);
    }

    public Playlist merge(Playlist otherPlaylist) {
        /* merge two playlists and remove identical song objects */
        ArrayList<Song> playlistCopy = new ArrayList<>(this.listOfSongs);
        ArrayList<Song> otherPlaylistCopy = new ArrayList<>(otherPlaylist.listOfSongs);
        playlistCopy.removeAll(otherPlaylistCopy);
        otherPlaylistCopy.addAll(playlistCopy);
        /* remove definitely duplicates */
        ArrayList<Song> result = new ArrayList<>();
        int numberOfSongs = otherPlaylistCopy.size();
        for (int i = 0; i < numberOfSongs; i++) {
            Song s1 = otherPlaylistCopy.get(i);
            result.add(s1);
            for (int j = i + 1; j < numberOfSongs; j++) {
                Song s2 = otherPlaylistCopy.get(j);
                if (s1.definitelyEquals(s2)) {
                    result.remove(s1);
                    break;
                }
            }
        }
        return new Playlist(result);
    }

    public void sort() {
        Collections.sort(this.listOfSongs, Collections.reverseOrder());
    }

    public void shuffle() {
        Collections.shuffle(this.listOfSongs);
    }

    public void writingJSONForPlaylist() {
        try {
            File inputFile = new File("MusicPlaylist_TEST.json");
            FileOutputStream is = new FileOutputStream(inputFile);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            w.write("{\n");
            w.write("\"songs\":[\n");
            int comma = 1;
            for (Song song: this.getListOfSongs()) {
                w.write(song.toJSON());
                if(comma < this.getListOfSongs().size()) {
                    w.write(",");
                }
                w.write("\n");
                comma++;
            }
            w.write("]");
            w.write("\n}");
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file MusicPlaylist_TEST.json");
        }
    }

    public void writingXMLForPlaylist() {
        try {
            File inputFile = new File("MusicPlaylist_TEST.xml");
            FileOutputStream is = new FileOutputStream(inputFile);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            w.write("<playlist>\n");
            w.write("<songs>\n");
            for (Song song: this.getListOfSongs()) {
                w.write(song.toXML());
                w.write("\n");
            }
            w.write("</songs>\n");
            w.write("</playlist>\n");
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file MusicPlaylist_TEST.xml");
        }
    }
}