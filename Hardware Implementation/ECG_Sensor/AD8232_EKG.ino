#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>
#include <FirebaseArduino.h>
#include <ArduinoJson.h>
#include <ESP8266HTTPClient.h>

#define FIREBASE_HOST "demopredictis-default-rtdb.firebaseio.com" 
#define FIREBASE_AUTH "CymjhVl2k3WCyptWAizU6tLT6EFUw5GiNqk7DnEp"

const char* ssid = "TP-Link_A6C7"; 
const char* password = "36898991"; 

#define ON_Board_LED 2


int ecg = 0;
void setup() {

  Serial.begin(115200);
  delay(500);
  
  WiFi.begin(ssid, password); //--> Connect to your WiFi router
  Serial.println("");

  Serial.print("Connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    digitalWrite(ON_Board_LED, LOW);
    delay(250);
    digitalWrite(ON_Board_LED, HIGH);
    delay(250);
  }

  digitalWrite(ON_Board_LED, HIGH); 
  Serial.println("");
  Serial.print("Successfully connected to : ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  
  pinMode(D5, INPUT); // Setup for leads off detection LO +
  pinMode(D6, INPUT); // Setup for leads off detection LO -

}

void loop() { 
  if((digitalRead(D5) == 1)||(digitalRead(D6) == 1)){
    Serial.println("!");
  }
  else{
    ecg = analogRead(A0);
    ecg = ecg * 20;
    Serial.println(ecg);
    if((ecg >= 100) && (ecg <= 600)){
      String datasend = String(ecg);
      Serial.print("ECG/EKG data : ");
      Serial.println(datasend);
      Firebase.pushString("ECG/Value",datasend);
      if (Firebase.failed()){ 
      Serial.print("Setting /Value failed :");
      Serial.println(Firebase.error());  
      //delay(500);
      return;
      }
      delay(200);
    }
  }
}
