/* hardware_code

Deloitte. TechnoUtsav 3.0
By Team: <UIET Chandigarh_intelliJ>

Insert your wifi name and password below in the definition of WIFI_SSID and WIFI_PASSWORD.
Upload this code to the ESP-8266 after selecting 'Generic ESP8266 Module' from Tools->Boards.

CONNECTIONS:

DH11 Digital Pin --------------> ESP Digital Pin 16

Soil-Moisture Analog Pin ------> ESP Analog Pin A0

Soil-pH Analog Pin ------------> ESP Pin 14

MQ-135 Analog Pin -------------> ESP Pin 12

*/

#include <ESP8266WiFi.h>    
#include <SoftwareSerial.h>                                               
#include <FirebaseArduino.h>                                                
#include <DHT.h>    
#include <NTPClient.h>
#include <WiFiUdp.h>

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "asia.pool.ntp.org", 19800,60000);
                                          
#define FIREBASE_HOST "https://smart-agriculture-deloitte.firebaseio.com/"                          
#define FIREBASE_AUTH "bMdhP712y1gVK6OLObM47owAiCeHJiAozKlN76HE"            

#define WIFI_SSID ""                                                               //name of your WiFi                                           
#define WIFI_PASSWORD ""                                                           //password
 

#define DHTPIN 16                                                                  //pin 16 for DH11 
#define moisture_pin A0                                                            // A0 for soil moisture sensor
#define ph_pin 14                                                                  // 14 for soil pH sensor
#define mq135_pin 12                                                               // 12 for MQ-135 sensor
                                             
#define DHTTYPE DHT11                                                       

DHT dht(DHTPIN, DHTTYPE);     

int buf[10];


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

float getph(){

  for(int i=0;i<10;i++)       
  { 
    buf[i]=analogRead(ph_pin);
    delay(10);
  }
  for(int i=0;i<9;i++)        
  {
    for(int j=i+1;j<10;j++)
    {
      if(buf[i]>buf[j])
      {
        int temp=buf[i];
        buf[i]=buf[j];
        buf[j]=temp;
      }
    }
  }
  float avgValue=0.00;
  for(int i=2;i<8;i++)                      
    avgValue+=buf[i];
  float phValue=(float)avgValue*5.0/1024/6; //convert the analog into millivolt
  phValue=3.5*phValue;  

  return phValue;
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////

void setup() {
  
  Serial.begin(9600);
  delay(1000);                
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);                                            //try to connect with wifi
  
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("Connected to ");
  Serial.println(WIFI_SSID);
  Serial.print("IP Address is : ");
  Serial.println(WiFi.localIP());                                            //print local IP address
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);                              // connect to firebase
  dht.begin();                                                               //Start reading dht sensor
  timeClient.begin();
  
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////


void loop() { 
  
  float humidity = dht.readHumidity();                                                  
  float temperature = dht.readTemperature();      //default (celcius)
  float moisture = ( 100.00 - ((analogRead(moisture_pin)/1023.00) * 100.00 ));  
  float ph = getph();
  float mq135_value = analogRead(mq135_pin);
    
  if (isnan(humidity) || isnan(temperature)) {                                                
    Serial.println(F("Failed to read from DHT sensor!"));
    return;
  }
  
  
  String humidity_percentage = String(humidity) + String("%");  
  String moisture_percentage = String(moisture) + String("%");                                      
  String temperature_celcius = String(temperature) + String("°C");                                                     
  delay(4000);

  timeClient.update();

  while(!timeClient.update()){
    timeClient.forceUpdate();
  }

  String date_time = timeClient.getFormattedDate();
  

  Firebase.pushString("/Humidity", humidity_percentage);                                  
  Firebase.pushString("/Temperature", temperature_celcius); 
  Firebase.pushString("/Soil-Moisture", moisture_percentage); 
  Firebase.pushFloat("/Soil-pH", ph);      
  Firebase.pushFloat("/Air-Quality", mq135_value);
  Firebase.pushString("/Date-Time", date_time); 


                          
}
