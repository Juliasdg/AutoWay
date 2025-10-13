#include "api_client.h"
#include <ESP8266WiFi.h>   
#include <ESP8266HTTPClient.h>

void APIClient::sendToAPI(String rfid, String data, String hora) {
  if (WiFi.status() == WL_CONNECTED) {
    WiFiClient client;
    HTTPClient http;
    http.begin(client, "http://192.168.0.251:9000/api/passagens");
    http.addHeader("Content-Type", "application/json");

    String jsonPayload = "{\"rfid\":\"" + rfid + "\",\"data\":\"" + data + "\",\"hora\":\"" + hora + "\"}";
    Serial.println("📤 Enviando payload: " + jsonPayload);

    int httpResponseCode = http.POST(jsonPayload);

    if (httpResponseCode > 0) {
      Serial.println("✅ Enviado com sucesso! Código HTTP: " + String(httpResponseCode));
    } else {
      Serial.println("❌ Erro ao enviar: " + String(httpResponseCode));
    }

    http.end();
  } else {
    Serial.println("⚠️ WiFi não conectado!");
  }
}
