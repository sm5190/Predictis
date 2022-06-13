void setup() {
  // initialize the serial communication:
  Serial.begin(9600);
  pinMode(D5, INPUT); // Setup for leads off detection LO +
  pinMode(D6, INPUT); // Setup for leads off detection LO -

}

void loop() { 
  if((digitalRead(D5) == 1)||(digitalRead(D6) == 1)){
    Serial.println("ECG/EKG data");
  }
  else{
    // send the value of analog input 0:
    Serial.println(analogRead(A0));
  }
  //Wait for a bit to keep serial data from saturating
  delay(100);
  Serial.end();
  delay(100);
}
