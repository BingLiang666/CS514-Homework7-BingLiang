package edu.usfca.cs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {
    Entity e1, e2;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        e1 = new Entity();
        e2 = new Entity();

    }

    @org.junit.jupiter.api.Test
    void testID() {
        System.out.println(e1.entityID + " " + e1.getDateCreated());
        System.out.println(e2.entityID);
    }


}