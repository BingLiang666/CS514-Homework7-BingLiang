package edu.usfca.cs;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Display {
    protected UserPrompt userPrompt = new UserPrompt();
    protected Scanner input = new Scanner(System.in);
    protected String userInput;
    protected IsInteger isInteger = new IsInteger();

    /**
     *
     * @param songs arrayList of songs
     *
     * This displays all songs in an arrayList in a formatted way.
     *
     */
    public void displaySongs(ArrayList<Song> songs) {
        System.out.println("--------------------------------------------------------------SONGS--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songReleaseDate", "songLanguage");
        for (Song s: songs) {
            System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                    s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getSongReleaseDate(), s.getSongLanguage());
        }
    }

    /**
     *
     * @param artists arrayList of artists
     *
     * This displays all artists in an arrayList in a formatted way.
     *
     */
    public void displayArtists(ArrayList<Artist> artists) {
        System.out.println("--------------------------------------------------------------ARTISTS--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-10s | %-12s | %s\n", "id", "name", "AudioDB_ID", "nAlbums",
                "nSongs", "mood", "artistStyle", "artistArea");
        for (Artist a: artists) {
            System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-10s | %-12s | %s\n", a.getEntityID(), a.getName(), a.getAudioDB_ID(),
                    a.getAlbums().size(), a.getSongs().size(), a.getMood(), a.getStyle(), a.getArtistArea());
        }
    }

    /**
     *
     * @param albums arrayList of albums
     *
     * This displays all albums in an arrayList in a formatted way.
     *
     */
    public void displayAlbums(ArrayList<Album> albums) {
        System.out.println("--------------------------------------------------------------ALBUMS--------------------------------------------------------------");
        System.out.format("%-3s | %-28s | %-10s | %-8s | %-8s | %-18s | %s\n", "id", "name", "AudioDB_ID",
                "artistID", "nSongs", "albumReleaseDate", "albumGenre");
        for (Album a: albums) {
            System.out.format("%-3s | %-28s | %-10s | %-8s | %-8s | %-18s | %s\n", a.getEntityID(), a.getName(), a.getAudioDB_ID(),
                    a.getArtist().getEntityID(), a.getSongs().size(), a.getReleaseDate(), a.getAlbumGenre());
        }
    }

    /**
     *
     * @param library music library
     *
     * This prompts users to choose things needed to be displayed when they want to see a part of library.
     *
     */
    public void displayPartOfLibrary(Library library) {
        System.out.println("What kind of information would you prefer to see?(Please enter 1, 2, or 3.)");
        System.out.println("-1- Song");
        System.out.println("-2- Artist");
        System.out.println("-3- Album");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displayPartOfSongsInLibrary(library);
                break;
            case "2":
                this.displayPartOfArtistsInLibrary(library);
                break;
            case "3":
                this.displayPartOfAlbumsInLibrary(library);
                break;
            default:
                System.out.println("Invalid input. Please enter a number from 1~3.");
                this.displayPartOfLibrary(library);
                break;
        }
    }

    /**
     *
     * @param library music library
     *
     * This prompts users to choose which songs needed to be displayed when they want to see some of them.
     *
     */
    public void displayPartOfSongsInLibrary(Library library) {
        System.out.println("What kind of songs would you prefer to see?(Please enter 1, 2, or 3.)");
        System.out.println("-1- Songs After Certain Year.");
        System.out.println("-2- Songs From Certain Artist.");
        System.out.println("-3- Songs From Certain Album.");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displaySongsAfterCertainYear(library, input);
                break;
            case "2":
                this.displaySongsFromCertainArtist(library);
                break;
            case "3":
                this.displaySongsFromCertainAlbum(library);
                break;
            default:
                System.out.println("Invalid input. Please enter a number from 1~3.");
                this.displayPartOfSongsInLibrary(library);
                break;
        }
    }

    /**
     *
     * @param library music library
     * @param input scanner from keyboard
     *
     * This prompts users to enter a certain year and displays songs stored in the library that were released after that year.
     *
     */
    public void displaySongsAfterCertainYear(Library library, Scanner input) {
        System.out.println("Please enter that year.");
        int year = userPrompt.requestForYear(input);
        this.songsFromCertainYear(library, year);
        //this.requestForAnotherSearch(library);
    }

    /**
     *
     * @param library music library
     * @param year year entered by users
     *
     * This displays qualified songs that were released after certain year.
     *
     */
    public void songsFromCertainYear(Library library, int year) {
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songReleaseDate", "songLanguage");
        for(Song s: library.getSongs()) {
            String yearOfSong = s.getSongReleaseDate().substring(0,4);
            if (Integer.parseInt(yearOfSong) >= year) {
                System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                        s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getSongReleaseDate(), s.getSongLanguage());
                index++;
            }
        }
        if (index == 0) {
            System.out.println("No qualified songs in the current database. You can add such songs later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
    }

    /**
     *
     * @param library music library
     *
     * This displays qualified songs owned by certain artist.
     *
     */
    public void displaySongsFromCertainArtist (Library library) {
        System.out.println("Please enter the name of an artist.");
        userInput = input.nextLine();
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songReleaseDate", "songLanguage");
        for(Song s: library.getSongs()) {
            String artistName = s.getPerformer().getName().toLowerCase(Locale.ROOT);
            if (artistName.contains(userInput.toLowerCase(Locale.ROOT))) {
                System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                        s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getSongReleaseDate(), s.getSongLanguage());
                index++;
            }
        }
        if (index == 0) {
            System.out.println("No qualified songs in the current database. You can add such songs later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        //this.requestForAnotherSearch(library);
    }

    /**
     *
     * @param library music library
     *
     * This displays qualified songs that are stored in certain album.
     *
     */
    public void displaySongsFromCertainAlbum(Library library) {
        System.out.println("Please enter the name of an album.");
        userInput = input.nextLine();
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songReleaseDate", "songLanguage");
        for(Song s: library.getSongs()) {
            String albumName = s.getAlbum().getName().toLowerCase(Locale.ROOT);
            if (albumName.toLowerCase(Locale.ROOT).contains(userInput.toLowerCase(Locale.ROOT))) {
                System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                        s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getSongReleaseDate(), s.getSongLanguage());
                index++;
            }
        }
        if (index == 0) {
            System.out.println("No qualified songs in the current database. You can add such songs later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        //this.requestForAnotherSearch(library);
    }

    /**
     *
     * @param library music library
     *
     * This prompts users to choose which artists needed to be displayed when they want to see some of them.
     *
     */
    public void displayPartOfArtistsInLibrary(Library library) {
        System.out.println("What kind of artists would you prefer to see?(Please enter 1 or 2.)");
        System.out.println("-1- Artists with certain style.");
        System.out.println("-2- Artists from certain area.");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displayArtistsWithCertainStyle(library);
                break;
            case "2":
                this.displayArtistsFromCertainArea(library);
                break;
            default:
                System.out.println("Invalid input. Please enter a number from 1~2.");
                this.displayPartOfArtistsInLibrary(library);
                break;
        }
    }

    /**
     *
     * @param library music library
     *
     * This displays qualified artists who are in certain styles.
     *
     */
    public void displayArtistsWithCertainStyle(Library library) {
        System.out.println("Please enter the artist style.(ex. pop/rock/urban/R&B...)");
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
        //this.requestForAnotherSearch(library);
    }

    /**
     *
     * @param library music library
     *
     * This displays qualified artists who are from certain area.
     *
     */
    public void displayArtistsFromCertainArea(Library library) {
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
        //this.requestForAnotherSearch(library);
    }

    /**
     *
     * @param library music library
     *
     * This prompts users to choose which albums needed to be displayed when they want to see some of them.
     *
     */
    public void displayPartOfAlbumsInLibrary(Library library) {
        System.out.println("What kind of albums would you prefer to see?(Please enter 1 or 2.)");
        System.out.println("-1- Albums from certain artists.");
        System.out.println("-2- Albums in certain genre.");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displayAlbumsFromCertainArtists(library);
                break;
            case "2":
                this.displayAlbumsInCertainGenre(library);
                break;
            default:
                System.out.println("Invalid input. Please enter a number from 1~2.");
                this.displayPartOfAlbumsInLibrary(library);
        }
    }

    /**
     *
     * @param library music library
     *
     * This displays qualified albums owned by certain artist.
     *
     */
    public void displayAlbumsFromCertainArtists(Library library) {
        System.out.println("Please enter the name of an artist.");
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
            System.out.println("No qualified artists in the current database. You can add such albums later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        //this.requestForAnotherSearch(library);
    }

    /**
     *
     * @param library music library
     *
     * This displays qualified albums that are in certain genre.
     *
     */
    public void displayAlbumsInCertainGenre(Library library) {
        System.out.println("Please enter the music genre.(ex. pop/soul/rock...)");
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
            System.out.println("No qualified artists in the current database. You can add such albums later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        //this.requestForAnotherSearch(library);
    }

    /**
     *
     * @param library music library
     *
     * This prompts users if they want to do another round of search.
     *
     */
    public void requestForAnotherSearch(Library library) {
        System.out.println("Do you prefer another round of search? (Y/N)");
        String userChoice = input.nextLine();
        while (true) {
            if (userChoice.equalsIgnoreCase("y")) {
                this.displayPartOfLibrary(library);
            } else if (userChoice.equalsIgnoreCase("n")) {
                System.out.println("Great! Let's jump back to the main menu.");
                break;
            } else {
                System.out.println("Invalid input. Please enter again.");
                userChoice = input.nextLine();
            }
        }
    }
}
