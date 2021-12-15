package edu.usfca.cs;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class MusicBrainz {
    protected UserPrompt userPrompt = new UserPrompt();
    protected String userInput;
    protected Scanner input = new Scanner(System.in);

    public void insertSongsFromMusicBrainz(Statement statement, String songName, Library library) throws SQLException {
        StringBuilder response = new StringBuilder();
        ArrayList<Song> duplicatedSongs = new ArrayList<>();
        this.buildConnectionWithMusicBrainz(songName, response);
        Song newSong = this.grabSongs(statement, response, songName, duplicatedSongs, library);
        if (newSong != null) {
            statement.executeUpdate(newSong.toSQL());
            if (newSong.getPerformer() != null) {
                statement.executeUpdate(newSong.getPerformer().toSQL());
            }
            if (newSong.getAlbum() != null) {
                statement.executeUpdate(newSong.getAlbum().toSQL());
            }
            System.out.println("The song \"" + newSong.getName() + "\" has been successfully imported into the database.");
        }
        while (true) {
            if (userPrompt.requestForAnotherInsertOfSongsWithName().equalsIgnoreCase("y")) {
                userInput = userPrompt.songNameRequest();
                this.insertSongsFromMusicBrainz(statement, userInput, library);
            }
            break;
        }
    }

    public void buildConnectionWithMusicBrainz(String songName, StringBuilder response) {
        String initialURL = "https://musicbrainz.org/ws/2/release?query=";
        String endURL = "&limit=10&fmt=json";
        String wholeURL = initialURL + songName + endURL;
        wholeURL = wholeURL.replaceAll(" ", "%12");
        URL u;
        try {
            u = new URL(wholeURL);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
            return;
        }
        try {
            URLConnection connection = u.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();

            String message = httpConnection.getResponseMessage();
            System.out.println(code + " " + message);
            if (code != HttpURLConnection.HTTP_OK) {
                return;
            }
            InputStream inStream = connection.getInputStream();
            Scanner in = new Scanner(inStream);
            while (in.hasNextLine()) {
                response.append(in.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading response");
            return;
        }
    }

    public Song grabSongs(Statement statement, StringBuilder response, String songName, ArrayList<Song> duplicatedSongs, Library library) {
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray releases = (JSONArray) jsonObject.get("releases");
            System.out.println("We have found various of songs containing \"" + songName +
                    "\".\nFollowings are the top 5 matched searching results.");
            for (int i = 0; i < releases.size() && i < 5; i++) {
                Song tempSong = new Song();
                Artist tempArtist = new Artist();
                JSONObject currentRelease = (JSONObject) releases.get(i);
                String songTitle = (String) currentRelease.get("title");
                tempSong.setName(songTitle);
                String MusicBrainz_ID = (String) currentRelease.get("id");
                tempSong.setMusicBrainzID(MusicBrainz_ID);
                JSONArray artists = (JSONArray) currentRelease.get("artist-credit");
                JSONObject currentArtist = (JSONObject) artists.get(0);
                String currentArtistName = (String) currentArtist.get("name");
                tempArtist.setName(currentArtistName);
                tempSong.setPerformer(tempArtist);
                JSONObject text = (JSONObject) currentRelease.get("text-representation");
                if (text != null) {
                    String songLanguage = (String) text.get("language");
                    tempSong.setSongLanguage(songLanguage);
                } else {
                    tempSong.setSongLanguage("null");
                }
                String releaseDate = (String) currentRelease.get("date");
                tempSong.setSongReleaseDate(releaseDate);
                duplicatedSongs.add(tempSong);
            }
            return this.selectSong(duplicatedSongs, library);
        } catch (ParseException e) {
            System.out.println("Error parsing JSON");
        }
        return null;
    }

    public Song selectSong(ArrayList<Song> duplicatedSongs, Library library) {
        boolean artistFound;
        boolean albumFound = false;
        Song tempSong = null;
        Artist tempArtist = new Artist();
        int id = 101 + library.getSongs().size();
        Song newSong = new Song(id);
        for (int i = 0; i < duplicatedSongs.size(); i++) {
            System.out.format("#%-3dSong Name:%-40sMusicBrainz_ID:%-40sArtist Name:%-25sRelease Date:%-15sLanguage:%-10s\n", i, duplicatedSongs.get(i).getName(), duplicatedSongs.get(i).getMusicBrainzID(),
                    duplicatedSongs.get(i).getPerformer().getName(), duplicatedSongs.get(i).getSongReleaseDate(), duplicatedSongs.get(i).getSongLanguage());
        }
        System.out.println("Is there any song among those 5 songs that you might want to import into the library? (Enter Y/N)");
        String userInput = userPrompt.promptUserForYesOrNO();
        if (userInput.equalsIgnoreCase("y")) {
            System.out.println("Great! Now please choose the song which is closest to the song that you are looking for." +
                    "\n(Input the exact number after # to import the chosen one:)");
            userInput = input.nextLine();
            while (!((userInput.equals("0")) || (userInput.equals("1")) || (userInput.equals("2")) || (userInput.equals("3")) || (userInput.equals("4")))) {
                System.out.println("Invalid input. Please enter an integer from 0~4.");
                userInput = input.nextLine();
            }
            switch (userInput) {
                case "0":
                    tempSong = duplicatedSongs.get(0);
                    break;
                case "1":
                    tempSong = duplicatedSongs.get(1);
                    break;
                case "2":
                    tempSong = duplicatedSongs.get(2);
                    break;
                case "3":
                    tempSong = duplicatedSongs.get(3);
                    break;
                case "4":
                    tempSong = duplicatedSongs.get(4);
                    break;
                default:
                    System.out.println("Bad input.");
                    break;
            }
            newSong.setName(tempSong.getName());
            newSong.setMusicBrainzID(tempSong.getMusicBrainzID());
            newSong.setSongReleaseDate(tempSong.getSongReleaseDate());
            newSong.setSongLanguage(tempSong.getSongLanguage());
            tempArtist.setName(tempSong.getPerformer().getName());
            newSong.setPerformer(tempArtist);
            artistFound = newSong.setArtistForSong(library);
            if (artistFound) {         // if there is an artists added to the song, we can further add album for the song, otherwise we stop finding album for the song
                albumFound = newSong.setAlbumForSong(library);
            }
            if (library.findDuplicates(newSong) != null) {
                if (artistFound) {
                    if (!albumFound) {
                        System.out.println("There is no album in the library or AudioDB that could be linked to the song you are trying to import.\n" +
                                "Do you want to import that song any way?(Y/N)");
                        if (userPrompt.promptUserForYesOrNO().equalsIgnoreCase("y")) {
                            library.addSong(newSong);
                            return newSong;
                        } else {
                            System.out.println("Gotcha! We will not import the song.");
                            return null;
                        }
                    }
                    library.addSong(newSong);
                    return newSong;
                } else {
                    System.out.println("There is no album or artist in the library or AudioDB that could be linked to the song you are trying to import.\n" +
                            "Do you want to import that song any way?(Y/N)");
                    if (userPrompt.promptUserForYesOrNO().equalsIgnoreCase("y")) {
                        library.addSong(newSong);
                        return newSong;
                    } else {
                        System.out.println("Gotcha! We will not import the song.");
                        return null;
                    }
                }
            }
        } else {
            System.out.println("Gotcha");
            return null;
        }
        return null;
    }

}
