#include "rfid_reader.h"

RFIDReader::RFIDReader(int ssPin, int rstPin) : rfid(ssPin, rstPin) {}

void RFIDReader::init() {
  SPI.begin();
  rfid.PCD_Init();
}

String RFIDReader::readUID() {
  if (!rfid.PICC_IsNewCardPresent() || !rfid.PICC_ReadCardSerial()) return "";

  String uid = "";
  for (byte i = 0; i < rfid.uid.size; i++) {
    if (rfid.uid.uidByte[i] < 0x10) uid += "0";
    uid += String(rfid.uid.uidByte[i], HEX);
  }
  uid.toUpperCase();
  rfid.PICC_HaltA();
  return uid;
}
