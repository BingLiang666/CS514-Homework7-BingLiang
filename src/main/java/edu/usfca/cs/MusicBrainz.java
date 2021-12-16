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
import java.util.Scanner;

public class MusicBrainz {
    protected UserPrompt userPrompt = new UserPrompt();
    protected String userInput;
    protected Scanner input = new Scanner(System.in);

    /**
     *
     * @param statement SQLite statement
     * @param songName song name
     * @param library music library
     * @throws SQLException
     *
     * This finds a song in the library or MusicBrainz and automatically fill in missing details of that song based on song name.
     * This also finds the artist and album of that song in the library or MusicBrainz.
     * PLEASE NOTE: The song will be imported to the library and database only if the right song, artist, album are all found.
     *
     */
    public void insertSongsFromMusicBrainz(Statement statement, String songName, Library library) throws SQLException {
        library.setAlbumAlreadyInLibrary(false);
        library.setArtistAlreadyInLibrary(false);
        StringBuilder response = new StringBuilder();
        ArrayList<Song> duplicatedSongs = new ArrayList<>();
        this.buildConnectionWithMusicBrainz(songName, response);
        Song newSong = this.grabSongs(response, songName, duplicatedSongs, library);
        if (newSong != null && newSong.getPerformer() != null && newSong.getAlbum() != null) {
            statement.executeUpdate(newSong.toSQL());
            if (library.isArtistAlreadyInLibrary() == true) {
                statement.executeUpdate("update artists set nSongs = \'" + newSong.getPerformer().getSongs().size() + "\' where id = \'" + newSong.getPerformer().getEntityID() + "\';");
                if (library.isAlbumAlreadyInLibrary() == false) {
                    statement.executeUpdate("update artists set nAlbums = \'" + newSong.getPerformer().getAlbums().size() + "\' where id = \'" + newSong.getPerformer().getEntityID() + "\';");
                }
            } else {
                statement.executeUpdate(newSong.getPerformer().toSQL());
            }
            if (library.isAlbumAlreadyInLibrary() == true) {
                statement.executeUpdate("update albums set nSongs = \'" + newSong.getAlbum().getSongs().size() + "\' where id = \'" + newSong.getAlbum().getEntityID() + "\';");
            } else {
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

    /**
     *
     * @param songName song name
     * @param response MusicBrainz searching result
     *
     * This builds connection with MusicBrainz.
     *
     */
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
            System.out.println("Building connection to AudioDB successes: \"" + code + " " + message + "\"");
            if (!message.equalsIgnoreCase("ok")) {
                System.out.println("There is no record for the song in the AudioDB.");
            }
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

    /**
     *
     * @param response MusicBrainz searching result
     * @param songName song name
     * @param duplicatedSongs arrayList of songs
     * @param library music library
     * @return song object / null
     *
     * This grabs ar most 5 qualified songs in AudioDB and stores them in an arrayList of songs.
     *
     */
    public Song grabSongs(StringBuilder response, String songName, ArrayList<Song> duplicatedSongs, Library library) {
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

    /**
     *
     * @param duplicatedSongs arrayList of songs
     * @param library music library
     * @return song object / null
     *
     * This lets users to chose which song is the right one.
     * This returns the song object if there is a song chosen by users, otherwise returns null.
     *
     */
    public Song selectSong(ArrayList<Song> duplicatedSongs, Library library) {
        boolean artistFound;
        boolean albumFound = false;
        Song tempSong = null;
        Artist tempArtist = new Artist();
        int id;
        if (library.getArtists().size() == 0) {           // set the entityID for the first song to be 101
            id = 101;
        } else {
            id = 1 + library.getSongs().get(library.getSongs().size() - 1).getEntityID();    // set the entityID of this new song to be 1 greater the current last song in the library
        }
        Song newSong = new Song(id);
        for (int i = 0; i < duplicatedSongs.size(); i++) {
            System.out.format("#%-3dSong Name:%-40sMusicBrainz_ID:%-40sArtist Name:%-25sRelease Date:%-15sLanguage:%-10s\n", i, duplicatedSongs.get(i).getName(), duplicatedSongs.get(i).getMusicBrainzID(),
                    duplicatedSongs.get(i).getPerformer().getName(), duplicatedSongs.get(i).getSongReleaseDate(), duplicatedSongs.get(i).getSongLanguage());
        }
        System.out.println("Is there any song among those songs that you might want to import into the library? (Y/N)");
        String userInput = userPrompt.promptUserForYesOrNo();
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
            if (artistFound && albumFound) {
                if (library.findDuplicates(newSong) != null) {
                    library.addSong(newSong);
                    return newSong;
                } else {
                    System.out.println("Okay. We will not import this song.");
                }
            }
            System.out.println("Sorry...Missed data for artist/album. Song importation failed.\nLet's try something else!");
        } else {
            System.out.println("Gotcha");
            return null;
        }
        return null;
    }

}
