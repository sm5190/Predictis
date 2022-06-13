package com.example.demopredictis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;

public class RiskZone extends AppCompatActivity {

    TextView centerCircleText;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    Double age ,sex,cp,tresbps,chol,fbs,restecg,thalach,exang,slope,oldpeak;
    Toolbar toolbar;
    ImageView backBtn;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_risk_zone);

        //String url = "https://predictis-315416.et.r.appspot.com";
        //String url = "https://predictis-2.et.r.appspot.com";
        toolbar = findViewById(R.id.custom_tool);
        backBtn = (ImageView) toolbar.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        centerCircleText = findViewById(R.id.centerCircleText);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();
        System.out.println(userID);
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    age = document.getDouble("Age");
                    sex = document.getDouble("Gender");
                    cp = document.getDouble("ChestPain");
                    tresbps = document.getDouble("TresBps");
                    chol = document.getDouble("Cholesterol");
                    fbs = document.getDouble("BloodSuger");
                    restecg = document.getDouble("RestEcg");
                    thalach = document.getDouble("Thalach");
                    exang = document.getDouble("Exang");
                    slope = document.getDouble("Slope");
                    oldpeak = 0.0;
//                    System.out.println(age);
//                    System.out.println(sex);
//                    System.out.println(cp);
//                    System.out.println(tresbps);
//                    System.out.println(chol);
//                    System.out.println(fbs);
//                    System.out.println(restecg);
//                    System.out.println(thalach);
//                    System.out.println(exang);
//                    System.out.println(slope);
                    //System.out.println(oldpeak);
                    //Log.d("Hello",name[0]);
                    //Double op =
                    postJson_oldpeak(age,sex,cp,tresbps,chol,fbs,restecg,thalach,exang,slope);

                }
            }
        });
           // setActivityBackgroundColor('2');
        //setActivityBackgroundColor('2');

    }

    public void setActivityBackgroundColor(char prediction){
        View view = this.getWindow().getDecorView();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (prediction=='2') { //RED
                    //Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.predecition_red));
                    ImageView ivB = (ImageView) findViewById(R.id.bigCircle);
                    ivB.setImageResource(R.drawable.pan_red_circle);
                    ImageView ivS1 = (ImageView) findViewById(R.id.smallCircle1);
                    ivS1.setImageResource(R.drawable.pan_red_circle);
                    ImageView ivS2 = (ImageView) findViewById(R.id.smallCircle2);
                    ivS2.setImageResource(R.drawable.pan_red_circle);
                    TextView up = (TextView) findViewById(R.id.textUp);
                    up.setText("You are in high risk zone");
                    TextView down = (TextView) findViewById(R.id.textDown);
                    down.setText("Please contact your doctor");
                    TextView center = (TextView) findViewById(R.id.centerCircleText);
                    center.setText("RED");

                }
                if (prediction=='0') { //GREEN
                    //Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.predecition_green));
                    ImageView ivB = (ImageView) findViewById(R.id.bigCircle);
                    ivB.setImageResource(R.drawable.pan_green_circle);
                    ImageView ivS1 = (ImageView) findViewById(R.id.smallCircle1);
                    ivS1.setImageResource(R.drawable.pan_green_circle);
                    ImageView ivS2 = (ImageView) findViewById(R.id.smallCircle2);
                    ivS2.setImageResource(R.drawable.pan_green_circle);
                    TextView up = (TextView) findViewById(R.id.textUp);
                    up.setText("You are in safe zone");
                    TextView down = (TextView) findViewById(R.id.textDown);
                    down.setText("Follow your diet as usual");
                    TextView center = (TextView) findViewById(R.id.centerCircleText);
                    center.setText("GREEN");

                }
                if (prediction=='1') { //YELLOW
                    //Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.predection_yellow));
                    ImageView ivB = (ImageView) findViewById(R.id.bigCircle);
                    ivB.setImageResource(R.drawable.pan_yellow_circle);
                    ImageView ivS1 = (ImageView) findViewById(R.id.smallCircle1);
                    ivS1.setImageResource(R.drawable.pan_yellow_circle);
                    ImageView ivS2 = (ImageView) findViewById(R.id.smallCircle2);
                    ivS2.setImageResource(R.drawable.pan_yellow_circle);
                    TextView up = (TextView) findViewById(R.id.textUp);
                    up.setText("Improve diet and workout");
                    TextView down = (TextView) findViewById(R.id.textDown);
                    down.setText("Consult a doctor if possible");
                    TextView center = (TextView) findViewById(R.id.centerCircleText);
                    center.setText("Yellow");

                }
            }
        });
    }

    public void postJson_oldpeak(Double age,Double sex,Double cp,Double tresbps,Double chol,Double fbs,Double restecg,Double thalach,Double exang,Double slope) {
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

                String API_KEY = "4vWpPoqVmXFSgyAwIYwB91z-SPJOw1DemeJFfQDvWmkx";

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
                    URL scoringUrl = new URL("https://eu-gb.ml.cloud.ibm.com/ml/v4/deployments/15fb00d6-05d0-4b48-aabd-29c62e2b821a/predictions?version=2021-06-18");
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
                    String payload = "{\"input_data\": [{\"field\": [[\"age\",\"sex\",\"cp\",\"tresbps\",\"chol\",\"fbs\",\"restecg\",\"thalach\",\"exang\",\"slope\"]], \"values\": [["+age+","+sex+","+cp+","+tresbps+","+chol+","+fbs+","+restecg+","+thalach+","+exang+","+slope+"]]}]}";
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
                    String output = jsonStringScoring.toString().substring(56,60);
                    //System.out.println(output);
                    oldpeak = Double.parseDouble(output);
                    System.out.println(oldpeak);
                    Map<String,Object> user = new HashMap<>();
                    user.put("Oldpeak",oldpeak);
                    fStore.collection("users").document(userID).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //System.out.println("Oldpeak Updated");

                        }
                    });
                    postJson(age,sex,cp,tresbps,chol,fbs,restecg,thalach,exang,oldpeak,slope);
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
        //return oldpeak;
    }

    public void postJson(Double age,Double sex,Double cp,Double tresbps,Double chol,Double fbs,Double restecg,Double thalach,Double exang,Double oldpeak1,Double slope) {
        // NOTE: you must manually set API_KEY below using information retrieved from your IBM Cloud account.

        new Thread(new Runnable() {
            @Override
            public void run() {

                FirebaseFirestore fStore;
                FirebaseAuth firebaseAuth;
                firebaseAuth = FirebaseAuth.getInstance();
                fStore = FirebaseFirestore.getInstance();
                String userID = firebaseAuth.getCurrentUser().getUid();

                String API_KEY = "hA_IWo-SwMaRxJ5fj_g2FqQefj-cFnbEH4XX6mHMQeF8";

                HttpURLConnection tokenConnection = null;
                HttpURLConnection scoringConnection = null;
                BufferedReader tokenBuffer = null;
                BufferedReader scoringBuffer = null;
                try {
                    // Getting IAM token
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
                    URL scoringUrl = new URL("https://eu-gb.ml.cloud.ibm.com/ml/v4/deployments/525ae765-f887-45ce-9783-246a7fc24390/predictions?version=2021-06-18");
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
                    String payload = "{\"input_data\": [{\"field\": [[\"age\",\"sex\",\"cp\",\"tresbps\",\"chol\",\"fbs\",\"restecg\",\"thalach\",\"exang\",\"oldpeak\",\"slope\"]], \"values\": [["+age+","+sex+","+cp+","+tresbps+","+chol+","+fbs+","+restecg+","+thalach+","+exang+","+oldpeak1+","+slope+"]]}]}";
                    //System.out.println(payload);
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
                    String output = jsonStringScoring.toString();
                    String prediction = String.valueOf(output.charAt(71));
                    Map<String,Object> user = new HashMap<>();
                    user.put("Prediction",prediction);
                    fStore.collection("users").document(userID).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //System.out.println("Oldpeak Updated");

                        }
                    });
                    setActivityBackgroundColor(output.charAt(71));
                    //System.out.println(output.charAt(70));
                    //System.out.println(output.charAt(71));
                    //System.out.println(output.charAt(72));

                } catch (IOException e) {
                    Toast.makeText(RiskZone.this, "Failed. Try again!", Toast.LENGTH_SHORT).show();
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


