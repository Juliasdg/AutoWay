#ifndef ANTI_DUPLICATE_H
#define ANTI_DUPLICATE_H

#include <Arduino.h>

class AntiDuplicate {
private:
  String lastUID;
  unsigned long lastReadTime;
  unsigned long interval;

public:
  AntiDuplicate(unsigned long minIntervalMs = 3000);
  bool isDuplicate(const String& uid);
};

#endif
