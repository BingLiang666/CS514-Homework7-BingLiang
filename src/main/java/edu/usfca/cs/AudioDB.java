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
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class AudioDB {
    protected UserPrompt userPrompt = new UserPrompt();
    protected String userInput;
    protected Scanner input = new Scanner(System.in);

    /**
     *
     * @param artistName artist name
     * @param library music library
     * @return artist / null
     *
     * This finds an artist in the current library as well as AudioDB and automatically fill in missing details of that song based on artist name.
     * This returns an artist object if there is one that has been found, otherwise returns null.
     * PLEASE NOTE: If the artist already exists in the library, then this also returns null.
     *
     */
    public Artist insertArtistFromAudioDB(String artistName, Library library) {
        for (Artist a: library.getArtists()) {
            if (a.getName().equalsIgnoreCase(artistName)) {
                System.out.println("Great! We have found the artist in the library.");
                System.out.println("---ARTIST INFO---");
                System.out.format("Artist Name:%-40sAudioDB_ID:%-18sArtist Style:%-18sArtist Area:%-10s\n", a.getName(), a.getAudioDB_ID(),
                        a.getStyle(), a.getArtistArea());

                return null;
            }
        }
        String requestURL = "https://www.theaudiodb.com/api/v1/json/2/search.php?s=";
        StringBuilder response = new StringBuilder();
        int id;
        if (library.getArtists().size() == 0) {           // set the entityID for the first artist to be 201
            id = 201;
        } else {
            id = 1 + library.getArtists().get(library.getArtists().size() - 1).getEntityID(); // set the entityID of this new artist to be 1 greater the current last artist in the library
        }
        Artist newArtist = new Artist(id, artistName);
        URL u;
        try {
            u = new URL(requestURL + artistName);
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
            return null;
        }
        try {
            URLConnection connection = u.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();

            String message = httpConnection.getResponseMessage();
            System.out.println("Building connection to AudioDB successes: \"" + code + " " + message + "\"");
            if (!message.equalsIgnoreCase("ok")) {
                System.out.println("There is no record for the artist in the AudioDB.");
            }
            if (code != HttpURLConnection.HTTP_OK) {
                return null;
            }
            InputStream inStream = connection.getInputStream();
            Scanner in = new Scanner(inStream);
            while (in.hasNextLine()) {
                response.append(in.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading response");
            return null;
        }
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray artists = (JSONArray) jsonObject.get("artists");
            if (artists == null) {
                System.out.println("Ops...No matched artist in AudioDB database.");
                return null;
            } else {
                JSONObject requestedArtist = (JSONObject) artists.get(0);
                String AudioDB_ID = (String) requestedArtist.get("idArtist");
                newArtist.setAudioDB_ID(AudioDB_ID);
                String mood = (String) requestedArtist.get("strMood");
                newArtist.setMood(mood);
                String artistStyle = (String) requestedArtist.get("strStyle");
                newArtist.setStyle(artistStyle);
                String artistArea = (String) requestedArtist.get("strCountry");
                newArtist.setArtistArea(artistArea);
                library.addArtist(newArtist);
                System.out.println("Great! We have found that artist in AudioDB.");
                System.out.println("---ARTIST INFO---");
                System.out.format("Artist Name:%-40sAudioDB_ID:%-18sArtist Style:%-18sArtist Area:%-10s\n", newArtist.getName(), newArtist.getAudioDB_ID(),
                        newArtist.getStyle(), newArtist.getArtistArea());
                return newArtist;
            }
        } catch (ParseException e) {
            System.out.println("Error parsing JSON");
            return null;
        }
    }

    /**
     *
     * @param artist artist
     * @param releaseDate album release date
     * @param library music library
     * @return album / null
     *
     * This finds an album in the current library as well as AudioDB and automatically fill in missing details of that song based on the artist name and album release date.
     * This returns an album object if there is one that has been found, otherwise returns false.
     * PLEASE NOTE: If the album already exists in the library, then this also returns null.
     *
     */
    public Album insertAlbumFromAudioDB(Artist artist, String releaseDate, Library library) {
        for (Album a: library.getAlbums()) {
            if (a.getReleaseDate().substring(0, 4).equals(releaseDate) && a.getArtist().getName().equalsIgnoreCase(artist.getName())) {
                System.out.println("Great! We have found the album in the library.");
                System.out.println("---ALBUM INFO---");
                System.out.format("Album Name:%-40sAudioDB_ID:%-18sArtist Name:%-25sRelease Date:%-15sGenre:%-10s\n", a.getName(), a.getAudioDB_ID(),
                        artist.getName(), a.getReleaseDate(), a.getAlbumGenre());
                System.out.println("Is this album the one that you are looking for? (Y/N)");
                if(userPrompt.promptUserForYesOrNo().equalsIgnoreCase("y")) {
                    library.setAlbumAlreadyInLibrary(true);
                    return null;
                }
                System.out.println("Okay! Then we will try to find the album for you in AudioDB.");
            }
        }
        String requestURL = "https://theaudiodb.com/api/v1/json/2/album.php?i=";
        StringBuilder response = new StringBuilder();
        int id;
        if (library.getArtists().size() == 0) {           // set the entityID for the first album to be 301
            id = 301;
        } else {
            id = 1 + library.getAlbums().get(library.getAlbums().size() - 1).getEntityID();  // set the entityID of this new album to be 1 greater the current last album in the library
        }
        Album newAlbum = new Album(id);
        newAlbum.setArtist(artist);
        ArrayList<Album> duplicatedAlbums = new ArrayList<>();
        URL u;
        try {
            u = new URL(requestURL + artist.getAudioDB_ID());
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
            return null;
        }
        try {
            URLConnection connection = u.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int code = httpConnection.getResponseCode();

            String message = httpConnection.getResponseMessage();
            System.out.println("Building connection to AudioDB successes: \"" + code + " " + message + "\"");
            if (!message.equalsIgnoreCase("ok")) {
                System.out.println("There is no album recorded for the artist in the AudioDB.");
            }
            if (code != HttpURLConnection.HTTP_OK) {
                return null;
            }
            InputStream inStream = connection.getInputStream();
            Scanner in = new Scanner(inStream);
            while (in.hasNextLine()) {
                response.append(in.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Error reading response");
            return null;
        }
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response.toString());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray albums = (JSONArray) jsonObject.get("album");
            if (albums == null) {
                System.out.println("Ops...This artist doesn't have any album recorded in AudioDB database.");
                return null;
            } else {
                int numberOfAlbums = albums.size();
                for (int i = 0; i < numberOfAlbums; i++) {
                    JSONObject requestedArtist = (JSONObject) albums.get(i);
                    String releaseYear = (String) requestedArtist.get("intYearReleased");
                    if (releaseYear.substring(0,4).equals(releaseDate)) {             // if the release year matches, then this album is possibly what we are looking for
                        Album tempAlbum = new Album();
                        tempAlbum.setReleaseDate(releaseYear);
                        String AudioDB_ID = (String) requestedArtist.get("idAlbum");
                        tempAlbum.setAudioDB_ID(AudioDB_ID);
                        String albumName = (String) requestedArtist.get("strAlbum");
                        tempAlbum.setName(albumName);
                        String albumGenre = (String) requestedArtist.get("strGenre");
                        tempAlbum.setAlbumGenre(albumGenre);
                        duplicatedAlbums.add(tempAlbum);
                        if (duplicatedAlbums.size() == 5) {              // show maximum 5 albums to user
                            break;
                        }
                    }
                }
                if (duplicatedAlbums.size() > 1) {
                    Album tempAlbum = this.selectAlbum(duplicatedAlbums, artist.getName());
                    if (tempAlbum != null) {
                        for (Album a: library.getAlbums()) {
                            if (a.getAudioDB_ID().equalsIgnoreCase(tempAlbum.getAudioDB_ID())) {
                                System.out.println("The album you just chosen is already in the library.");
                                library.setAlbumAlreadyInLibrary(true);
                                return null;
                            }
                        }
                        newAlbum.setName(tempAlbum.getName());
                        newAlbum.setAudioDB_ID(tempAlbum.getAudioDB_ID());
                        newAlbum.setReleaseDate(tempAlbum.getReleaseDate());
                        newAlbum.setAlbumGenre(tempAlbum.getAlbumGenre());
                        newAlbum.setArtist(artist);
                        artist.addAlbum(newAlbum);
                        library.addAlbum(newAlbum);
                        System.out.println("Great! Album is successfully chosen.");
                    } else {

                    }
                } else if (duplicatedAlbums.size() == 1){
                    for (Album a: library.getAlbums()) {
                        if (a.getAudioDB_ID().equalsIgnoreCase(duplicatedAlbums.get(0).getAudioDB_ID())) {
                            System.out.println("The album you just chosen is already in the library.");
                            library.setAlbumAlreadyInLibrary(true);
                            return null;
                        }
                    }
                    newAlbum.setName(duplicatedAlbums.get(0).getName());
                    newAlbum.setAudioDB_ID(duplicatedAlbums.get(0).getAudioDB_ID());
                    newAlbum.setReleaseDate(duplicatedAlbums.get(0).getReleaseDate());
                    newAlbum.setAlbumGenre(duplicatedAlbums.get(0).getAlbumGenre());
                    artist.addAlbum(newAlbum);
                    newAlbum.setArtist(artist);
                    library.addAlbum(newAlbum);
                    System.out.println("Great! We have found that album in AudioDB.");
                    System.out.println("---ALBUM INFO---");
                    System.out.format("#Album Name:%-40sAudioDB_ID:%-18sArtist Name:%-25sRelease Date:%-15sGenre:%-10s\n", newAlbum.getName(), newAlbum.getAudioDB_ID(),
                            artist.getName(), newAlbum.getReleaseDate(), newAlbum.getAlbumGenre());
                } else {
                    System.out.println("No matched album in the AudioDB.");
                    return null;
                }
                return newAlbum;
            }
        } catch (ParseException e) {
            System.out.println("Error parsing JSON");
            return null;
        }
    }

    /**
     *
     * @param duplicatedAlbums arrayList of albums
     * @param artistName artist name
     * @return album / null
     *
     * This shows at most 5 qualified albums found in AudioDB to users and let users decide which one they want to keep
     * This return an album object if users chose one, otherwise returns null.
     *
     */
    public Album selectAlbum(ArrayList<Album> duplicatedAlbums, String artistName) {
        Album tempAlbum= null;
        for (int i = 0; i < duplicatedAlbums.size(); i++) {
            System.out.format("#%-3dAlbum Name:%-40sAudioDB_ID:%-18sArtist Name:%-25sRelease Date:%-15sGenre:%-10s\n", i, duplicatedAlbums.get(i).getName(), duplicatedAlbums.get(i).getAudioDB_ID(),
                    artistName, duplicatedAlbums.get(i).getReleaseDate(), duplicatedAlbums.get(i).getAlbumGenre());
        }
        System.out.println("(" + duplicatedAlbums.size() + " searching results for qualified albums in total.)");
        System.out.println("Is there any album among those albums that you might want to import into the library? (Y/N)");
        userInput = input.nextLine();
        while (true) {
            if (userInput.toLowerCase(Locale.ROOT).equals("y")) {
                System.out.println("Great! Now please choose the album which is closest to the album that you are looking for." +
                        "\n(Input the exact number after # to import the chosen one:)");
                userInput = input.nextLine();
                while (!((userInput.equals("0")) || (userInput.equals("1")) || (userInput.equals("2")) || (userInput.equals("3")) || (userInput.equals("4")))) {
                    System.out.println("Invalid input. Please enter an integer from 0~4.");
                    userInput = input.nextLine();
                }
                switch (userInput) {
                    case "0":
                        tempAlbum = duplicatedAlbums.get(0);
                        break;
                    case "1":
                        tempAlbum = duplicatedAlbums.get(1);
                        break;
                    case "2":
                        tempAlbum = duplicatedAlbums.get(2);
                        break;
                    case "3":
                        tempAlbum = duplicatedAlbums.get(3);
                        break;
                    case "4":
                        tempAlbum = duplicatedAlbums.get(4);
                        break;
                    default:
                        System.out.println("Bad input.");
                        break;
                }
                return tempAlbum;
            } else if (userInput.toLowerCase(Locale.ROOT).equals("n")) {
                System.out.println("Gotcha!");
                break;
            } else {
                System.out.println("Invalid input. Please enter \"Y\" or \"N\".");
                userInput = input.nextLine();
            }
        }
        return null;
    }
}
