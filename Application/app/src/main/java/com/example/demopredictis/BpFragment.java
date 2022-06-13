package com.example.demopredictis;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BpFragment extends Fragment {
    //Button connect;
    //Button disconnect;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    int systolicPressure;
    Toolbar toolbar;
    ImageView backBtn;
    TextView plWait;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_bp, container, false);
        plWait = (TextView) view.findViewById(R.id.pleaseWaitBP);
        toolbar = view.findViewById(R.id.include);
        backBtn = (ImageView) toolbar.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        Button connect = (Button) view.findViewById(R.id.bp_connect);
        DatabaseReference conn= firebaseDatabase.getInstance().getReference().child("Status").child("BP");
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conn.setValue(1);
                connect.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.custom_button_verify_profile));
                connect.setText("CONNECTED");
                plWait.setVisibility(View.VISIBLE);
                firebaseAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                String userID = firebaseAuth.getCurrentUser().getUid();
                System.out.println(userID);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query lastQuery =reference.child("Status").child("BP");
                lastQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //String key = snapshot.getKey();
                        if (snapshot.getValue().toString().equals("0")) {
                            Toast.makeText(getActivity(),"Data Uploaded to database",Toast.LENGTH_SHORT).show();
                            connect.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.custom_button));
                            connect.setText("CONNECT");
                            plWait.setVisibility(View.INVISIBLE);
                            tresBpsCollection();
                            //tresBpsUpdate(ans);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return view;
    }
    public void tresBpsCollection(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        Query anotherQuery = reference.child("Blood_Pressure").child(userID).orderByKey().limitToLast(1);
        anotherQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Log.d("TAG",dataSnapshot.getValue().toString());
                    //Toast.makeText(BpMonitor.this, dataSnapshot.child("dia").getValue().toString(), Toast.LENGTH_SHORT).show();
                    systolicPressure = Integer.parseInt(dataSnapshot.child("0").getValue().toString()) ;
                    tresBpsUpdate(systolicPressure);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //return systolicPressure;
    }
    public void tresBpsUpdate(int sys){
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        Map<String,Object> user = new HashMap<>();
        user.put("TresBps",sys);
        fStore.collection("users").document(userID).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //System.out.println("Oldpeak Updated");
            }
        });
    }
}

