package com.example.demopredictis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {
    EditText loginEmail,loginPassword;
    Button loginBtn;
    ImageView backBtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    TextView forgetPassword;
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        backBtn = findViewById(R.id.backBtn);

        forgetPassword = (TextView) findViewById(R.id.forgotPassword);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgetPasswor.class));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginEmail.getText().toString().isEmpty()){
                    loginEmail.setError("Enter your email address");
                    return;
                }
                if (loginPassword.getText().toString().isEmpty()){
                    loginPassword.setError("Enter your password");
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(loginEmail.getText().toString(),loginPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (!firebaseAuth.getCurrentUser().isEmailVerified()){
                            startActivity(new Intent(getApplicationContext(),Verification.class));
                        }
                        else {
                            String userID = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if (document.getBoolean("Verify") == false){
                                            DatabaseReference databaseReference = firebaseDatabase.getInstance().getReference().child("CurrentUser1").child("ID");
                                            databaseReference.setValue(userID);
                                            startActivity(new Intent(getApplicationContext(),Form1.class));
                                        }else {
                                            DatabaseReference databaseReference = firebaseDatabase.getInstance().getReference().child("CurrentUser1").child("ID");
                                            databaseReference.setValue(userID);
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        }
                                    }
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}