package com.pushkin;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.plus.model.people.Person;
import com.pushkin.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class NewMessage extends AppCompatActivity {

    private EditText searchTo;
    private KintactAdapter mAdapter;
    //private ArrayList<ConversationPreview> data;
    private ArrayList<Kintact> data;
    private ListView listView;

    private PushkinDatabaseHelper dbHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_new_message);

        dbHelper = MainActivity.dbHelper;

        searchTo = (EditText)findViewById(R.id.searchto);

        data = dbHelper.getKintacts();
        System.out.println(data.size());

        listView = (ListView)findViewById(R.id.list);

        mAdapter = new KintactAdapter(this, R.id.pic, data);

        listView.setAdapter(mAdapter);

        searchTo.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                mAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Kintact kintact = mAdapter.getItem(position);
                searchTo.setText(kintact.getfName() + " " + kintact.getlName());

            }
        });
    }
}