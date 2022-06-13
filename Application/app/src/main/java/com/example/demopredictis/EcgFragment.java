package com.example.demopredictis;

import android.os.Bundle;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EcgFragment extends Fragment {

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    Toolbar toolbar;
    ImageView backBtn;
    TextView plWait;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ecg, container, false);
        Button connect = (Button) view.findViewById(R.id.ecg_connect);
        plWait = (TextView) view.findViewById(R.id.pleaseWaitECG);
        toolbar = view.findViewById(R.id.include);
        backBtn = (ImageView) toolbar.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        DatabaseReference conn = firebaseDatabase.getInstance().getReference().child("Status").child("ECG");

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
                Query lastQuery =reference.child("Status").child("ECG");
                lastQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //String key = snapshot.getKey();
                        if (snapshot.getValue().toString().equals("0")) {
                            Toast.makeText(getActivity(),"Data Uploaded to database",Toast.LENGTH_SHORT).show();
                            connect.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.custom_button));
                            connect.setText("CONNECT");
                            plWait.setVisibility(View.INVISIBLE);
                            restEcgCollect();

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


    public void restEcgCollect(){
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query anotherQuery =reference.child("Rest_ECG").child(userID).child("Value").orderByKey().limitToLast(1);
        anotherQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //String key = snapshot.getKey();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Log.d("TAG",dataSnapshot.getValue().toString());
                    //Toast.makeText(BpMonitor.this, dataSnapshot.child("dia").getValue().toString(), Toast.LENGTH_SHORT).show();
                    String op = dataSnapshot.getValue().toString();
                    int restEcg = Integer.parseInt(op) ;
                    restEcgUpdate(restEcg);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void restEcgUpdate(int rest){
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        Map<String,Object> user = new HashMap<>();
        user.put("RestEcg",rest);
        fStore.collection("users").document(userID).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dataFetch();
            }
        });
    }


    public void dataFetch(){
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    Double age = document.getDouble("Age");
                    Double sex = document.getDouble("Gender");
                    Double cp = document.getDouble("ChestPain");
                    Double tresbps = document.getDouble("TresBps");
                    Double chol = document.getDouble("Cholesterol");
                    Double fbs = document.getDouble("BloodSuger");
                    Double restecgPredict = document.getDouble("RestEcg");
                    Double thalach = document.getDouble("Thalach");
                    Double exang = document.getDouble("Exang");
                    System.out.println(age);
                    System.out.println(sex);
                    System.out.println(cp);
                    System.out.println(tresbps);
                    System.out.println(chol);
                    System.out.println(fbs);
                    System.out.println(restecgPredict);
                    System.out.println(thalach);
                    System.out.println(exang);
                    //Log.d("Hello",name[0]);
                    postJson(age,sex,cp,tresbps,chol,fbs,restecgPredict,thalach,exang);


                }
            }
        });
    }

    public void postJson(Double age,Double sex,Double cp,Double tresbps,Double chol,Double fbs,Double restecg,Double thalach,Double exang) {
        // NOTE: you must manually set API_KEY below using information retrieved from your IBM Cloud account.
        new Thread(new Runnable() {
            @Override
            public void run() {

                //Log.d("Hello",name[0]);
                //System.out.println(name);
                FirebaseFirestore fStore;
                FirebaseAuth firebaseAuth;
                firebaseAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                String userID = firebaseAuth.getCurrentUser().getUid();

                String API_KEY = "m1dvN02PhzshnNMQtfRStYGxlzxXmCglN-egCM-Cx6WM";

                HttpURLConnection tokenConnection = null;
                HttpURLConnection scoringConnection = null;
                BufferedReader tokenBuffer = null;
                BufferedReader scoringBuffer = null;
                try {
                    // Getting IAM token
                    //System.out.println("hello");
                    URL tokenUrl = new URL("https://iam.cloud.ibm.com/identity/token?grant_type=urn:ibm:params:oauth:grant-type:apikey&apikey=" + API_KEY);
                    tokenConnection = (HttpURLConnection) tokenUrl.openConnection();
                    tokenConnection.setDoInput(true);
                    tokenConnection.setDoOutput(true);
                    tokenConnection.setRequestMethod("POST");
                    tokenConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    tokenConnection.setRequestProperty("Accept", "application/json");
                    tokenBuffer = new BufferedReader(new InputStreamReader(tokenConnection.getInputStream()));
                    StringBuffer jsonString = new StringBuffer();
                    String line;
                    while ((line = tokenBuffer.readLine()) != null) {
                        jsonString.append(line);
                    }
                    //System.out.println(jsonString.toString());
                    // Scoring request
                    URL scoringUrl = new URL("https://us-south.ml.cloud.ibm.com/ml/v4/deployments/cbe67c25-948b-41f3-98f7-683857a2ae99/predictions?version=2021-06-06");
                    String iam_token = "Bearer " + jsonString.toString().split(":")[1].split("\"")[1];
                    scoringConnection = (HttpURLConnection) scoringUrl.openConnection();
                    //System.out.println(scoringConnection);
                    scoringConnection.setDoInput(true);
                    scoringConnection.setDoOutput(true);
                    scoringConnection.setRequestMethod("POST");
                    scoringConnection.setRequestProperty("Accept", "application/json");
                    scoringConnection.setRequestProperty("Authorization", iam_token);
                    scoringConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    OutputStreamWriter writer = new OutputStreamWriter(scoringConnection.getOutputStream(), "UTF-8");

                    // NOTE: manually define and pass the array(s) of values to be scored in the next line
                    //String payload = "{\"input_data\": [{\"field\": [[\"age\",\"sex\",\"cp\",\"tresbps\",\"chol\",\"fbs\",\"restecg\",\"thalach\",\"exang\",\"oldpeak\",\"slope\"]], \"values\": [[63,1,1,145,233,1,2,150,0,2.3,1]]}]}";
                    //String payload = "{\"input_data\": [{\"field\": [[\"age\",\"sex\",\"cp\",\"tresbps\",\"chol\",\"fbs\",\"restecg\",\"thalach\",\"exang\"]], \"values\": [[54,0,2,135,304,1,0,170,0]]}]}";
                    String payload = "{\"input_data\": [{\"field\": [[\"age\",\"sex\",\"cp\",\"tresbps\",\"chol\",\"fbs\",\"restecg\",\"thalach\",\"exang\"]], \"values\": [["+age+","+sex+","+cp+","+tresbps+","+chol+","+fbs+","+restecg+","+thalach+","+exang+"]]}]}";
                    writer.write(payload);
                    writer.close();

                    scoringBuffer = new BufferedReader(new InputStreamReader(scoringConnection.getInputStream()));
                    //System.out.println("bello");
                    StringBuffer jsonStringScoring = new StringBuffer();

                    String lineScoring;
                    //System.out.println("Hello");
                    while ((lineScoring = scoringBuffer.readLine()) != null) {
                        jsonStringScoring.append(lineScoring);
                    }
                    //System.out.println(jsonStringScoring.toString());
                    //System.out.println(jsonStringScoring.toString().charAt(56));
                    char output = jsonStringScoring.toString().charAt(56);
                    int slope = Integer.parseInt(String.valueOf(output));
                    Map<String,Object> user = new HashMap<>();
                    user.put("Slope",slope);
                    fStore.collection("users").document(userID).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println("Slope Updated");
                        }
                    });
                    //setActivityBackgroundColor(output.charAt(71));

                } catch (IOException e) {
                    //Toast.makeText(gthis, "Failed. Try again!", Toast.LENGTH_SHORT).show();
                } finally {
                    if (tokenConnection != null) {
                        tokenConnection.disconnect();
                    }
                    if (tokenBuffer != null) {
                        try {
                            tokenBuffer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (scoringConnection != null) {
                        scoringConnection.disconnect();
                    }
                    if (scoringBuffer != null) {
                        try {
                            scoringBuffer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }
}