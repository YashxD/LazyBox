//Libraries Included
#include  "SPI.h"
#include  "WiFi.h"
#include  "PubSubClient.h"
#include  "Wire.h"
#include  "EEPROM.h"
#include  "Timer.h"
//Macros Declaration
#define MQTT_USERNAME         "gldsqnue"
#define MQTT_PASS             "3IjECOD6a7It"
#define MQTT_TOPIC_SUB        "lazybox/switchstate"
#define MQTT_TOPIC_PUB        "lazybox/currentval"
#define MQTT_WILLTOPIC        "lazybox_willMsg"
#define MQTT_CLIENTID         "7aae1876-75a8-460f-b473-627f64df956a"
#define MQTT_WILLQOS          1
#define MQTT_WILLRETAIN       0
#define MQTT_WILLMSG          "ded."
#define MQTT_SERVER           "m24.cloudmqtt.com"
#define SERVER_PORT           14818


#define EEPROM_SIZE           100
#define MEMLOC_INITIATED      8
#define MEMLOC_WIFI_SSID_SIZE 09
#define MEMLOC_WIFI_SSID      10
#define MEMLOC_WIFI_PASS_SIZE 42
#define MEMLOC_WIFI_PASS      43
#define MEMLOC_LASTMSG        64

#define ACTION_PIN            13
#define RESET_PIN             15
#define HALL_PIN              27
//Variable Declarations
bool        isInit        = false;      //Check if ESP has been Initialised
bool        msgRecieved   = false;
bool        isCleanSession = false;
char*       wifi_ssid     = "BijuKoMatBolna";
char*       wifi_pass     = "jaimatadi";
uint8_t     wifi_ssid_siz = 0;
uint8_t     wifi_pass_siz = 0;
int         hall_current = 3; //testing
const int   mVperAmp = 66;  //volt->Amp scale factor
char*       current_char = NULL;
int         timer_event = 0;
Timer timer;
WiFiClient    espClient;
PubSubClient  mqttClient(espClient);

void setup() {
  //Define PinModes
  pinMode(ACTION_PIN, OUTPUT);
  pinMode(RESET_PIN,  OUTPUT);
  Serial.begin(115200);
  startESP();
  //Restore previous state
  if (!isInit) {
    if (msgRecieved)
      digitalWrite(ACTION_PIN, HIGH);
    else
      digitalWrite(ACTION_PIN, LOW);
  }
  setup_WiFi();
  mqttClient.setServer(MQTT_SERVER, SERVER_PORT);
  mqttClient.setCallback(MQTTCallback);

  //timer_event = timer.every(60000,pub_current, (void*)1);
}

void loop() {
  if (!mqttClient.connected())
    reconnect();
  mqttClient.loop();
  timer.update();
}

void pub_current(void* context){
  int time = (int) context;
  hall_current = read_current();
  to_Char(hall_current);
  char temp = 'a';
  current_char = &temp;
  mqttClient.publish(MQTT_TOPIC_PUB, current_char, true);
  Serial.println("Message Published");
}

void startESP() {
  //initialize EEPROM for saving state
  if (!EEPROM.begin(EEPROM_SIZE)) {
    Serial.println("Failed to initialise EEPROM");
    Serial.println("Resetting ESP32 in 10Secs");
    delay(10000);
    digitalWrite(RESET_PIN, HIGH);
  }
  isInit  = bool(EEPROM.read(MEMLOC_INITIATED));
  if (!isInit) {
    /*wifi_ssid_siz = uint8_t(EEPROM.read(MEMLOC_WIFI_SSID_SIZE));
      wifi_pass_siz = uint8_t(EEPROM.read(MEMLOC_WIFI_PASS_SIZE));
      for(int i=0; i<wifi_ssid_siz; i++)
        wifi_ssid += char(EEPROM.read(MEMLOC_WIFI_SSID + i));
      for(i=0; i<wifi_pass_siz; i++)
        wifi_ssid += char(EEPROM.read(MEMLOC_WIFI_PASS + i));
    */
    msgRecieved = bool(EEPROM.read(MEMLOC_LASTMSG));
    isCleanSession = true;
    Serial.println("Restored value of msgRecieved as ");
    Serial.print(msgRecieved);
  }
  else {
    //Initialize the ESP NOW.
    isInit = false;
    msgRecieved = false;
    isCleanSession = true;
    EEPROM.write(MEMLOC_INITIATED, isInit);
    EEPROM.write(MEMLOC_LASTMSG, msgRecieved);
    Serial.println("Wrote new value of msgRecieved as ");
    Serial.print(msgRecieved);
    EEPROM.commit();
  }

}

void setup_WiFi() {
  delay(10);                //But why?
  Serial.println("Connecting to ");
  Serial.print(wifi_ssid);

  WiFi.begin(wifi_ssid, wifi_pass);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print("-_");
  }
  Serial.println(" WiFi Connected");
  Serial.println("IP address: ");
  Serial.print(WiFi.localIP());
}

void MQTTCallback(char* topic, byte* msg, unsigned int len) {
  Serial.print("Message arrived on : ");
  Serial.print(topic);
  Serial.print(". Message: ");
  char messageTemp = msg[0];
  if (isCleanSession) {
    //Recieve Last message
    messageTemp = msg[len - 1];
  }
  /*for (int i = 0; i < len; i++) {
    Serial.print((char)msg[i]);
    messageTemp += msg[i];
    }*/
  Serial.println(messageTemp);
  if (String(topic).equals(MQTT_TOPIC_SUB)) {
    Serial.println("Changing output to ");
    if (messageTemp == '1') {
      Serial.println("switch ON");
      digitalWrite(ACTION_PIN, HIGH);
      msgRecieved = true;
      EEPROM.write(MEMLOC_LASTMSG, msgRecieved);
      EEPROM.commit();
    } else if (messageTemp == '0') {
      Serial.println("switch OFF");
      digitalWrite(ACTION_PIN, LOW);
      msgRecieved = false;
      EEPROM.write(MEMLOC_LASTMSG, msgRecieved);
      EEPROM.commit();
    }
  }
}

void reconnect() {
  //looping until succssfull
  while (!mqttClient.connected()) {
    Serial.print("Connecting to MQTT server \n");
    //attempt
    if (mqttClient.connect(MQTT_CLIENTID, MQTT_USERNAME, MQTT_PASS, MQTT_WILLTOPIC, MQTT_WILLQOS, MQTT_WILLRETAIN, MQTT_WILLMSG, isCleanSession)) {
      Serial.println("MQTT Connected");
      mqttClient.subscribe(MQTT_TOPIC_SUB);
      Serial.println("Subscribed to ");
      Serial.print(MQTT_TOPIC_SUB);
    } else {
      Serial.print("failed, rc = ");
      Serial.print(mqttClient.state());
      Serial.println("try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}
//to sample sensor values and return current to publish
float read_current() {
  //hall sensor code
  ///*
  float result;
  int maxVal = 0;
  int minVal = 1024;
  int hall_value;

  uint32_t start_t = millis();
  while ((millis() - start_t) < 1000) {
    hall_value = analogRead(HALL_PIN);
    if (hall_value > maxVal) maxVal = hall_value;
    if (hall_value < minVal) minVal = hall_value;
  }
  result = ((maxVal - minVal) * 5.0) / 1024.0; //Peak-2-Peak Volt
  //*/
  //result =++hall_current;
  result = (result / 2.0) * 0.707;    //VRMS Value
  result = (result * 1000) / mVperAmp; //Current RMS value
  return result;
}

//convert to char []
void to_Char(float val) {

  if ((int(val) % 100) >= 10) {
    //eg 12.323
    for (int i = 0; i < 6; i++) {
      if (i == 2) current_char[i] = '.';
      if (i != 2) {
        if (i == 1)
          current_char[i] = int(val / 10) % 10;
        else
          current_char[i] = int(val) % 10;
      }
      val = val * 10;
    }
  } else { //eg 5.623
    for (int i = 0; i < 5; i++) {
      if (i != 2) current_char[i] = int(val) % 10;
      if (i == 2) current_char[i] = '.';
      val * 10;
    }
  }
}
