#ifndef RFID_READER_H
#define RFID_READER_H

#include <SPI.h>
#include <MFRC522.h>

class RFIDReader {
private:
  MFRC522 rfid;

public:
  RFIDReader(int ssPin, int rstPin);
  void init();
  String readUID();
};

#endif
