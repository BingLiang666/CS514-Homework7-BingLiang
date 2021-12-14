package edu.usfca.cs;

import java.util.Date;

public class Entity {
    protected String name;
    protected static int counter = 0;
    protected int entityID;
    protected Date dateCreated;

    public Entity() {
        this.name = "";
        counter++;
        this.entityID = counter;
        dateCreated = new Date();
    }

    public Entity(int ID) { entityID = ID; }

    public Entity(int ID, String name) {
        entityID = ID;
        this.name = name;}

    public boolean equals(Entity otherEntity) {
        return entityID == otherEntity.entityID;
    }


    public Entity(String name) {
        this.name = name;
        counter++;
        this.entityID = counter;
        dateCreated = new Date();
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    protected void setEntityID(int entityID) {this.entityID = entityID;}

    protected int getEntityID() {return entityID;}

    public String toString() {
        return "Name: " + this.name + " Entity ID: " + this.entityID;
    }
    public String toHTML() {
        return "<b>" + this.name + "</b><i> " + this.entityID + "</i>";
    }
    public String toXML() {
        return "<entity><name>" + this.name + "</name><ID> " + this.entityID + "</ID></entity>";
    }

    public String toJSON() { return "{" + "\"id\": \"" + this.getEntityID() + "\"," + "\"name\": \"" + this.getName() + "\"}"; }
}
