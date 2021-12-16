package edu.usfca.cs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UserPromptTest {
    Playlist myPlaylist;
    Song s1, s2;
    UserPrompt userPrompt = new UserPrompt();

    @BeforeEach
    void setUp() {
        myPlaylist = new Playlist();
        s1 = new Song("Happy Birthday");
        s2 = new Song("Beauty");
    }

    @Test
    void promptUserForLikes() {
        String data = "5\r\n";
        InputStream stdin = System.in;
        try {
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            Scanner scanner = new Scanner(System.in);
            assertEquals(5, userPrompt.promptUserForLikes(s1, scanner));
        } finally {
            System.setIn(stdin);
        }
    }
}