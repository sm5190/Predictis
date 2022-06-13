package com.example.demopredictis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmergencyContact extends AppCompatActivity {
    private Button button,call,cancel;
    Toolbar toolbar;
    ImageView backBtn;
    TextView phone;
    Dialog dialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    String emergencyNumber, userID;
    DocumentReference documentReference;
    private static final int REQUEST_CALL = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);
        Log.d("TAG","Initial");
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

        userID = firebaseAuth.getCurrentUser().getUid();
        dialog = new Dialog(EmergencyContact.this);
        dialog.setContentView(R.layout.custom_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background_pop));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        call = dialog.findViewById(R.id.btnCall);
        cancel = dialog.findViewById(R.id.btnCancel);
        phone = dialog.findViewById(R.id.EmergencyContactNumber);

        documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    long l = document.getLong("EmergencyContact");
                    emergencyNumber = "0" + Long.toString(l);
                    //Toast.makeText(EmergencyContact.this, emergencyNumber, Toast.LENGTH_SHORT).show();
                    Log.d("TAG",emergencyNumber+"exit read me");
                    phone.setText(emergencyNumber);
                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(EmergencyContact.this, emergencyNumber, Toast.LENGTH_SHORT).show();
                            makePhoneCall(emergencyNumber);
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        //Log.d("TAG",emergencyNumber+"middle");
        //Toast.makeText(EmergencyContact.this, emergencyNumber, Toast.LENGTH_SHORT).show();
        //setContentView(R.layout.activity_emergency_contact);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(EmergencyContact.this, "Cancel", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        button = findViewById(R.id.contact);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        Log.d("TAG",emergencyNumber+"end");
    }
    private  void makePhoneCall(String phoneNumber) {
        if (phoneNumber.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(EmergencyContact.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EmergencyContact.this,
                        new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            }else {
                String dial = "tel:" + phoneNumber;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall(emergencyNumber);
            }else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}