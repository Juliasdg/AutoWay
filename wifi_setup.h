#ifndef WIFI_SETUP_H
#define WIFI_SETUP_H

#include <ESP8266WiFi.h>

namespace WiFiSetup {
  void connect(const char* ssid, const char* password);
}

#endif
