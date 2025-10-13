#include "ntp_time.h"

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", -3 * 3600);

void NTPTime::init() {
  timeClient.begin();
}

void NTPTime::update() {
  timeClient.update();
}

String NTPTime::getDate() {
  time_t epochTime = timeClient.getEpochTime();
  struct tm *ptm = gmtime((time_t *)&epochTime);
  char dataStr[11];
  sprintf(dataStr, "%04d-%02d-%02d", (ptm->tm_year + 1900), (ptm->tm_mon + 1), ptm->tm_mday);
  return String(dataStr);
}

String NTPTime::getTime() {
  return timeClient.getFormattedTime();
}
