package com.example.demopredictis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Form1 extends AppCompatActivity {
    Button logoutBtn,formStoreBtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    EditText inUsername,inAge,inHeight,inWeight,inOccupation,inLipid,inBloodSuger,inEmergencyContact;
    CheckBox checkboxInputYes, checkboxInputNo ,checkboxInputFamilyYes, checkboxInputFamilyNo ,getCheckboxInputGenderMale,getCheckboxInputGenderFemale,getCheckboxInputGenderOther;
    CheckBox getcheckBoxPain1,getcheckBoxPain2,getcheckBoxPain3,getcheckBoxPain4;
    CheckBox getExangYes,getExangNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form1);
        inUsername = findViewById(R.id.Username);
        inAge = findViewById(R.id.Age);
        inHeight = findViewById(R.id.Height);
        inWeight = findViewById(R.id.Weight);
        inOccupation = findViewById(R.id.Occupation);
        inLipid = findViewById(R.id.Lipid);
        inBloodSuger = findViewById(R.id.BloodSuger);
        inEmergencyContact = findViewById(R.id.EmergencyContact);
        logoutBtn = findViewById(R.id.logoutBtn);
        checkboxInputYes = (CheckBox) findViewById(R.id.checkboxInputYes);
        checkboxInputNo = (CheckBox) findViewById(R.id.checkboxInputNo);
        checkboxInputFamilyYes = (CheckBox) findViewById(R.id.checkboxInputFamilyYes);
        checkboxInputFamilyNo = (CheckBox) findViewById(R.id.checkboxInputFamilyNo);
        getCheckboxInputGenderMale = (CheckBox) findViewById(R.id.checkboxInputGenderMale);
        getCheckboxInputGenderFemale = (CheckBox) findViewById(R.id.checkboxInputGenderFemale);
        getCheckboxInputGenderOther = (CheckBox) findViewById(R.id.checkboxInputGenderOther);
        getcheckBoxPain1 = (CheckBox) findViewById(R.id.checkboxPain1);
        getcheckBoxPain2 = (CheckBox) findViewById(R.id.checkboxPain2);
        getcheckBoxPain3 = (CheckBox) findViewById(R.id.checkboxPain3);
        getcheckBoxPain4 = (CheckBox) findViewById(R.id.checkboxPain4);
        getExangYes = (CheckBox) findViewById(R.id.checkboxInputExangYes);
        getExangNo = (CheckBox) findViewById(R.id.checkboxInputExangNo);


        formStoreBtn = findViewById(R.id.formStoreBtn);
        formStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                String userID = firebaseAuth.getCurrentUser().getUid();


                String username = inUsername.getText().toString();
                Double age = Double.parseDouble(inAge.getText().toString());
                Double height = Double.parseDouble(inHeight.getText().toString());
                Double weight = Double.parseDouble(inWeight.getText().toString());
                Double lipid = Double.parseDouble(inLipid.getText().toString());
                Double bloodsuger = Double.parseDouble(inBloodSuger.getText().toString());
                int emergecy = Integer.parseInt(inEmergencyContact.getText().toString());
                boolean smoking = false;
                boolean familyHistory = false;
                int gender = 0;
                int suger = 0;
                int exang = 0;
                int cp = 0;

                if (checkboxInputYes.isChecked()) smoking = true;
                if (checkboxInputNo.isChecked()) smoking = false;
                if (checkboxInputFamilyYes.isChecked()) familyHistory = true;
                if (checkboxInputFamilyNo.isChecked()) familyHistory = false;
                if (getCheckboxInputGenderMale.isChecked()) gender = 1;
                if (getCheckboxInputGenderFemale.isChecked()) gender = 0;
                if (getCheckboxInputGenderOther.isChecked()) gender = 2;
                if (bloodsuger >= 6.7) suger = 1;
                if (bloodsuger < 6.7) suger = 0;
                if (getExangYes.isChecked()) exang = 1;
                if (getExangNo.isChecked()) exang = 0;
                if (getcheckBoxPain1.isChecked()) cp = 0;
                if (getcheckBoxPain2.isChecked()) cp = 1;
                if (getcheckBoxPain3.isChecked()) cp = 2;
                if (getcheckBoxPain4.isChecked()) cp = 3;


                String occupation = inOccupation.getText().toString();
                Map<String,Object> user = new HashMap<>();
                user.put("Username",username);
                user.put("Gender",gender);
                user.put("Age",age);
                user.put("Height",height);
                user.put("Weight",weight);
                user.put("Cholesterol",lipid);
                user.put("BloodSuger",suger);
                user.put("EmergencyContact",emergecy);
                user.put("Smoking",smoking);
                user.put("FamilyHistory",familyHistory);
                user.put("Verify",true);
                user.put("Exang",exang);
                user.put("ChestPain",cp);
                user.put("Prediction","no");
                fStore.collection("users").document(userID).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG2","data stored");
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG1","data not stored");
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        finish();
                    }
                });
            }

        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
    }
    public void onCheckBoxClickedSmoking(View view){
        switch (view.getId()) {
            case R.id.checkboxInputYes :
                checkboxInputNo.setChecked(false);
                break;
            case R.id.checkboxInputNo :
                checkboxInputYes.setChecked(false);
                break;
        }
    }
    public void onCheckBoxClickedFamily(View view){
        switch (view.getId()) {
            case R.id.checkboxInputFamilyYes :
                checkboxInputFamilyNo.setChecked(false);
                break;
            case R.id.checkboxInputFamilyNo :
                checkboxInputFamilyYes.setChecked(false);
                break;
        }
    }
    public void onCheckBoxClickedGender(View view){
        switch (view.getId()) {
            case R.id.checkboxInputGenderMale :
                getCheckboxInputGenderFemale.setChecked(false);
                getCheckboxInputGenderOther.setChecked(false);
                break;
            case R.id.checkboxInputGenderFemale :
                getCheckboxInputGenderMale.setChecked(false);
                getCheckboxInputGenderOther.setChecked(false);
                break;
            case R.id.checkboxInputGenderOther :
                getCheckboxInputGenderMale.setChecked(false);
                getCheckboxInputGenderFemale.setChecked(false);
                break;
        }
    }
    public void onCheckBoxClickedPain(View view){
        switch (view.getId()) {
            case R.id.checkboxPain1 :
                getcheckBoxPain2.setChecked(false);
                getcheckBoxPain3.setChecked(false);
                getcheckBoxPain4.setChecked(false);
                break;
            case R.id.checkboxPain2 :
                getcheckBoxPain1.setChecked(false);
                getcheckBoxPain3.setChecked(false);
                getcheckBoxPain4.setChecked(false);
                break;
            case R.id.checkboxPain3 :
                getcheckBoxPain1.setChecked(false);
                getcheckBoxPain2.setChecked(false);
                getcheckBoxPain4.setChecked(false);
                break;
            case R.id.checkboxPain4 :
                getcheckBoxPain1.setChecked(false);
                getcheckBoxPain2.setChecked(false);
                getcheckBoxPain3.setChecked(false);
                break;
        }
    }
    public void onCheckBoxExang(View view){
        switch (view.getId()) {
            case R.id.checkboxInputExangYes :
                getExangNo.setChecked(false);
                break;
            case R.id.checkboxInputExangNo :
                getExangYes.setChecked(false);
                break;
        }
    }
}