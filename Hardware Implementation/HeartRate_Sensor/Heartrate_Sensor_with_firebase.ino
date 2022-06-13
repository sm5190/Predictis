#include <ESP8266WiFi.h>
#include <SoftwareSerial.h>
#include <FirebaseArduino.h>
#include <ArduinoJson.h>
#include <ESP8266HTTPClient.h>

#define FIREBASE_HOST "demopredictis-default-rtdb.firebaseio.com" 
#define FIREBASE_AUTH "CymjhVl2k3WCyptWAizU6tLT6EFUw5GiNqk7DnEp" 

//My database Link : https://nodemcufirebase-d0d14-default-rtdb.firebaseio.com/
//My database Auth : 2n8nmjkPJez2FzVWtF94y4hWNZ6SJC2WTEj9TUwl

//Demo_Predictis Link : https://demopredictis-default-rtdb.firebaseio.com/
//Demo_Predictis Auth : CymjhVl2k3WCyptWAizU6tLT6EFUw5GiNqk7DnEp

#define ON_Board_LED 2  

const char* ssid = "TP-Link_A6C7"; 
const char* password = "36898991"; 


//long randNumber;

unsigned long previousMillisGetHR = 0; 
unsigned long previousMillisHR = 0; 

const long intervalGetHR = 100; 
const long intervalHR = 10000;

const int pulseRateSensorWire = A0;
const int LED_D1 = D1; // Led to detect when the heart is beating.The LED connected to pin D1
int Threshold = 600; // determine which signal to "count as beat" and which to ignore
int Signal; //holds incoming raw data

int cntHB = 0;
boolean ThresholdStat = true; 
int BPMval = 0;

void GetHeartRate(){
  unsigned long currentMillisGetHR = millis();

  //Serial.print("currentMillisGetHR : ");
  //Serial.println(currentMillisGetHR);

  if (currentMillisGetHR - previousMillisGetHR >= intervalGetHR) {
    previousMillisGetHR = currentMillisGetHR;

    int PulseSensorHRVal = analogRead(pulseRateSensorWire);

    if (PulseSensorHRVal > Threshold && ThresholdStat == true) {
      cntHB++;
      Serial.print("cntHB : ");
      Serial.println(cntHB);
      ThresholdStat = false;
      digitalWrite(LED_D1,HIGH);
    }

    if (PulseSensorHRVal < Threshold) {
      ThresholdStat = true;
      digitalWrite(LED_D1,LOW);
    }
  }
  
  unsigned long currentMillisHR = millis();

  if (currentMillisHR - previousMillisHR >= intervalHR) {
    previousMillisHR = currentMillisHR;

    BPMval = cntHB * 6;  
    String datasend = String(BPMval);
    Serial.print("BPM : ");
    Serial.println(datasend);
    Firebase.pushString("PulseRate/Value",datasend);

    if (Firebase.failed()){ 
      Serial.print("Setting /Value failed :");
      Serial.println(Firebase.error());  
      //delay(500);
      return;
    }

  Serial.println("successfully Updated!!");
  Serial.println();
    cntHB = 0;
  }
}


void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  delay(500);
  
  WiFi.begin(ssid, password); //--> Connect to your WiFi router
  Serial.println("");
    
  pinMode(ON_Board_LED,OUTPUT); //--> On Board LED port Direction output
  digitalWrite(ON_Board_LED, HIGH); //--> Turn off Led On Board

  //----------------------------------------Wait for connection
  Serial.print("Connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    //----------------------------------------Make the On Board Flashing LED on the process of connecting to the wifi router.
    digitalWrite(ON_Board_LED, LOW);
    delay(250);
    digitalWrite(ON_Board_LED, HIGH);
    delay(250);
    //----------------------------------------
  }
  //----------------------------------------
  digitalWrite(ON_Board_LED, HIGH); //--> Turn off the On Board LED when it is connected to the wifi router.
  //----------------------------------------If successfully connected to the wifi router, the IP Address that will be visited is displayed in the serial monitor.
  Serial.println("");
  Serial.print("Successfully connected to : ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  Serial.println();
  //----------------------------------------

  //----------------------------------------Firebase Realtime Database Configuration.
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  //----------------------------------------
}

void loop() {
  
  GetHeartRate();
  
}
