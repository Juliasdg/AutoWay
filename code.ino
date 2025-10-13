#include <Arduino.h>
#include "wifi_setup.h"
#include "rfid_reader.h"
#include "ntp_time.h"
#include "api_client.h"
#include "anti_duplicate.h"


#define SS_PIN_1 4   // D2
#define RST_PIN_1 16 // D0
#define SS_PIN_2 5   // D1
#define RST_PIN_2 2  // D4

RFIDReader leitor1(SS_PIN_1, RST_PIN_1);
RFIDReader leitor2(SS_PIN_2, RST_PIN_2);
AntiDuplicate antiDup; 

void setup() {
  Serial.begin(115200);
  WiFiSetup::connect("2GINTERNET", "19012006");
  NTPTime::init();
  leitor1.init();
  leitor2.init();

  Serial.println("✅ Sistema pronto. Aproxime o cartão em qualquer leitor.");
}

void loop() {
  NTPTime::update();

  String data = NTPTime::getDate();
  String hora = NTPTime::getTime();

  String uid1 = leitor1.readUID();
  if (uid1 != "") {
    if (antiDup.isDuplicate(uid1)) {
      Serial.println("⚠️ UID repetido ignorado (Leitor 1).");
    } else {
      Serial.println("Leitor 1 -> UID: " + uid1);
      APIClient::sendToAPI(uid1, data, hora);
    }
  }

  String uid2 = leitor2.readUID();
  if (uid2 != "") {
    if (antiDup.isDuplicate(uid2)) {
      Serial.println("⚠️ UID repetido ignorado (Leitor 2).");
    } else {
      Serial.println("Leitor 2 -> UID: " + uid2);
      APIClient::sendToAPI(uid2, data, hora);
    }
  }

  delay(200);
}
