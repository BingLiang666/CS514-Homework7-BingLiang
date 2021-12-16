package edu.usfca.cs;

import java.util.Locale;
import java.util.Scanner;

public class UserPrompt {
    String userInput;
    Scanner input = new Scanner(System.in);
    IsInteger isInteger = new IsInteger();

    /**
     *
     * @return user answer
     *
     * This prompts users if they think the artist is the right one for the song.
     * This returns true if the answer from users is yes, otherwise returns false.
     *
     */
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

    /**
     *
     * @return user answer(Y/N)
     *
     * This prompts users if they want to do another round of song importation.
     * This returns the answer from users.
     *
     */
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

    /**
     *
     * @return song name
     *
     * This prompts users for song name.
     * This returns the song name given by users.
     *
     */
    public String songNameRequest() {
        System.out.println("Please enter the name of your favorite song here, and then we will automatically fill in some details of that song for you.");
        String songName = input.nextLine();
        return(songName);
    }

    /**
     *
     * @return answer from users(Y/N)
     *
     * This prompts users for yes or no.
     * This returns the answer from users.
     *
     */
    public String promptUserForYesOrNo() {
        userInput = input.nextLine();
        while (!(userInput.equalsIgnoreCase("y") || userInput.equalsIgnoreCase("n"))) {
            System.out.println("Invalid input. Please enter \"Y\" or \"N\".");
            userInput = input.nextLine();
        }
        return userInput;
    }

    /**
     *
     * @param s song
     * @param input scanner from keyboard
     * @return likes of song
     *
     * This prompts users for the likes of a song.
     * This returns the value for likes of a song given by users.
     *
     */
    public int promptUserForLikes(Song s, Scanner input) {
        System.out.print("Please set likes for the song \"" + s.getName() + "\": ");
        userInput = input.nextLine();
        int likes;
        while (true) {
            if (isInteger.isInteger(userInput)) {
                likes = Integer.valueOf(userInput);
                if (likes >= 1 && likes <= 5) {
                    System.out.println("The likes of song \"" + s.getName() + "\" has been successfully set to " + likes + ".");
                    break;
                }
            }
            System.out.println("Invalid input. The likes should be an INTEGER between 1~5.");
            userInput = input.nextLine();
        }
        return likes;
    }

    /**
     *
     * @param input scanner from keyboard
     * @return
     *
     * This prompts users for the minimum value of likes of a song.
     * This returns the minimum value of likes of a song given by users.
     *
     */
    public int requestForMinimumLikes(Scanner input) {
        System.out.println("First, please indicate the lowest likes of songs.\n" +
                "Then we will generate the playlist whose songs all have likes that are equal to or higher than the lowest value.");
        userInput = input.nextLine();
        int lowestLikes;
        while (true) {
            if (isInteger.isInteger(userInput)) {
                lowestLikes = Integer.valueOf(userInput);
                if (lowestLikes >= 1 && lowestLikes <= 5) {
                    break;
                }
            }
            System.out.println("Invalid input. The lowest likes should be an INTEGER between 1~5.");
            userInput = input.nextLine();
        }
        return lowestLikes;
    }

    /**
     *
     * @param input scanner from keyboard
     * @return value of a year
     *
     * This prompts users for the value of a year.
     * This returns the value of a year given by users.
     *
     */
    public int requestForYear(Scanner input) {
        System.out.println("(Note: The number represented for year should be smaller than or equal to 2021 and in 4-digit format, eg, 2020.)");
        userInput = input.nextLine();
        int year;
        while (true) {
            if (isInteger.isInteger(userInput) && userInput.length() == 4) {
                year = Integer.parseInt(userInput);
                if (year <= 2021) {
                    break;
                } else {
                    System.out.println("Invalid input! The year you entered is greater than 2021. Please enter again.");
                    userInput = input.nextLine();
                }
            } else {
                System.out.println("Mal-format of year you just entered. Please enter again.");
                userInput = input.nextLine();
            }
        }
        return year;
    }
}
