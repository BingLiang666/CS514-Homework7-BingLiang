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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class AudioDB {
    protected UserPrompt userPrompt = new UserPrompt();
    protected String userInput;
    protected Scanner input = new Scanner(System.in);

    public Artist insertArtistFromAudioDB(Statement statement, String artistName, Library library) {
        String requestURL = "https://www.theaudiodb.com/api/v1/json/2/search.php?s=";
        StringBuilder response = new StringBuilder();
        int id = 201 + library.getArtists().size();
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
            System.out.println(code + " " + message);
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
                //statement.executeUpdate(newArtist.toSQL());
                return newArtist;
            }
        } catch (ParseException e) {
            System.out.println("Error parsing JSON");
            return null;
        }
    }

    public Album insertAlbumFromAudioDB(Statement statement, Artist artist, String releaseDate, Library library) {
        String requestURL = "https://theaudiodb.com/api/v1/json/2/album.php?i=";
        StringBuilder response = new StringBuilder();
        int id = 301 + library.getAlbums().size();
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
            System.out.println(code + " " + message);
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
                    if (releaseYear.equals(releaseDate.substring(0,4))) {             // if the release year matches, then this album is possibly what we are looking for
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
                    Album tempAlbum = this.selectAlbum(duplicatedAlbums);
                    newAlbum.setAudioDB_ID(tempAlbum.getAudioDB_ID());
                    newAlbum.setReleaseDate(tempAlbum.getReleaseDate());
                    newAlbum.setAlbumGenre(tempAlbum.getAlbumGenre());
                    library.addAlbum(newAlbum);
                    //statement.executeUpdate(newAlbum.toSQL());
                } else {
                    newAlbum.setName(duplicatedAlbums.get(0).getName());
                    newAlbum.setAudioDB_ID(duplicatedAlbums.get(0).getAudioDB_ID());
                    newAlbum.setReleaseDate(duplicatedAlbums.get(0).getReleaseDate());
                    newAlbum.setAlbumGenre(duplicatedAlbums.get(0).getAlbumGenre());
                    library.addAlbum(newAlbum);
                    //statement.executeUpdate(newAlbum.toSQL());
                }
                return newAlbum;
            }
        } catch (ParseException e) {
            System.out.println("Error parsing JSON");
            return null;
        } /*catch (SQLException e) {
            System.out.println("SQLite updates failed");
            return null;
        } */
    }

    public Album selectAlbum(ArrayList<Album> duplicatedAlbums) {
        Album tempAlbum= null;
        for (int i = 0; i < duplicatedAlbums.size(); i++) {
            System.out.format("#%-3dSong Name:%-40sMusicBrainz_ID:%-40sArtist Name:%-25sRelease Date:%-15sGenre:%-10s\n", i, duplicatedAlbums.get(i).getName(), duplicatedAlbums.get(i).getAudioDB_ID(),
                    duplicatedAlbums.get(i).getArtist().getName(), duplicatedAlbums.get(i).getReleaseDate(), duplicatedAlbums.get(i).getAlbumGenre());
        }
        System.out.println("Is there any album among those albums that you might want to import into the library? (Enter Y/N)");
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
                System.out.println("Invalid input. Please enter again.");
                userInput = input.nextLine();
            }
        }
        return null;
    }
}
