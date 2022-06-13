package com.example.demopredictis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {
    Button updateBtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    EditText inUsername,inAge,inHeight,inWeight,inOccupation,inLipid,inBloodSuger,inEmergencyContact;
    CheckBox checkboxInputYes, checkboxInputNo ,checkboxInputFamilyYes, checkboxInputFamilyNo ,getCheckboxInputGenderMale,getCheckboxInputGenderFemale,getCheckboxInputGenderOther;
    Toolbar toolbar;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        toolbar = findViewById(R.id.include);
        backBtn = (ImageView) toolbar.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        inUsername = findViewById(R.id.Username);
        inWeight = findViewById(R.id.Weight);
        inLipid = findViewById(R.id.Lipid);
        inBloodSuger = findViewById(R.id.BloodSuger);
        inEmergencyContact = findViewById(R.id.EmergencyContact);
        checkboxInputYes = (CheckBox) findViewById(R.id.checkboxInputYes);
        checkboxInputNo = (CheckBox) findViewById(R.id.checkboxInputNo);

        String userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    String usernameS = document.getString("Username");
                    String weightS = document.getLong("Weight").toString();
                    String lipidS = document.getLong("Cholesterol").toString();
                    String bloodsugerS = document.getLong("BloodSuger").toString();
                    String emergencyS = document.getLong("EmergencyContact").toString();
                    boolean smoking;
                    inUsername.setHint(usernameS.toString());
                    inWeight.setHint(weightS);
                    inLipid.setHint(lipidS);
                    inBloodSuger.setHint(bloodsugerS);
                    inEmergencyContact.setHint("0"+emergencyS);
                    boolean smoke = document.getBoolean("Smoking");
                    if (smoke == true){
                        checkboxInputYes.setChecked(true);
                        checkboxInputNo.setChecked(false);
                    }
                    if (smoke == false) {
                        checkboxInputYes.setChecked(false);
                        checkboxInputNo.setChecked(true);
                    }

                    updateBtn = findViewById(R.id.updateBtn);
                    updateBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //firebaseAuth = FirebaseAuth.getInstance();
                            //fStore = FirebaseFirestore.getInstance();
                            //String userID = firebaseAuth.getCurrentUser().getUid();
                            String username = usernameS;
                            Double weight = Double.valueOf(weightS);
                            Double cholesterol = Double.valueOf(lipidS);
                            int bloodsuger = Integer.parseInt(bloodsugerS);
                            int emergency = Integer.parseInt(emergencyS);
                            if (!inUsername.getText().toString().equals("")) username = inUsername.getText().toString();
                            if (!inWeight.getText().toString().equals("")) weight = Double.valueOf(inWeight.getText().toString());
                            if (!inLipid.getText().toString().equals("")) cholesterol = Double.valueOf(inLipid.getText().toString());
                            if (!inBloodSuger.getText().toString().equals("")) bloodsuger = Integer.parseInt(inBloodSuger.getText().toString());
                            if (!inEmergencyContact.getText().toString().equals("")) emergency = Integer.parseInt(inEmergencyContact.getText().toString());
                            if (bloodsuger > 120) bloodsuger = 1;
                            if (bloodsuger <= 120) bloodsuger = 0;
                            boolean smoking = false;

                            if (checkboxInputYes.isChecked()) smoking = true;
                            if (checkboxInputNo.isChecked()) smoking = false;
                            Map<String,Object> user = new HashMap<>();
                            user.put("Username",username);
                            user.put("Weight",weight);
                            user.put("Cholesterol",cholesterol);
                            user.put("BloodSuger",bloodsuger);
                            user.put("EmergencyContact",emergency);
                            user.put("Smoking",smoking);
                            fStore.collection("users").document(userID).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UpdateProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG2",e.toString());
                                }
                            });
                        }

                    });
                }
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
}