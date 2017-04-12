package com.pushkin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Harith on 04/08/17.
 */

public class PushkinDatabaseHelper extends SQLiteOpenHelper{

    //database version and name
    private static int database_version = 1;
    private static final String database_name = "Pushkin Database";

    //static variables for Conversations table
    private static final String CONVERSATIONS = "Conversations";
    private static final String KEY_USERNAME = "Username";
    private static final String KEY_Chat_ID = "CHAT_ID";
    private static final String KEY_FIRST_NAME = "FirstName";
    private static final String KEY_LAST_NAME = "LastName";
    private static final String KEY_LAST_ACTIVE = "LastActive";
    private static final String KEY_MESSAGES_SENT = "MessagesSent";
    private static final String KEY_MESSAGES_RECEIVED = "MessagesReceived";
    private static final String KEY_TOTAL_POINTS = "TotalPoints";

    //variables for Chat table. This is a table that contains all messages
    private static final String CHAT = "Chat";
    private static final String KEY_TIME = "Time";
    private static final String KEY_TEXT = "Text";
    private static final String KEY_SENDER = "Sender";
    private static final String KEY_MESSAGE_NUMBER = "Message_Number";
    private static final String KEY_POINTS_EARNED_BY_THIS_MESSAGE = "Points_earned";

    //Conversations table name
    private static final String CONVERSATIONS_NAME = "create table Conversations (" + KEY_USERNAME + " text not null, " +
            KEY_Chat_ID + " int PRIMARY KEY AUTOINCREMENT DEFAULT 1, " + KEY_FIRST_NAME + " text not null, " +
            KEY_LAST_NAME + " text not null, " + KEY_LAST_ACTIVE + " text, " + KEY_MESSAGES_SENT + " int, " +
            KEY_MESSAGES_RECEIVED + " int, " + KEY_TOTAL_POINTS + " float )";

    //Chat table name
    private static final String CHAT_NAME = "create table "+ CHAT + " (" + KEY_TIME + " text not null, " + KEY_TEXT + " text not null, " +
            KEY_SENDER + " text not null, " + KEY_MESSAGE_NUMBER + " int PRIMARY KEY AUTOINCREMENT DEFAULT 1, " +
            KEY_POINTS_EARNED_BY_THIS_MESSAGE + " int DEFAULT 0 " + KEY_Chat_ID + " INTEGER not null)";

    public PushkinDatabaseHelper(Context context) {
        super(context, database_name, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CONVERSATIONS_NAME);
        db.execSQL(CHAT_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PushkinDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + CONVERSATIONS);
        db.execSQL(CONVERSATIONS_NAME);
        db.execSQL(CHAT_NAME);
    }

    //adding a conversation to the database FOR THE FIRST TIME!!!
    long addConversation (Conversation c) {
        //get database
        SQLiteDatabase db = this.getWritableDatabase();

        //prepare column value pairs
        //opening message always gets 1 point to recipient
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, c.getUsername());
        values.put(KEY_FIRST_NAME, c.getFirstName());
        values.put(KEY_LAST_NAME, c.getLastName());
        values.put(KEY_LAST_ACTIVE, c.getTimeOfFirstMessage());
        values.put(KEY_MESSAGES_SENT, 0);
        values.put(KEY_MESSAGES_RECEIVED, 1);
        values.put(KEY_TOTAL_POINTS, 1);

        //insert them into the table
        //and return the primary key, unique CHATid
        long toReturn = db.insert(CONVERSATIONS_NAME, null, values);


        //insert whatever the first message was into the CHAT Corresponding to the NEWLY Created table
        //first message of each chat always gets 1 point
        ContentValues chat = new ContentValues();
        chat.put(KEY_TIME, c.getTimeOfFirstMessage());
        chat.put(KEY_TEXT, c.getFirstMessage());
        chat.put(KEY_SENDER, c.getUsername());
        chat.put(KEY_POINTS_EARNED_BY_THIS_MESSAGE, 1);
        chat.put(KEY_Chat_ID, toReturn);
        db.insert(CHAT, null, chat);

        return toReturn;

    }

    //this method checks whether a conversation with a given Username already exists
    //if so, then just call addReceivedMessage(Message m) with your message
    //if not, then call addConversation(Conversation c) with a new conversation
    public boolean conversationExists (String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        //query the database to check whether a conversation with the given username exists
        Cursor c = db.rawQuery("SELECT Username from " + CONVERSATIONS + " WHERE Username = '" + username + "'", null);
        if (c.moveToFirst()) {
            if (c.getString(c.getColumnIndex("Username")).equals(username)) {
                return true;
            }
        }
        return false;
    }

    //adds a message to an existing chat and updates the conversations database
    void addReceivedMessage (Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor c = db.rawQuery("select " + KEY_Chat_ID + " from " + CONVERSATIONS + " where " +
                    KEY_USERNAME + " = ?", new String[] {message.getUsername()});
        int chatID = -1;
        if (c.moveToFirst()) {
            chatID = c.getInt(c.getColumnIndex(KEY_Chat_ID));
        }

        //FIGURE OUT WHAT TO DO WITH POINTS!!!

        //update Chat table
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, message.getTimeOfMessage());
        values.put(KEY_TEXT, message.getText());
        values.put(KEY_SENDER, message.getUsername());
        values.put(KEY_POINTS_EARNED_BY_THIS_MESSAGE, 0.5);
        values.put(KEY_Chat_ID, chatID);
        db.insert(CHAT, null, values);

        //update Conversations
        db.execSQL("UPDATE " + CONVERSATIONS + " SET " + KEY_LAST_ACTIVE + " = " + message.getTimeOfMessage() +
                    ", " + KEY_MESSAGES_RECEIVED + " = " + KEY_MESSAGES_RECEIVED + " +1, " + KEY_TOTAL_POINTS + " = " +
                    KEY_TOTAL_POINTS + " +0.5 WHERE Username = '" + message.getUsername() + "'");

    }

    void addSentMessage (Message message, String toUsername) {
        SQLiteDatabase db = this.getWritableDatabase();

        //update Chat table
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, message.getTimeOfMessage());
        values.put(KEY_TEXT, message.getText());
        values.put(KEY_SENDER, message.getUsername());
        values.put(KEY_POINTS_EARNED_BY_THIS_MESSAGE, 0.5);
        db.insert(toUsername, null, values);

        //Update Conversations
        db.execSQL("UPDATE " + CONVERSATIONS + " SET " + KEY_LAST_ACTIVE + " = " + message.getTimeOfMessage() +
                ", " + KEY_MESSAGES_SENT + " = " + KEY_MESSAGES_SENT + " +1, " + KEY_TOTAL_POINTS + " = " +
                KEY_TOTAL_POINTS + " +0.5 WHERE Username = '" + toUsername + "'");
    }

    //some method to compute the number of points a certain message gets you
    public static float computeNumberOfPoints () {
        return 0;
    }
}
