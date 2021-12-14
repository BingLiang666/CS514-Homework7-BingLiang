package edu.usfca.cs;

import java.util.Scanner;

public class UserPrompt {

    public boolean keepArtistInLibraryForSongOrNot() {
        Scanner input = new Scanner(System.in);
        System.out.println("Is this artist the one for the song? (Y/N)");
        String userInput = input.nextLine();
        while (!(userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("n"))) {
            System.out.println("Invalid input. Please enter \"Y\" or \"N\"");
            userInput = input.nextLine();
        }
        if (userInput.equalsIgnoreCase("y")) {
            return true;
        }
        return false;
    }

    public boolean keepArtistInAudioDBForSongOrNot() {
        Scanner input = new Scanner(System.in);
        System.out.println("Is this artist the one for the song? (Y/N)");
        System.out.println("Please NOTE: if you doesn't choose to import the artist that we have found for you in AudioDB," +
                "The song will be imported to the library with no artist or album linked to it!!!");
        String userInput = input.nextLine();
        while (!(userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("n"))) {
            System.out.println("Invalid input. Please enter \"Y\" or \"N\"");
            userInput = input.nextLine();
        }
        if (userInput.equalsIgnoreCase("y")) {
            return true;
        }
        return false;
    }

}
