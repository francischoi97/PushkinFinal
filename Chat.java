package com.pushkin;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pushkin.activity.MainActivity;
import com.pushkin.other.MessageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Chat extends AppCompatActivity {

    CustomAdapter<MessageView> mAdapter;
    ArrayList<String> data;
    EditText editText;
    Message m;
    private String[] activityTitles;
    public static int navItemIndex = 0;

    public static PushkinDatabaseHelper dbHelper;
    int chatID;
    private Toolbar toolbar;
    private final String TAG = "Chat Activity";
    private final int REQUEST_IMAGE_CAPTURE = 1;

    private boolean photoTaken;

    public void updateAdapter(){
//
        //get Messages
        ArrayList<MessageView> messages = dbHelper.getMessages(chatID);

        //Create the adapter with this new info
        mAdapter = new CustomAdapter<MessageView>(this, R.layout.activity_listview, R.id.listview, messages);
        ListView listView = (ListView)findViewById(R.id.listview);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setAdapter(mAdapter);

        //Set the view of this adapter to the bottom
        listView.setSelection(listView.getCount() - 1);
    }

    private void setToolbarTitle(String chatName) {
        getSupportActionBar().setTitle(chatName);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(Chat.this, MainActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //Noah: SEND imageBitmap TO SERVER HERE

            photoTaken = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        dbHelper = MainActivity.dbHelper;
        chatID = getIntent().getIntExtra("CHAT_ID",0);
        String firstName = dbHelper.getFirstName(chatID);
        String lastName = dbHelper.getLastName(chatID);
        //

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle(firstName + " " + lastName);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        photoTaken = false;

        //System.out.println (chatID);
        //System.out.println("if chatID is 0, then error!!");
//        System.out.println("here");
//        Intent intent = new Intent(this, MainConversationView.class);
//        startActivity(intent);

        final Handler handler=new Handler();
        handler.post(new Runnable(){
            @Override
            public void run() {
                // upadte textView here
                updateAdapter();
                handler.postDelayed(this,500); // set time here to refresh textView
            }
        });

        final Button cameraButton = (Button)findViewById(R.id.camerabutton);
        cameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        editText = (EditText)findViewById(R.id.messagebox);
//
        final Button sendButton = (Button)findViewById(R.id.sendbutton);
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //hide the keyboard
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(sendButton.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                sendMessage();

            }
        });

    }

    private class CustomAdapter<E> extends ArrayAdapter<MessageView> {

        public CustomAdapter(Context context, int resource, int id, ArrayList<MessageView> data) {
            super(context, resource, id, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            MessageView s = getItem(position);

            String sender = s.getSender();
            String text = s.getText();
            String time = s.getTime();
            String firstName = dbHelper.getFirstName(chatID);
            String lastName = dbHelper.getFirstName(chatID);
            //Noah: ASSIGN PHOTO FROM SERVER HERE

            if(!sender.equals(getLocalUser()))
            {
                //System.out.println("Inflating view to [to]");
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);

                // Lookup view for data population
                TextView username = (TextView) convertView.findViewById(R.id.username);
                TextView textMessage = (TextView) convertView.findViewById(R.id.textmessage);
                TextView timeStamp = (TextView) convertView.findViewById(R.id.timestamp);
                ImageView photoView = (ImageView) convertView.findViewById(R.id.photoview);
                // Populate the data into the template view using the data object
                username.setText(firstName);
                textMessage.setText(text);
                timeStamp.setText(time);

//                Noah: uncomment below code
//                if (photoFromServer != null) {
//                    photoView.setVisibility(View.VISIBLE);
//                    photoView.setImageBitmap(photoFromServer);
//                }

                username.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                username.setTypeface(null, Typeface.BOLD);
                textMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                timeStamp.setTextSize(11);
            }
            else{
                //from
                //System.out.println("Inflating view to [from]");
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview_from, parent, false);
                convertView.setBackgroundColor(Color.parseColor("#B3E5FC"));
                convertView.getBackground().setAlpha(116);
                // Lookup view for data population

                TextView username = (TextView) convertView.findViewById(R.id.username);
                TextView textMessage = (TextView) convertView.findViewById(R.id.textmessage);
                TextView timeStamp = (TextView) convertView.findViewById(R.id.timestamp);
                // Populate the data into the template view using the data object
                username.setText(firstName);
                textMessage.setText(text);
                timeStamp.setText(time);


                username.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                username.setTypeface(null, Typeface.BOLD);

                textMessage.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                timeStamp.setTextSize(11);

                if (photoTaken) {
                    textMessage.setVisibility(View.INVISIBLE);
                    ImageView photoView = (ImageView)convertView.findViewById(R.id.photoview);

                    //Noah: ASSIGN PHOTO TO mImageView HERE

                    //setting image position
                    photoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT));

                    photoView.setVisibility(View.VISIBLE);

                    photoTaken = false;
                }
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

    public void sendMessage() {

        String message = editText.getText().toString();
        String sender = getLocalUser();
        String time = getTime();
        m = new Message(sender,time,message);

        editText.setText("");

        sendtoServer(m);
    }

    public void sendtoServer(Message message) {
        new PushkinAsyncTask().execute(message);
    }

    public String readUserTokenFile(){
        String authorizationToken = "";
        try {
            FileInputStream fIn;
            fIn = openFileInput("userToken.dat");
            int n;
            while((n = fIn.read()) != -1) {
                authorizationToken = authorizationToken + Character.toString((char)n);

            }
            fIn.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //return authorizationToken
        return "noah:WQ1I5TdkOTUyOGQyMWE1OTk1OTMzYTUwYTFjMGMxMzIxZjZhNTNkYzUyZjA=";
    }

    public String getLocalUser(){
        String token = readUserTokenFile();
        return token.split(":")[0];
    }

    public String getLocalAuthToken(){
        String token = readUserTokenFile();
        return token.split(":")[1];
    }

    public class PushkinAsyncTask extends AsyncTask<Message, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Message... params) {
            String localUser = getLocalUser();
            String token = getLocalAuthToken();
            Message m = params[0];
            String message = params[0].getText();

            String recipient = dbHelper.getConversant(chatID);


            //encode the Base64.encodeBase64 expects byte[]. eMsg is a byte[] that contains the encrypted message

            byte[] b64Msg = Base64.encode(message.getBytes(), Base64.NO_WRAP);
            HttpURLConnection harpoon;
            String url = "http://148.85.240.18:8080/sendMsg";
            String result = null;
            String thing = "{\"recipient\":\"" + recipient + "\",\"message\":\"" + new String (b64Msg) + "\",\"authorization\":\"" + getLocalUser() + ":" + getLocalAuthToken() + "\"}";

            try {
                //Connect
                harpoon = (HttpURLConnection) ((new URL(url).openConnection()));
                harpoon.setDoOutput(true);
                harpoon.setRequestProperty("Content-Type", "application/json");
                harpoon.setRequestProperty("Accept", "application/json");
                harpoon.setRequestMethod("POST");
                harpoon.connect();

                //Write
                OutputStream os = harpoon.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(thing);
                writer.close();
                os.close();

                //Read
                BufferedReader br = new BufferedReader(new InputStreamReader(harpoon.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                result = sb.toString();
                JSONObject json = new JSONObject(result);
                String success = json.getString("Success");
                if (success.equals("0")) {
                    //we have failed to login, throw bad login.
                    //System.out.println(result);
                    //System.out.println(success);
                    return false;
                }
                //else, we are good, get token and store it...
                String response = json.getString("Message");
                //System.out.println(response);
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                dbHelper.addSentMessage(m, chatID);
            }
            updateAdapter();
        }
    }
}
