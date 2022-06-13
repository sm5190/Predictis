package com.example.demopredictis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MedicalConsultation extends AppCompatActivity {
    FirebaseFirestore fStore;
    FirebaseAuth firebaseAuth;
    TextView avoid,intake,exercise,sleep;
    Toolbar toolbar;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_consultation);
        fStore = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.include);
        backBtn = (ImageView) toolbar.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    String riskzone = document.getString("Prediction");
                    getConsultation(riskzone);
                }
            }
        });
    }
    public void getConsultation(String riskzone) {
        avoid = (TextView) findViewById(R.id.bpText);
        intake = (TextView) findViewById(R.id.bpText3);
        exercise = (TextView) findViewById(R.id.bpText4);
        sleep = (TextView) findViewById(R.id.bpText5);
        if (!riskzone.equals("no")){
            DocumentReference documentReference = fStore.collection("consultation").document(riskzone);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        String avoidConsultation1 = document.getString("avoid1");
                        String avoidConsultation2 = document.getString("avoid2");
                        String avoidConsultation3 = document.getString("avoid3");
                        String avoidConsultation = avoidConsultation1+"\n"+avoidConsultation2+"\n"+avoidConsultation3;
                        avoid.setText("AVOID:\n"+avoidConsultation);
                        String intakeConsultation1 = document.getString("intake1");
                        String intakeConsultation2 = document.getString("intake2");
                        String intakeConsultation3 = document.getString("intake3");
                        String intakeConsultation4 = document.getString("intake4");
                        String intakeConsultation = intakeConsultation1+"\n"+intakeConsultation2+"\n"+intakeConsultation3+"\n"+intakeConsultation4;
                        intake.setText("INTAKE:\n"+intakeConsultation);
                        String sleepConsultation = document.getString("sleep");
                        sleep.setText("SLEEP:\n"+sleepConsultation);
                        String exerciseConsultation = document.getString("exercise");
                        exercise.setText("EXCERCISE:\n"+exerciseConsultation);
                    }
                }
            });
        }
    }
}