package edu.usfca.cs;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Library {
    private ArrayList<Song> songs;
    private ArrayList<Artist> artists;
    private ArrayList<Album> albums;


    /**
     * This is a constructor.
     */
    public Library() {
        songs = new ArrayList<>();
        artists = new ArrayList<>();
        albums = new ArrayList<>();
    }

    /**
     *
     * @param songs an arrayList of songs
     * @param artists an arrayList of artists
     * @param albums an arrayList of albums
     *
     * This is a constructor.
     *
     */
    public Library(ArrayList<Song> songs, ArrayList<Artist> artists, ArrayList<Album> albums) {
        this.songs = songs;
        this.artists = artists;
        this.albums = albums;
    }

    /**
     *
     * @param s song needs to be checked
     * @return
     *
     * This returns true if the song is in the library.
     *
     */
    public boolean findSong(Song s) {
        return songs.contains(s);
    }

    /**
     *
     * @return
     *
     * This returns an arrayList of songs in the library
     *
     */
    public ArrayList<Song> getSongs() {
        return songs;
    }

    /**
     *
     * @return
     *
     * This returns an arrayList of artists in the library
     *
     */
    public ArrayList<Artist> getArtists() {
        return artists;
    }

    /**
     *
     * @return
     *
     * This returns an arrayList of albums in the library
     *
     */
    public ArrayList<Album> getAlbums() {
        return albums;
    }

    /**
     *
     * @param song song needs to be added into library
     *
     * This adds song into library
     *
     */
    public void addSong(Song song) {
        songs.add(song);
    }

    /**
     *
     * @param artist artist needs to be added into library
     *
     * This adds artist into library
     *
     */
    public void addArtist(Artist artist) {
        artists.add(artist);
    }

    /**
     *
     * @param album album needs to be added into library
     *
     * This adds album into library
     *
     */
    public void addAlbum(Album album) {
        albums.add(album);
    }

    /**
     *
     * @param song new song needs to be checked
     * @return
     *
     * This returns a string "noDuplicatedSong" if there is no duplicated song of the new song in the library
     * This returns a string "importNewSong" if user wants to import that song even thought it is duplicated
     * This returns null if this new song has duplicated song in the library and user doesn't want to import it
     *
     */
    public String findDuplicates(Song song) {
        Scanner input = new Scanner(System.in);
        String deleteOrNot;
        Iterator<Song> songsIterator = songs.iterator();
        while (songsIterator.hasNext()) {
            Song s = songsIterator.next();
            if (s.definitelyEquals(song)) {
                System.out.println("We found one song in the current library that is definitely same with the new song that you want us to import." +
                        "\nDo you still want to import it anyway?\nPlease note: we will NOT import the new song if you choose \"N\".");
                deleteOrNot = input.nextLine();
                while (!(deleteOrNot.equalsIgnoreCase("y") || deleteOrNot.equalsIgnoreCase("n"))) {
                    System.out.println("Invalid answer. Please enter \"Y\" or \"N\"");
                    deleteOrNot = input.nextLine();
                }
                if (deleteOrNot.equalsIgnoreCase("y")) {
                    System.out.println("Good! We will keep both songs in the library.");
                    return "importNewSong";
                } else {
                    System.out.println("Good! We will not import the new song.");
                    return null;
                }
            } else if (s.possiblyEquals(song)) {
                System.out.println("We found one song in the library that is possibly a duplicate of the new song." +
                        "\n*Info* Song Name: " + s.name + "      Artist: " + s.performer.getName() +
                        "      Album: " + s.album.getName() +
                        "\nDo you want to keep both?(Y/N) \nPlease note: we will NOT import the new song if you choose \"N\".");
                deleteOrNot = input.nextLine();
                while (!(deleteOrNot.equalsIgnoreCase("y") || deleteOrNot.equalsIgnoreCase("n"))) {
                    System.out.println("Invalid answer. Please enter \"Y\" or \"N\"");
                    deleteOrNot = input.nextLine();
                }
                if (deleteOrNot.equalsIgnoreCase("y")) {
                    System.out.println("Good! We will keep both songs in the library.");
                    return "importNewSong";
                } else {
                    System.out.println("Good! We will not import the new song.");
                    return null;
                }
            }
        }
        return "noDuplicatedSong";
    }

    public static String getContent(Node n) {
        StringBuilder sb = new StringBuilder();
        Node child = n.getFirstChild();
        sb.append(child.getNodeValue());
        return sb.toString();
    }

    public void readInMusicFromXML(String filename) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(filename));

            System.out.printf("Version = %s\n", doc.getXmlVersion());
            Element root = doc.getDocumentElement();
            System.out.println(root);

            NodeList songNodes = root.getElementsByTagName("song");
            NodeList artistNodes = root.getElementsByTagName("artist");
            NodeList albumNodes = root.getElementsByTagName("album");

            readInSongFromXML(songNodes);
            readInArtistFromXML(artistNodes);
            readInAlbumFromXML(albumNodes);

            fillInLibraryFromXML(songNodes);
        } catch (Exception e) {
            System.out.println("Parsing error:" + e);
        }
    }

    public void readInSongFromXML(NodeList songNodes) {
        Node currentNode, subNode;
        Song currentSong;
        String songName;
        int songID;
        System.out.println("*********Songs**********");
        for (int i = 0; i < songNodes.getLength(); i++) {
            currentNode = songNodes.item(i);
            NodeList children = currentNode.getChildNodes();
            Element eElement = (Element) currentNode;
            songID = Integer.valueOf(eElement.getAttributes().getNamedItem("id").getNodeValue());
            for (int j = 0; j < children.getLength(); j++) {
                subNode = children.item(j);
                currentSong = new Song(songID);
                if (subNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (subNode.getNodeName().equals("title")) {
                        songName = getContent(subNode).trim();
                        currentSong.setName(songName);
                        System.out.println("Song name: " + currentSong.getName());
                        System.out.println("ID: " + currentSong.getEntityID());
                        this.addSong(currentSong);
                    }
                }
            }
        }
    }

    public ArrayList readInArtistFromXML(NodeList artistNodes) {
        Node currentNode, subNode;
        Artist currentArtist;
        String artistName;
        int artistID;
        System.out.println("*********Artists**********");
        for (int i = 0; i < artistNodes.getLength(); i++) {
            currentNode = artistNodes.item(i);
            NodeList children = currentNode.getChildNodes();
            Element eElement = (Element) currentNode;
            artistID = Integer.valueOf(eElement.getAttributes().getNamedItem("id").getNodeValue());
            for (int j = 0; j < children.getLength(); j++) {
                subNode = children.item(j);
                currentArtist = new Artist(artistID);
                if (subNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (subNode.getNodeName().equals("name")) {
                        artistName = getContent(subNode).trim();
                        currentArtist.setName(artistName);
                        System.out.println("Artist name: " + currentArtist.getName());
                        System.out.println("ID: " + currentArtist.getEntityID());
                        artists.add(currentArtist);
                    }
                }
            }
        }
        return artists;
    }

    public ArrayList readInAlbumFromXML(NodeList albumNodes) {
        Node currentNode, subNode;
        Album currentAlbum;
        String albumName;
        int albumID;
        System.out.println("*********Albums**********");
        for (int i = 0; i < albumNodes.getLength(); i++) {
            currentNode = albumNodes.item(i);
            NodeList children = currentNode.getChildNodes();
            Element eElement = (Element) currentNode;
            albumID = Integer.valueOf(eElement.getAttributes().getNamedItem("id").getNodeValue());
            for (int j = 0; j < children.getLength(); j++) {
                subNode = children.item(j);
                currentAlbum = new Album(albumID);
                if (subNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (subNode.getNodeName().equals("title")) {
                        albumName = getContent(subNode).trim();
                        currentAlbum.setName(albumName);
                        System.out.println("Album name: " + currentAlbum.getName());
                        System.out.println("ID: " + currentAlbum.getEntityID());
                        albums.add(currentAlbum);
                    }
                }
            }
        }
        return albums;
    }

    public void fillInLibraryFromXML(NodeList songNodes) {
        Node currentNode, subNode;
        int artistID;
        int albumID;
        System.out.println("*********Library info**********");
        for (int i = 0; i < songNodes.getLength(); i++) {
            currentNode = songNodes.item(i);
            NodeList children = currentNode.getChildNodes();
            System.out.printf("#SONG# ID:%d  Name:%s \n", songs.get(i).getEntityID(), songs.get(i).getName());
            for (int j = 0; j < children.getLength(); j++) {
                subNode = children.item(j);
                if (subNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (subNode.getNodeName().contains("artist")) {
                        Element eElement = (Element) subNode;
                        artistID = Integer.valueOf(eElement.getAttributes().getNamedItem("id").getNodeValue());
                        for(Artist artist: artists) {
                            if (artist.getEntityID() == artistID) {
                                songs.get(i).setPerformer(artist);
                                System.out.printf("#ARTIST# ID:%d  Name:%s\n", songs.get(i).getPerformer().getEntityID(), songs.get(i).getPerformer().getName());
                                break;
                            }
                        }
                    } else if (subNode.getNodeName().contains("album")) {
                        Element eElement = (Element) subNode;
                        albumID = Integer.valueOf(eElement.getAttributes().getNamedItem("id").getNodeValue());
                        for(Album album: albums) {
                            if (album.getEntityID() == albumID) {
                                songs.get(i).setAlbum(album);
                                System.out.printf("#ALBUM# ID:%d  Name:%s\n", songs.get(i).getAlbum().getEntityID(), songs.get(i).getAlbum().getName());
                                break;
                            }
                        }
                    }
                }
            }
            System.out.println("------------------------------------");
        }
    }

    public void writingXMLForLibrary() {
        try {
            File inputFile = new File("MusicLibrary_TEST.xml");
            FileOutputStream is = new FileOutputStream(inputFile);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            w.write("<library>\n");
            w.write("<songs>\n");
            for (Song song: this.getSongs()) {
                w.write(song.toXML());
                w.write("\n");
            }
            w.write("</songs>\n");
            w.write("<artists>\n");
            for (Artist artist: this.getArtists()) {
                w.write(artist.toXML());
                w.write("\n");
            }
            w.write("</artists>\n");
            w.write("<albums>\n");
            for (Album album: this.getAlbums()) {
                w.write(album.toXML());
                w.write("\n");
            }
            w.write("</albums>\n");
            w.write("</library>\n");
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file MusicLibrary_TEST.xml");
        }
    }

    public void writingJSONForLibrary() {
        try {
            File inputFile = new File("MusicLibrary_TEST.json");
            FileOutputStream is = new FileOutputStream(inputFile);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            w.write("{\n");
            w.write("\"songs\":[\n");
            int comma = 1;
            for (Song song: this.getSongs()) {
                w.write(song.toJSON());
                if(comma < this.getSongs().size()) {
                    w.write(",");
                }
                w.write("\n");
                comma++;
            }
            w.write("]");
            w.write(",\n\"artists\":[\n");
            comma = 1;
            for (Artist artist: this.getArtists()) {
                w.write(artist.toJSON());
                if(comma < this.getArtists().size()) {
                    w.write(",");
                }
                w.write("\n");
                comma++;
            }
            w.write("]");
            w.write(",\n\"albums\":[\n");
            comma = 1;
            for (Album album: this.getAlbums()) {
                w.write(album.toJSON());
                if(comma < this.getAlbums().size()) {
                    w.write(",");
                }
                w.write("\n");
                comma++;
            }
            w.write("]");
            w.write("\n}");
            w.close();
        } catch (IOException e) {
            System.err.println("Problem writing to the file MusicLibrary_TEST.json");
        }
    }

    public void lookUpMusicBrainzForAliasesOfArtist(Artist artist) {

        String initialURL = "https://musicbrainz.org/ws/2/artist" + artist.getName() + "?query=beatles&fmt=xml";
        /* MusicBrainz gives each element in their DB a unique ID, called an MBID. We'll use this to fetch that. */

        /* now let's parse the XML.  */
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            URLConnection u = new URL(initialURL).openConnection();
            /* MusicBrainz asks to have a user agent string set. This way they can contact you if there's an
             * issue, and they won't block your IP. */
            u.setRequestProperty("User-Agent", "Application ExampleParser/1.0 (bliang11@dons.usfca.edu");

            Document doc = db.parse(u.getInputStream());
            /* let's get the artist-list node */
            NodeList artists = doc.getElementsByTagName("artist-list");
            /* let's assume that the one we want is first. */
            Node beatlesNode = artists.item(0).getFirstChild();
            Node beatlesIDNode = beatlesNode.getAttributes().getNamedItem("id");
            String id = beatlesIDNode.getNodeValue();
            System.out.println(id);

            /* Now let's use that ID to get info specifically about this artist. */

            String lookupURL = "https://musicbrainz.org/ws/2/artist/" + id + "?inc=aliases";
            // debug helper
            System.out.println();
            URLConnection u2 = new URL(lookupURL).openConnection();
            u2.setRequestProperty("User-Agent", "Application ExampleParser/1.0 (bliang11@dons.usfca.edu");

            db = dbf.newDocumentBuilder();
            doc = db.parse(u2.getInputStream());
            /* let's get all the aliases. */
            NodeList aliases = doc.getElementsByTagName("alias");
            for (int i = 0; i < aliases.getLength(); i++) {
                System.out.println(aliases.item(i).getFirstChild().getNodeValue());
            }

        } catch (Exception ex) {
            System.out.println("XML parsing error" + ex);
        }
    }
}
