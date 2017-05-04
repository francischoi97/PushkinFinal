package com.pushkin;


import android.app.IntentService;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegistrationService extends IntentService {

    public static final String TAG = "Registration Service";

    public RegistrationService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID myID = InstanceID.getInstance(this);
        try {
            String registrationToken = myID.getToken(
                    getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null
            );
            //Send to server
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpURLConnection harpoon;
            String url = "http://148.85.240.18:8000/storepic";
            String result = null;
            String thing = registrationToken;

            try {
                //Connect
                harpoon = (HttpURLConnection) ((new
                        URL(url).openConnection()));
                harpoon.setDoOutput(true);
                harpoon.setRequestProperty("Content-Type",
                        "application/json");
                harpoon.setRequestProperty("Accept", "application/json");
                harpoon.setRequestMethod("POST");
                harpoon.connect();

                //Write
                OutputStream os = harpoon.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new
                        OutputStreamWriter(os, "UTF-8"));
                writer.write(thing);
                writer.close();
                os.close();

                //Read
                BufferedReader br = new BufferedReader(new
                        InputStreamReader(harpoon.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                result = sb.toString();
                System.out.println(result);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, registrationToken);
//            GcmPubSub subscription = GcmPubSub.getInstance(this);
//            subscription.subscribe(registrationToken, "/topics/my_little_topic", null);
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }
}