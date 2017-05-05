package com.pushkin;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Chat extends AppCompatActivity {

    CustomAdapter<String> mAdapter;
    ArrayList<String> data;
    EditText editText;
    String username = "Shmoney";

    static final String FILE_NAME = "messageDater.txt";

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
        if(returnable.equals(""))
        {
            System.out.println("Returnable is empty. Writing dummy data to file");
            writeToFile("to;Robert;Hey there John. Want to hang out?;Monday at 12:02PM.\n");
            //okay we wrote to the file, now call ourselves again, re-read the file...
            readFile();
            //and then the below return will occur.
        }
        else{
            System.out.print("Returnable is no longer empty. Returning");
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

    public void updateAdapter(){
        data = new ArrayList<String>();

        //Read the file with our data
        String readMessages = readFile();

        //Create the arrayList of that data
        String[] lines = readMessages.split("\\n");
        for (String i:lines){
            data.add(i);
        }

        //Create the adapter with this new info
        mAdapter = new CustomAdapter<String>(this, R.layout.activity_listview, R.id.listview, data);
        ListView listView = (ListView)findViewById(R.id.listview);
        listView.setAdapter(mAdapter);

        //Set the view of this adapter to the bottom
        listView.setSelection(listView.getCount() - 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

//        System.out.println("here");
//        Intent intent = new Intent(this, MainConversationView.class);
//        startActivity(intent);

        updateAdapter();
//
        editText = (EditText)findViewById(R.id.messagebox);
//
        final Button sendButton = (Button)findViewById(R.id.sendbutton);
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendMessage();
                //hide the keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sendButton.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

            }
        });

    }

    public void sendMessage() {
        String curFile = readFile();
        String[] barsArr = curFile.split("\\n");
        String bars = barsArr[barsArr.length - 1].split(";")[0];
        System.out.println("Bars is " + bars);
        if(bars.equals("to")){
            System.out.println("Storing as [from]");
            bars = "from";
        }
        else{
            System.out.println("Storing as [to]");
            bars = "to";
        }
        String s = bars + ";" + username + ";" + editText.getText() + ";" + getTime() + "\n";
        writeToFile(s); //write this message to file...
        updateAdapter();
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

            s = "to;Robert;Hey Noah, how is it going?;Monday, 12:52 PM";
            System.out.println("The thing is [" + s + "]");
            if(s.split(";")[0].equals("to"))
            {
                System.out.println("Inflating view to [to]");
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);


                // Lookup view for data population
                TextView username = (TextView) convertView.findViewById(R.id.username);
                TextView textMessage = (TextView) convertView.findViewById(R.id.textmessage);
                TextView timeStamp = (TextView) convertView.findViewById(R.id.timestamp);
                // Populate the data into the template view using the data object
                username.setText(s.split(";")[1]);
                textMessage.setText(s.split(";")[2]);
                timeStamp.setText(s.split(";")[3]);

                username.setTextColor(Color.RED);
                textMessage.setTextColor(Color.GREEN);
                timeStamp.setTextColor(Color.GRAY);

            }
            else{
                //from
                System.out.println("Inflating view to [from]");
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview_from, parent, false);

                // Lookup view for data population
                TextView username = (TextView) convertView.findViewById(R.id.username);
                TextView textMessage = (TextView) convertView.findViewById(R.id.textmessage);
                TextView timeStamp = (TextView) convertView.findViewById(R.id.timestamp);
                // Populate the data into the template view using the data object
                username.setText(s.split(";")[1]);
                textMessage.setText(s.split(";")[2]);
                timeStamp.setText(s.split(";")[3]);

                username.setTextColor(Color.BLUE);
                textMessage.setTextColor(Color.BLACK);
                timeStamp.setTextColor(Color.GRAY);
            }
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