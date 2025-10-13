#ifndef NTP_TIME_H
#define NTP_TIME_H

#include <NTPClient.h>
#include <WiFiUdp.h>

namespace NTPTime {
  void init();
  void update();
  String getDate();
  String getTime();
}

#endif
