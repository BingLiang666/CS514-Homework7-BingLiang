package edu.usfca.cs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {
    Library myLibrary;
    Song song1, song2;

    @BeforeEach
    void setUp() {
        myLibrary = new Library();
        song1 = new Song("Happy Birthday");
        song2 = new Song("Helter Skelter");
    }

    @org.junit.jupiter.api.Test
    void addSong() {
        myLibrary.addSong(song1);
        assertTrue(myLibrary.getSongs().contains(song1));
    }

    @org.junit.jupiter.api.Test
    void findSong() {
        myLibrary.getSongs().add(song1);
        assertTrue(myLibrary.findSong(song1));
        assertFalse(myLibrary.findSong(song2));
    }

}