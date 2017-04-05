package com.pushkin;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import java.io.ByteArrayOutputStream;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.PrintWriter;


public class Chat extends AppCompatActivity {

    CustomAdapter<String> mAdapter;
    ArrayList<String> data;
    EditText editText;
    String username = "Shmoney";

    static final String FILE_NAME = "messageData.txt";

    public String readFile(){
        String returnable = "";
        try {
            FileInputStream fIn;
            fIn = openFileInput(FILE_NAME);
            int n;
            returnable = "";
            while((n = fIn.read()) != -1) {
                returnable = returnable + Character.toString((char)n);

            }
            fIn.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(returnable == "")
        {
            System.out.println("Returnable is empty. Writing dummy data to file");
            writeToFile("Robert;Hey there John. Want to hang out?;Monday at 12:02PM.\n");
            //okay we wrote to the file, now call ourselves again, re-read the file...
            readFile();
            //and then the below return will occur.
        }
        return returnable;
    }

    public void writeToFile(String messageContent)
    {
        //Important Note, this function should probably return a boolean and the
        //function that calls it should check if it is True or False, and act accordingly
        try {
            FileOutputStream fOut = openFileOutput(FILE_NAME, MODE_PRIVATE | MODE_APPEND);
            byte[] decodedData = messageContent.getBytes();
            fOut.write(decodedData);
            fOut.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        data = new ArrayList<String>();

        String readMessages = readFile();

        String[] lines = readMessages.split("\\n");
        for (String i:lines){
            System.out.println("Here you go " + i);
            data.add(i);}

        mAdapter = new CustomAdapter<String>(this, R.layout.activity_listview, R.id.listview, data);
        ListView listView = (ListView)findViewById(R.id.listview);
        listView.setAdapter(mAdapter);

        editText = (EditText)findViewById(R.id.messagebox);

        Button sendButton = (Button)findViewById(R.id.sendbutton);
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    public void sendMessage() {
        String s = username + ";" + editText.getText() + ";" + getTime() + "\n";
        writeToFile(s); //write this message to file...
        System.out.println("STORED LOCALLY");
    }

    private class CustomAdapter<E> extends ArrayAdapter<String> {

        public CustomAdapter(Context context, int resource, int id, ArrayList<String> data) {
            super(context, resource, id, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            String s = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);
            }
            // Lookup view for data population
            TextView username = (TextView) convertView.findViewById(R.id.username);
            TextView textMessage = (TextView) convertView.findViewById(R.id.textmessage);
            TextView timeStamp = (TextView) convertView.findViewById(R.id.timestamp);
            // Populate the data into the template view using the data object
            username.setText(s.split(";")[0]);
            textMessage.setText(s.split(";")[1]);
            timeStamp.setText(s.split(";")[2]);


            username.setTextColor(Color.RED);
            textMessage.setTextColor(Color.BLACK);
            timeStamp.setTextColor(Color.GRAY);

            // Return the completed view to render on screen
            return convertView;
        }
    }

    public String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }

}