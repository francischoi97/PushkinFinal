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
    private int chatID;

    public ConversationPreview (String fname, String lname, String lastActive, String imageURL, float points, int chatID) {
        name = fname + " " + lname;
        this.lastActive = lastActive;
        this.imageURL = imageURL;
        this.points = points;
        this.chatID = chatID;
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

    public int getChatID() {
        return chatID;
    }

    public String toString() {
        return name + " " + lastActive + " " + imageURL + " " + points;
    }
}
