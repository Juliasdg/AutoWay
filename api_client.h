#ifndef API_CLIENT_H
#define API_CLIENT_H

#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>

namespace APIClient {
  void sendToAPI(String rfid, String data, String hora);
}

#endif
