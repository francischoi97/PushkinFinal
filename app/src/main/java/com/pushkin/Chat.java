package com.pushkin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;


public class Chat extends AppCompatActivity
        implements ColorPickerDialog.OnColorChangedListener {

    CustomAdapter<String> mAdapter;
    ArrayList<String> data;
    EditText editText;
    Button drawButton;
    String username = "Shmoney";
    DrawingView mv;

    private Bitmap back;
    private Bitmap front;

    private Paint       mPaint;
    private MaskFilter mEmboss;
    private MaskFilter  mBlur;

    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
    private static final int BLUR_MENU_ID = Menu.FIRST + 2;
    private static final int ERASE_MENU_ID = Menu.FIRST + 3;
    private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;
    private static final int Save = Menu.FIRST + 5;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    EditText input;

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

        editText = (EditText)findViewById(R.id.messagebox);

        drawButton = (Button)findViewById(R.id.drawbutton);
        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawScreen();
            }
        });

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

        mv = (DrawingView)findViewById(R.id.drawingview);
        back = mv.getDrawingCache();
        if (back == null) back = Bitmap.createBitmap(1,1,ARGB_8888);

        updateAdapter();
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
            //System.out.println(s);
            System.out.println("The thing is [" + s + "]");
            if(s.split(";")[0].equals("to"))
            {
                System.out.println("Inflating view to [to]");
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);


                // Lookup view for data population
                TextView username = (TextView) convertView.findViewById(R.id.username);
                TextView textMessage = (TextView) convertView.findViewById(R.id.textmessage);
                TextView timeStamp = (TextView) convertView.findViewById(R.id.timestamp);
                ImageView imageText = (ImageView) convertView.findViewById(R.id.imagetext);
                // Populate the data into the template view using the data object
//                username.setText(s.split(";")[1]);
//                textMessage.setText(s.split(";")[2]);
//                timeStamp.setText(s.split(";")[3]);

                username.setText("YO");
                textMessage.setText("YOYO");
                timeStamp.setText("YOYOYO");

                front = textAsBitmap(textMessage.getText().toString(), 10, 4);
                Bitmap combo = combineImages(back,front);
                imageText.setImageBitmap(combo);

                username.setTextColor(Color.RED);
                textMessage.setTextColor(Color.GREEN);
                timeStamp.setTextColor(Color.GRAY);

            }
            else{
                //from
                System.out.println("Inflating view to [from]");
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);

                // Lookup view for data population
                TextView username = (TextView) convertView.findViewById(R.id.username);
                TextView textMessage = (TextView) convertView.findViewById(R.id.textmessage);
                TextView timeStamp = (TextView) convertView.findViewById(R.id.timestamp);
                ImageView imageText = (ImageView) convertView.findViewById(R.id.imagetext);
                // Populate the data into the template view using the data object
                System.out.println("String:" + s);

//                username.setText(s.split(";")[1]);
//                textMessage.setText(s.split(";")[2]);
//                timeStamp.setText(s.split(";")[3]);

                username.setText("YO");
                textMessage.setText("YOYO");
                timeStamp.setText("YOYOYO");

                front = textAsBitmap(textMessage.getText().toString(), 10, 4);
                Bitmap combo = combineImages(back,front);
                imageText.setImageBitmap(combo);

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

    public Bitmap combineImages(Bitmap background, Bitmap foreground) {

        int width = 0, height = 0;
        Bitmap cs;

        width = getWindowManager().getDefaultDisplay().getWidth();
        height = getWindowManager().getDefaultDisplay().getHeight();

        cs = Bitmap.createBitmap(100, 100, ARGB_8888);
        Canvas comboImage = new Canvas(cs);
        //background = Bitmap.createScaledBitmap(background, width, height, true);
        comboImage.drawBitmap(background, 0, 0, null);
        comboImage.drawBitmap(foreground, 0, 0, null);

        return cs;
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);

        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);

        Bitmap image;
        if (width < 1 || height < 1)
            image = Bitmap.createBitmap(1,1,ARGB_8888);
        else
            image = Bitmap.createBitmap(width, height, ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    public void openDrawScreen() {

    }

    public void colorChanged(int color) {
        mv.setColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.draw_menu, menu);

        menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
        menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
        menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
        menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
        menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');
        menu.add(0, Save, 0, "Save").setShortcut('5', 'z');

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mv.prep();

        switch (item.getItemId()) {
            case COLOR_MENU_ID:
                new ColorPickerDialog(this, this, mv.getmPaint().getColor()).show();
                return true;
            case EMBOSS_MENU_ID:
                if (mv.getmPaint().getMaskFilter() != mv.getmEmboss()) {
                    mv.setEmboss();
                } else {
                    mv.setEmbossNull();
                }
                return true;
            case BLUR_MENU_ID:
                if (mv.getmPaint().getMaskFilter() != mv.getmBlur()) {
                    mv.setBlur();
                } else {
                    mv.setBlurNull();
                }
                return true;
            case ERASE_MENU_ID:
                mv.erase();
                return true;
            case SRCATOP_MENU_ID:
                mv.srcATop();
                return true;
            case Save:
                AlertDialog.Builder editalert = new AlertDialog.Builder(Chat.this);
                editalert.setTitle("Please Enter the name with which you want to Save");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.FILL_PARENT);
                input = new EditText(Chat.this);
                input.setLayoutParams(lp);
                editalert.setView(input);
                editalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        requestPermission();
                        Bitmap bitmap = mv.getDrawingCache();
                        back = bitmap;
                        if (back == null) back = Bitmap.createBitmap(1,1,ARGB_8888);
                    }
                });
                editalert.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    String name= input.getText().toString();
                    Bitmap bitmap = mv.getDrawingCache();
                    back = bitmap;
                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, name, null);

                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}