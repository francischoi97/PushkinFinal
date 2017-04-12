package com.pushkin;

/**
 * Created by Harith on 04/08/17.
 */

public class Message {

    //username of sender
    private String username;
    private String timeOfMessage;
    private String text;

    public Message (String username, String timeOfMessage, String text) {
        this.username = username;
        this.timeOfMessage = timeOfMessage;
        this.text = text;
    }

    public String getUsername() {return username;}
    public String getTimeOfMessage() {return timeOfMessage;}
    public String getText() {return text;}

}
