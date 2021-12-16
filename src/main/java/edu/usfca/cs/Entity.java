package edu.usfca.cs;

import java.util.Date;

public class Entity {
    protected String name;
    protected int entityID;
    protected Date dateCreated;

    /**
     * This is a constructor.
     */
    public Entity() {
        this.name = "";
        this.entityID = -1;
        dateCreated = new Date();
    }

    /**
     *
     * @param ID entityID
     *
     * This is a constructor.
     *
     */
    public Entity(int ID) { entityID = ID; }

    /**
     *
     * @param ID entityID
     * @param name entity name
     *
     * This is a constructor.
     *
     */
    public Entity(int ID, String name) {
        entityID = ID;
        this.name = name;}

    /**
     *
     * @param otherEntity entity
     * @return bollean
     *
     * This returns true if the two entities are equal, otherwise returns false.
     *
     */
    public boolean equals(Entity otherEntity) {
        return entityID == otherEntity.entityID;
    }

    /**
     *
     * @param name entity name
     *
     * This is a constructor.
     *
     */
    public Entity(String name) {
        this.name = name;
        this.entityID = -1;
        dateCreated = new Date();
    }

    /**
     *
     * @return Date
     *
     * This returns the date when an entity is created.
     *
     */
    public Date getDateCreated() {
        return dateCreated;
    }


    /**
     *
     * @param dateCreated date when an entity is created
     *
     * This sets the created date for an entity.
     *
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     *
     * @return entity name
     *
     * This returns the entity name.
     *
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name entity name
     *
     * This sets the name for an entity.
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param entityID entityID
     *
     * This sets the entityID for an entity.
     *
     */
    protected void setEntityID(int entityID) {this.entityID = entityID;}


    /**
     *
     * @return entityID
     *
     * This returns the entityID for an entity.
     *
     */
    protected int getEntityID() {return entityID;}

    /**
     *
     * @return string containing entity details
     *
     * This returns some details of entity in string format.
     *
     */
    public String toString() {
        return "Name: " + this.name + " Entity ID: " + this.entityID;
    }

    /**
     *
     * @return string containing entity details in HTML format
     *
     * This returns some details of entity in string and HTML format.
     *
     */
    public String toHTML() {
        return "<b>" + this.name + "</b><i> " + this.entityID + "</i>";
    }

    /**
     *
     * @return string containing entity details in XML format
     *
     * This returns some details of entity in string and XML format.
     *
     */
    public String toXML() {
        return "<entity><name>" + this.name + "</name><ID> " + this.entityID + "</ID></entity>";
    }

    /**
     *
     * @return string containing entity details in XML format
     *
     * This returns some details of entity in string and JSON format.
     *
     */
    public String toJSON() { return "{" + "\"id\": \"" + this.getEntityID() + "\"," + "\"name\": \"" + this.getName() + "\"}"; }
}
