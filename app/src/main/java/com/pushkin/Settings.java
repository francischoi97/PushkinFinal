package com.pushkin;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {

    private String[] times={"12am-2am","2am-4am","4am-6am","6am-8am","8am-10am","10am-12pm","12pm-2pm","2pm-4pm","4pm-6pm","6pm-8pm","8pm-10pm","10pm-12am"};
    private int[] messagesbyTime={123,123,123,123,123,132,132,132,123,123,123,123};
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView header=(TextView)findViewById(R.id.header);
        TextView messagesSent=(TextView)findViewById(R.id.messagesSent);
        TextView messagesReceived=(TextView)findViewById(R.id.messagesReceived);
        TextView kintactsNumber=(TextView)findViewById(R.id.kintactsNumber);

        EditText name=(EditText)findViewById(R.id.editName);
        EditText username=(EditText)findViewById(R.id.editUsername);

        pieChart=(PieChart)findViewById(R.id.idPieChart);

        int sentNum=1958;
        int receivedNum=12345;
        int kintactsNum=1234;



        pieChart.setCenterText("Activity by time");
        pieChart.setRotationEnabled(true);
        pieChart.setTransparentCircleAlpha(0);
        addDataSet(pieChart);

        header.setText("Settings");
        messagesSent.setText("Messages Sent: "+sentNum);
        messagesReceived.setText("Messages Received: "+receivedNum);
        kintactsNumber.setText("Num ber of Kintacts: "+kintactsNum);

        name.setText("Joey");
        username.setText("manwhoami");
    }

    private void addDataSet(PieChart pieChart) {
        ArrayList<PieEntry> yEntrys=new ArrayList<>();
        ArrayList<String> xEntrys=new ArrayList<>();

        for(int i=0;i< messagesbyTime.length;i++){
            yEntrys.add(new PieEntry(messagesbyTime[i],i));
        }
        for(int i=0;i<times.length;i++){
            xEntrys.add(times[i]);
        }

        PieDataSet pieDataSet= new PieDataSet(yEntrys, "Interactions");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        ArrayList<Integer> colors= new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.CYAN);
        colors.add(Color.GRAY);
        colors.add(Color.GREEN);
        colors.add(Color.DKGRAY);
        colors.add(Color.MAGENTA);
        colors.add(Color.RED);
        colors.add(Color.WHITE);
        colors.add(Color.YELLOW);
        colors.add(Color.LTGRAY);
        colors.add(Color.GREEN);
        pieDataSet.setColors(colors);

        PieData pieData=new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
