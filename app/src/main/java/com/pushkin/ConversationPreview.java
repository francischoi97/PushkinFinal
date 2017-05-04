package com.pushkin;

/**
 * Created by Harith on 04/28/17.
 * This is a file that creates ConversationPreviews for the conversation listView
 */

public class ConversationPreview {

    private String name;
    private String lastActive;
    private String imageURL;
    private float points;

    public ConversationPreview (String fname, String lname, String lastActive, String imageURL, float points) {
        name = fname + " " + lname;
        this.lastActive = lastActive;
        this.imageURL = imageURL;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getLastActive() {
        return lastActive;
    }

    public float getPoints() {
        return points;
    }

    public String toString() {
        return name + " " + lastActive + " " + imageURL + " " + points;
    }
}
