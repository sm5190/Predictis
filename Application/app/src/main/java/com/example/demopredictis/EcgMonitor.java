package com.example.demopredictis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EcgMonitor extends AppCompatActivity {

    LineChart lineChart;
    ArrayList<Entry> yAXESsin = new ArrayList<>();
    DatabaseReference reference;
    ValueEventListener valueEventListener;
    FirebaseAuth firebaseAuth;
    Toolbar toolbar;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_monitor);
        lineChart = (LineChart) findViewById(R.id.lineChart);
        toolbar = findViewById(R.id.custom_tool);
        backBtn = (ImageView) toolbar.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        ArrayList<String> xAXES = new ArrayList<>();
//
//        ArrayList<Entry> yAXEScos = new ArrayList<>();
//        ArrayList<String>op = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("ECG").child(userID).child("Value");
        reference.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                yAXESsin.clear();
                double x = 0.0 ;
                Float sin;
              //  show();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    x = x + 0.7;
                    float xEntry = Float.parseFloat(String.valueOf(x));
                    //pulse.add(Integer.valueOf(String.valueOf(snapshot.getValue())));
                    //op.add(String.valueOf(snapshot.getValue()));
                    sin = Float.parseFloat(snapshot.getValue().toString());
                    yAXESsin.add(new Entry(xEntry,sin));
                }
                //show(yAXESsin);
                LineDataSet lineDataSet = new LineDataSet(yAXESsin,"ECG Signal");
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawValues(false);
                lineDataSet.setColor(ContextCompat.getColor(lineChart.getContext(),R.color.graph_color));
                LineData data = new LineData(lineDataSet);
                lineChart.setData(data);
                lineChart.getAxisRight().setDrawGridLines(false);
                lineChart.getAxisLeft().setDrawGridLines(false);
                lineChart.getXAxis().setDrawGridLines(false);
                lineChart.notifyDataSetChanged();
                YAxis yAxis = lineChart.getAxisLeft();
                yAxis.setAxisMaximum(1000f);
                yAxis.setAxisMinimum(50f);
                XAxis xAxis = lineChart.getXAxis();

                lineChart.setVisibleXRangeMaximum(65f);
                lineChart.getDescription().setText("ECG Graph");
                lineChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
//    void show (){
//        Log.d("TAG",yAXESsin.toString());
//        yAXESsin.add(new Entry(1,280f));
//        yAXESsin.add(new Entry(2,200f));
//        yAXESsin.add(new Entry(3,300f));
//        yAXESsin.add(new Entry(4,360f));
//        yAXESsin.add(new Entry(5,180f));
//
//        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
//        Toast.makeText(EcgMonitor.this, "Hello", Toast.LENGTH_SHORT).show();
//        //Log.d("TAG",op.toString());
//
//        LineDataSet lineDataSet2 = new LineDataSet(yAXESsin,"sin");
//        lineDataSet2.setDrawCircles(false);
//        lineDataSet2.setColor(Color.RED);
//
//        lineDataSets.add(lineDataSet2);
//
//        lineChart.setData(new LineData(lineDataSets));
//
//        lineChart.setVisibleXRangeMaximum(65f);
//    }
}