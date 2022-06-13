package com.example.demopredictis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswor extends AppCompatActivity {
    private EditText textEmail;
    private Button forgetPass;
    private FirebaseAuth auth;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passwor);

        auth = FirebaseAuth.getInstance();
        textEmail = findViewById(R.id.forgetPassEmail);
        forgetPass = findViewById(R.id.forgetBtn);
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

    }

    private void validate() {
        email = textEmail.getText().toString();
        if (email.isEmpty()){
            textEmail.setError("Required");
        }else {
            forgetPass();
        }
    }

    private void forgetPass() {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgetPasswor.this, "Check your email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),Login.class));
                            finish();
                        }else {
                            Toast.makeText(ForgetPasswor.this, "Error Occured ! Try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}