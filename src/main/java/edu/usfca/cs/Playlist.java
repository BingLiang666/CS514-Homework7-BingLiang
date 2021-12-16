package edu.usfca.cs;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Playlist {
    protected String userInput;
    protected Scanner input = new Scanner(System.in);
    private ArrayList<Song> listOfSongs;
    protected UserPrompt userPrompt = new UserPrompt();

    /**
     *
     * @param listOfSongs arrayList of songs
     *
     * This is a constructor.
     *
     */
    public Playlist(ArrayList<Song> listOfSongs) {
        this.listOfSongs = listOfSongs;
    }

    /**
     * This is a constructor.
     */
    public Playlist() {
        listOfSongs = new ArrayList<>();
    }

    /**
     *
     * @param s song
     * @return
     *
     * This returns true if the playlist contains certain song, otherwise returns false.
     *
     */
    public boolean findSong(Song s) {
        return listOfSongs.contains(s);
    }

    /**
     *
     * @return
     *
     * This returns an arrayList of songs stored in the playlist.
     *
     */
    public ArrayList<Song> getListOfSongs() {
        return listOfSongs;
    }

    /**
     *
     * @param newSong song
     *
     * This adds a new song into the playlist.
     *
     */
    public void addSong(Song newSong) {
        listOfSongs.add(newSong);
    }

    /**
     *
     * @param song song
     *
     * This removes certain song from the playlist.
     *
     */
    public void deleteSong(Song song) {
        listOfSongs.remove(song);
    }

    /**
     *
     * @param otherPlaylist playlist
     * @return playlist
     *
     * This merges two playlists as one playlist and remains only one piece of the identical songs that both playlists have.
     *
     */
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

    /**
     * This sorts the songs in the playlist based on their likes from high to low.
     */
    public void sort() {
        Collections.sort(this.listOfSongs, Collections.reverseOrder());
    }

    /**
     * This randomly changes the orders of the songs in the library.
     */
    public void shuffle() {
        Collections.shuffle(this.listOfSongs);
    }

    /**
     * This generates a JSON file for the playlist.
     */
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

    /**
     * This generates an XML file for the playlist.
     */
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

    /**
     *
     * @param library music library
     * @return playlist
     *
     * This prompts users two ways to generate the playlist of songs.
     * This returns the newly generated playlist.
     *
     */
    public Playlist generatePlaylist(Library library) {
        System.out.println("In which way would you prefer to generate the playlist?(Please enter 1 or 2.)");
        System.out.println("-1- By Likes Of Song");
        System.out.println("-2- By Release Year Of Song");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                return this.generatePlaylistByLikes(library, input);
            case "2":
                return this.generatePlaylistByReleaseYear(library, input);
            default:
                System.out.println("Invalid input. Please enter a number from 1~3.");
                this.generatePlaylist(library);
        }
        return null;
    }

    /**
     *
     * @param library music library
     * @param input scanner from keyboard
     * @return playlist
     *
     * This returns a playlist based on the likes of songs from high to low.
     * All songs in the playlist have likes that is bigger than the minimum value of likes given by users.
     *
     */
    public Playlist generatePlaylistByLikes(Library library, Scanner input) {
        System.out.println("First, let's check if all the songs in library have likes.");
        System.out.println("You might need to set likes for each song manually for the first time.");
        System.out.println("(To set likes for a song, you are required to enter an INTEGER between 1~5 that represents the level you like the song.)");
        for (Song s: library.getSongs()) {
            if (s.getLikes() == 0) {
                System.out.print("Please set likes for the song \"" + s.getName() + "\": ");
                int likes = userPrompt.promptUserForLikes(s, input);
                s.setLikes(likes);
            }
        }
        System.out.println("Great! All songs have likes in them.\nWe would generate the playlist by likes now.");
        int lowestLikes = userPrompt.requestForMinimumLikes(input);
        for (Song s: library.getSongs()) {
            if (s.getLikes() >= lowestLikes) {
                this.addSong(s);
            }
        }
        this.sort();
        this.writingXMLForPlaylist();
        System.out.println("Playlist based on likes of song is generated successfully.\nPlease NOTE: Songs in this playlist are sorted by their likes from high to low.");
        return this;
    }

    /**
     *
     * @param library music library
     * @param input scanner from keyboard
     * @return playlist
     *
     * This generates a playlist that contains songs from certain year.
     *
     */
    public Playlist generatePlaylistByReleaseYear(Library library, Scanner input) {
        System.out.println("Please indicate songs after which year would you like to add into the playlist?");
        int year = userPrompt.requestForYear(input);
        for (Song s: library.getSongs()) {
            if (Integer.parseInt(s.getSongReleaseDate().substring(0,4)) >= year) {
                this.addSong(s);
            }
        }
        this.writingXMLForPlaylist();
        System.out.println("Playlist containing songs that are from the year of \"" + year + "\" is generated successfully.");
        return this;
    }

    /**
     *
     * @param library music library
     * @param input scanner from keyboard
     * @return
     *
     * This returns a playlist whose songs' order is randomly shuffled.
     *
     */
    public Playlist shufflePlaylist(Library library, Scanner input) {
        this.shuffle();
        this.writingXMLForPlaylist();
        System.out.println("Playlist is shuffled successfully.");
        return this;
    }
}