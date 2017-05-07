package com.pushkin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        ImageView i = (ImageView)findViewById(R.id.imageView);
        new ImageLoadTask("https://static.pexels.com/photos/2324/skyline-buildings-new-york-skyscrapers.jpg", i).execute();

        TextView messagesSent = (TextView) findViewById(R.id.messagesSent);
        TextView messagesReceived = (TextView) findViewById(R.id.messagesReceived);
        TextView kintactsNumber = (TextView) findViewById(R.id.kintactsNumber);
        TextView nickname = (TextView) findViewById(R.id.displayNickname);

        EditText newName=(EditText) findViewById(R.id.editNickname);
        EditText oldPassword=(EditText) findViewById(R.id.oldPassword);
        EditText newPassword=(EditText) findViewById(R.id.newPassword);
        Button saveName=(Button) findViewById(R.id.buttonName);
        Button savePassword=(Button) findViewById(R.id.buttonPassword);

        saveName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //fill in
            }
        });

        savePassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //fill in
            }
        });

        //dummies; get actual values from database
        int sentNum = 2958;
        int receivedNum = 3845;
        int kintactsNum = 143;
        String s= "Alex";

        messagesSent.setText("Messages Sent: " + sentNum);
        messagesReceived.setText("Messages Received: " + receivedNum);
        kintactsNumber.setText("Number of Kintacts: " + kintactsNum);
        nickname.setText(s);
    }
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }
    }
}
