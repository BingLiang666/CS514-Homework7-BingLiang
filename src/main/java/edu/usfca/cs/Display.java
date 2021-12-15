package edu.usfca.cs;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Display {
    protected UserPrompt userPrompt;
    protected Scanner input = new Scanner(System.in);
    protected String userInput;
    protected SQLite sqLite;
    protected IsInteger isInteger;


    public void displaySongs(ArrayList<Song> songs) {
        System.out.println("--------------------------------------------------------------SONGS--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songGenre", "songReleaseDate", "songLanguage");
        for (Song s: songs) {
            System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                    s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getGenre(), s.getSongReleaseDate(), s.getSongLanguage());
        }
    }

    public void displayArtists(ArrayList<Artist> artists) {
        System.out.println("--------------------------------------------------------------ARTISTS--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-10s | %-12s | %s\n", "id", "name", "AudioDB_ID", "nAlbums",
                "nSongs", "mood", "artistStyle", "artistArea");
        for (Artist a: artists) {
            System.out.format("%-3s | %-25s | %-10s | %-8s | %-8s | %-10s | %-12s | %s\n", a.getEntityID(), a.getName(), a.getAudioDB_ID(),
                    a.getAlbums().size(), a.getSongs().size(), a.getMood(), a.getStyle(), a.getArtistArea());
        }
    }

    public void displayAlbums(ArrayList<Album> albums) {
        System.out.println("--------------------------------------------------------------ALBUMS--------------------------------------------------------------");
        System.out.format("%-3s | %-28s | %-10s | %-8s | %-8s | %-18s | %s\n", "id", "name", "AudioDB_ID",
                "artistID", "nSongs", "albumReleaseDate", "albumGenre");
        for (Album a: albums) {
            System.out.format("%-3s | %-28s | %-10s | %-8s | %-8s | %-18s | %s\n", a.getEntityID(), a.getName(), a.getAudioDB_ID(),
                    a.getArtist().getEntityID(), a.getSongs().size(), a.getReleaseDate(), a.getAlbumGenre());
        }
    }

    public void displayPartOfLibrary(Library library) {
        System.out.println("What kind of information would you prefer to see?(Please enter 1, 2, 3, or 4.)");
        System.out.println("-1- Song");
        System.out.println("-2- Artist");
        System.out.println("-3- Album");
        System.out.println("-4- Back to main menu.");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displayPartOfSongsInLibrary(library);
            case "2":
                this.displayPartOfArtistsInLibrary(library);
            case "3":
                this.displayPartOfAlbumsInLibrary(library);
            case "4":
                sqLite.mainMenu();
        }
    }

    public void displayPartOfSongsInLibrary(Library library) {
        System.out.println("What kind of songs would you prefer to see?(Please enter 1, 2, or 3.)");
        System.out.println("-1- Songs after certain year.");
        System.out.println("-2- Songs from certain artist.");
        System.out.println("-3- Songs from certain album.");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displaySongsAfterCertainYear(library);
            case "2":
                this.displaySongsFromCertainArtist(library);
            case "3":
                this.displaySongsFromCertainAlbum(library);
            default:
                System.out.println("Invalid input. Please enter a number from 1~3.");
                this.displayPartOfSongsInLibrary(library);
                break;
        }
    }

    public void displaySongsAfterCertainYear(Library library) {
        System.out.println("Please enter that year.(Please enter a number smaller than 2021 in 4-digit format, eg, 2020.)");
        userInput = input.nextLine();
        int year;
        while (true) {
            if (isInteger.isInteger(userInput)) {
                year = Integer.parseInt(userInput);
                if (year <= 2021) {
                    break;
                }
            } else {
                System.out.println("Mal-format of year you just entered. Please enter again.");
                userInput = input.nextLine();
            }
        }
        this.songsFromCertainYear(library, year);
        this.requestForAnotherSearch(library);
        sqLite.mainMenu();
    }

    public void songsFromCertainYear(Library library, int year) {
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songGenre", "songReleaseDate", "songLanguage");
        for(Song s: library.getSongs()) {
            String yearOfSong = s.getSongReleaseDate().substring(0,4);
            if (Integer.parseInt(yearOfSong) <= year) {
                System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                        s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getGenre(), s.getSongReleaseDate(), s.getSongLanguage());
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
    }

    public void displaySongsFromCertainArtist (Library library) {
        System.out.println("Please enter the last name of an artist.");
        userInput = input.nextLine();
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songGenre", "songReleaseDate", "songLanguage");
        for(Song s: library.getSongs()) {
            String artistName = s.getPerformer().getName().toLowerCase(Locale.ROOT);
            if (artistName.contains(userInput.toLowerCase(Locale.ROOT))) {
                System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                        s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getGenre(), s.getSongReleaseDate(), s.getSongLanguage());
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
        this.requestForAnotherSearch(library);
        sqLite.mainMenu();
    }

    public void displaySongsFromCertainAlbum(Library library) {
        System.out.println("Please enter the name of an album.");
        userInput = input.nextLine();
        int index = 0;
        System.out.println("--------------------------------------------------------------Searching Results--------------------------------------------------------------");
        System.out.format("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", "id", "name", "MusicBrainz_ID", "artistID",
                "albumID", "songGenre", "songReleaseDate", "songLanguage");
        for(Song s: library.getSongs()) {
            String albumName = s.getAlbum().getName().toLowerCase(Locale.ROOT);
            if (albumName.contains(userInput.toLowerCase(Locale.ROOT))) {
                System.out.printf("%-3s | %-25s | %-36s | %-8s | %-8s | %-10s | %-16s | %s\n", s.getEntityID(), s.getName(), s.getMusicBrainzID(),
                        s.getPerformer().getEntityID(), s.getAlbum().getEntityID(), s.getGenre(), s.getSongReleaseDate(), s.getSongLanguage());
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
        this.requestForAnotherSearch(library);
        sqLite.mainMenu();
    }

    public void requestForAnotherSearch(Library library) {
        System.out.println("Do you prefer to do another round of search? (Please enter Y/N)");
        String userChoice = input.nextLine();
        while (true) {
            if (userChoice.toLowerCase(Locale.ROOT).equals("y")) {
                this.displayPartOfLibrary(library);
            } else if (userChoice.toLowerCase(Locale.ROOT).equals("n")) {
                System.out.println("Great! Let's jump to the main menu.");
                break;
            } else {
                System.out.println("Invalid input. Please enter again.");
                userChoice = input.nextLine();
            }
        }
    }

    public void displayPartOfArtistsInLibrary(Library library) {
        System.out.println("What kind of artists would you prefer to see?(Please enter 1 or 2.)");
        System.out.println("-1- Artists with certain style.");
        System.out.println("-2- Artists from certain area.");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displayArtistsWithCertainStyle(library);
            case "2":
                this.displayArtistsFromCertainArea(library);
            default:
                System.out.println("Invalid input. Please enter a number from 1~2.");
                this.displayPartOfArtistsInLibrary(library);
                break;
        }
    }

    public void displayArtistsWithCertainStyle(Library library) {
        System.out.println("Please enter the style.");
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
        this.requestForAnotherSearch(library);
        sqLite.mainMenu();
    }

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
        this.requestForAnotherSearch(library);
        sqLite.mainMenu();
    }

    public void displayPartOfAlbumsInLibrary(Library library) {
        System.out.println("What kind of albums would you prefer to see?(Please enter 1 or 2.)");
        System.out.println("-1- Albums from certain artists.");
        System.out.println("-2- Albums in certain genre.");
        userInput = input.nextLine();
        switch (userInput) {
            case "1":
                this.displayAlbumsFromCertainArtists(library);
            case "2":
                this.displayAlbumsInCertainGenre(library);
            default:
                System.out.println("Invalid input. Please enter a number from 1~2.");
                this.displayPartOfAlbumsInLibrary(library);
                break;
        }
    }

    public void displayAlbumsFromCertainArtists(Library library) {
        System.out.println("Please enter the last name of an artist.");
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
            System.out.println("No qualified artists in the current database. You can add such artists later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        this.requestForAnotherSearch(library);
        sqLite.mainMenu();
    }

    public void displayAlbumsInCertainGenre(Library library) {
        System.out.println("Please enter the last name of an artist.");
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
            System.out.println("No qualified artists in the current database. You can add such artists later.");
        } else if (index == 1){
            System.out.println("(1 result in total)");
        } else {
            System.out.println("(" + index + " results in total)");
        }
        this.requestForAnotherSearch(library);
        sqLite.mainMenu();
    }
}
