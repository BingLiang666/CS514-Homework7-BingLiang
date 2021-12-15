package edu.usfca.cs;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class UserPrompt {
    String userInput;
    Scanner input = new Scanner(System.in);


    public boolean keepArtistInLibraryForSongOrNot() {
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

    public String requestForAnotherInsertOfSongsWithName(){
        System.out.println("Do you prefer to do another round of song import with song name? (Please enter Y/N)");
        userInput = input.nextLine();
        while (true) {
            if (userInput.toLowerCase(Locale.ROOT).equals("n")) {
                System.out.println("Great! Let's play again.");
                break;
            } else if (userInput.toLowerCase(Locale.ROOT).equals("n")) {
                System.out.println("Great! Let's jump to the main menu.");
                break;
            } else {
                System.out.println("Invalid input. Please enter again.");
                userInput = input.nextLine();
            }
        }
        return userInput;
    }

    public String songNameRequest() {
        System.out.println("Please enter the name of your favorite artist here, and then we will automatically fill in some details of that artist for you.");
        String songName = input.nextLine();
        return(songName);
    }

    public String promptUserForYesOrNO() {
        userInput = input.nextLine();
        while (!(userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("n"))) {
            System.out.println("Invalid input. Please enter \"Y\" or \"N\"");
            userInput = input.nextLine();
        }
        return userInput;
    }


}
