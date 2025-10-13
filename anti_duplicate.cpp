#include "anti_duplicate.h"

AntiDuplicate::AntiDuplicate(unsigned long minIntervalMs) {
  lastUID = "";
  lastReadTime = 0;
  interval = minIntervalMs;
}

bool AntiDuplicate::isDuplicate(const String& uid) {
  unsigned long now = millis();

  if (uid == lastUID && (now - lastReadTime < interval)) {
    return true;
  }

  lastUID = uid;
  lastReadTime = now;

  return false;
}
