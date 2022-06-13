package com.example.demopredictis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BarChartActivity extends AppCompatActivity {

    ArrayList<Integer>pulse = new ArrayList<>();
    Toolbar toolbar;
    ImageView backBtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        toolbar = findViewById(R.id.custom_tool);
        backBtn = (ImageView) toolbar.findViewById(R.id.backBtn);
        ArrayList<BarEntry> visitors = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        //Firebase realtime database to arraylist test v_1.0
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Thalach").child(userID).child("Value");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pulse.clear();
                visitors.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    pulse.add(Integer.valueOf(String.valueOf(snapshot.getValue())));
                }
                for (int counter = 0; counter < pulse.size(); counter++){
                    //Log.d("TAG", String.valueOf(pulse.get(counter)));
                    visitors.add(new BarEntry(counter,pulse.get(counter)));
                }
                //visitors.add(new BarEntry(2014,20));
                //visitors.add(new BarEntry(2015,15));
                //visitors.add(new BarEntry(2016,14));
                BarChart barChart = findViewById(R.id.barChart);
                BarDataSet barDataSet= new BarDataSet(visitors, "Maximum Pulse Rate");
                barDataSet.setColors(ContextCompat.getColor(barChart.getContext(),R.color.graph_color),
                                     ContextCompat.getColor(barChart.getContext(),R.color.Light_pink));
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);

                BarData barData = new BarData(barDataSet);
                barChart.setFitBars(true);
                barChart.setData(barData);
                //barChart.getDescription().setText("Pulse Rate Graph");
                barChart.animateY(200);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}