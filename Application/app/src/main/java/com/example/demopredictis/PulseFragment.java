package com.example.demopredictis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PulseFragment extends Fragment {
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore fStore;
    FirebaseAuth firebaseAuth;
    ArrayList<Integer> pulseValue = new ArrayList<Integer>();
    int sum = 0;
    int count = 0;
    Toolbar toolbar;
    ImageView backBtn;
    TextView plWait;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_pulse, container, false);
        Button connect = (Button) view.findViewById(R.id.pulse_connect);
        toolbar = view.findViewById(R.id.include);
        plWait = (TextView) view.findViewById(R.id.pleaseWaitPulse);
        backBtn = (ImageView) toolbar.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        DatabaseReference conn= firebaseDatabase.getInstance().getReference().child("Status").child("PULSE");
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conn.setValue(1);
                connect.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.custom_button_verify_profile));
                connect.setText("CONNECTED");
                plWait.setVisibility(View.VISIBLE);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query lastQuery =reference.child("Status").child("PULSE");
                lastQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //String key = snapshot.getKey();
                        if (snapshot.getValue().toString().equals("0")) {
                            Toast.makeText(getActivity(),"Data Uploaded to database",Toast.LENGTH_SHORT).show();
                            connect.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.custom_button));
                            connect.setText("CONNECT");
                            plWait.setVisibility(View.INVISIBLE);
                            ExangCalculator();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //ExangCalculator();

            }
        });
        return view;
    }

    public void ExangCalculator(){
        fStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference reference = firebaseDatabase.getReference().child("Thalach").child(userID).child("Value");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String thalach = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    thalach = dataSnapshot.getValue().toString();
                }
                //System.out.println(thalach);
                int Thalach = Integer.parseInt(thalach);
                Map<String,Object> user = new HashMap<>();
                user.put("Thalach",Thalach);
                fStore.collection("users").document(userID).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Thalach Updated");
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Thalach not Updated");
            }
        });
    }
}
