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

    /*
    public boolean keepArtistInAudioDBForSongOrNot() {
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
    }*/

    public String requestForAnotherInsertOfSongsWithName(){
        System.out.println("Do you prefer to do another round of song import with song name? (Y/N)");
        userInput = input.nextLine();
        while (true) {
            if (userInput.toLowerCase(Locale.ROOT).equals("y")) {
                System.out.println("Great! Let's play again.");
                break;
            } else if (userInput.toLowerCase(Locale.ROOT).equals("n")) {
                System.out.println("Great! Let's jump back to the main menu.");
                break;
            } else {
                System.out.println("Invalid input. Please enter \"Y\" or \"N\"");
                userInput = input.nextLine();
            }
        }
        return userInput;
    }

    public String songNameRequest() {
        System.out.println("Please enter the name of your favorite song here, and then we will automatically fill in some details of that song for you.");
        String songName = input.nextLine();
        return(songName);
    }

    public String promptUserForYesOrNo() {
        userInput = input.nextLine();
        while (!(userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("n"))) {
            System.out.println("Invalid input. Please enter \"Y\" or \"N\"");
            userInput = input.nextLine();
        }
        return userInput;
    }


}
