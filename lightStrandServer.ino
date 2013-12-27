#include <Adafruit_CC3000.h>
#include <Adafruit_CC3000_Server.h>
#include <ccspi.h>
#include <SPI.h>
#include "utility/debug.h"
#include "utility/socket.h"
#include <IRremote.h>
#include <avr/pgmspace.h>

// These are the interrupt and control pins
#define ADAFRUIT_CC3000_IRQ   2  // MUST be an interrupt pin!
// These can be any two pins
#define ADAFRUIT_CC3000_VBAT  5
#define ADAFRUIT_CC3000_CS    10
// Use hardware SPI for the remaining pins
// On an UNO, SCK = 13, MISO = 12, and MOSI = 11
Adafruit_CC3000 cc3000 = Adafruit_CC3000(ADAFRUIT_CC3000_CS, ADAFRUIT_CC3000_IRQ, ADAFRUIT_CC3000_VBAT, SPI_CLOCK_DIV2); // you can change this clock speed

#define WLAN_SSID       "Avalon"           // cannot be longer than 32 characters!
#define WLAN_PASS       "DaveISGreat!HeGaveUsChocolateCake1234"
#define WLAN_SECURITY   WLAN_SEC_WPA2// Security can be WLAN_SEC_UNSEC, WLAN_SEC_WEP, WLAN_SEC_WPA or WLAN_SEC_WPA2

#define LISTEN_PORT     5000    // What TCP port to listen on for connections.
     
IRsend irsend;
prog_uint16_t power_off[] PROGMEM            = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };                                         
prog_uint16_t power_on[]  PROGMEM            = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };                            
prog_uint16_t brightness_up[] PROGMEM        = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
prog_uint16_t brightness_down[] PROGMEM      = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
prog_uint16_t flash[] PROGMEM                = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 39996, 8967, 2261, 552, };
prog_uint16_t strobe[] PROGMEM               = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
prog_uint16_t fade[] PROGMEM                 = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
prog_uint16_t smooth[] PROGMEM               = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
prog_uint16_t color_white[] PROGMEM          = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
prog_uint16_t color_red[] PROGMEM            = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
prog_uint16_t color_red_orange[] PROGMEM     = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
prog_uint16_t color_orange[] PROGMEM         = { 8993, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 40101, 8993, 2261, 552 };
prog_uint16_t color_orange_yellow[] PROGMEM  = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683,	552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
prog_uint16_t color_yellow[] PROGMEM         = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552} ;
prog_uint16_t color_green[] PROGMEM          = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
prog_uint16_t color_light_green[] PROGMEM    = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
prog_uint16_t color_green_blue[] PROGMEM     = { 8967, 4523, 552, 552, 552, 552, 552, 552 ,552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552,	552, 552, 552, 1683, 552, 1683,	552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683,	552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683,	552, 39996, 8967, 2261, 552 };
prog_uint16_t color_light_blue[] PROGMEM     = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
prog_uint16_t color_medium_blue[] PROGMEM    = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
prog_uint16_t color_blue[] PROGMEM           = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
prog_uint16_t color_dark_blue[] PROGMEM      = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
prog_uint16_t color_dark_purple[] PROGMEM    = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
prog_uint16_t color_medium_purple[] PROGMEM  = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
prog_uint16_t color_light_purple[] PROGMEM   = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };

Adafruit_CC3000_Server lightStrandServer(LISTEN_PORT);

void setup(void)
{
  Serial.begin(9600);
  Serial.println(F("Hello, CC3000!\n")); 

  Serial.print("Free RAM: "); Serial.println(getFreeRam(), DEC);
  
  /* Initialise the module */
  Serial.println(F("\nInitializing..."));
  if (!cc3000.begin())
  {
    Serial.println(F("Couldn't begin()! Check your wiring?"));
    while(1);
  }
  
  if (!cc3000.connectToAP(WLAN_SSID, WLAN_PASS, WLAN_SECURITY)) {
    Serial.println(F("Failed!"));
    while(1);
  }
   
  Serial.println(F("Connected!"));
  
  Serial.println(F("Request DHCP"));
  while (!cc3000.checkDHCP())
  {
    delay(100); // ToDo: Insert a DHCP timeout!
  }  

  /* Display the IP address DNS, Gateway, etc. */  
  uint32_t ipAddress, netmask, gateway, dhcpserv, dnsserv;
  
  if(!cc3000.getIPAddress(&ipAddress, &netmask, &gateway, &dhcpserv, &dnsserv))
  {
    Serial.println(F("Unable to retrieve the IP Address!\r\n"));   
  }
  else
  {
    Serial.print(F("\nIP Addr: ")); cc3000.printIPdotsRev(ipAddress);Serial.println();
  }
  
  // Start listening for connections
  lightStrandServer.begin();
  
  Serial.println(F("Listening for connections..."));  
  
  Serial.println(F("Resetting Light Strand"));

  SendIRCommand( 0 );
}

void loop(void)
{
  // Try to get a client which is connected.
  Adafruit_CC3000_ClientRef client = lightStrandServer.available();
  if (client) 
  {
     Serial.println(F("Client connected"));
    
     // Check if there is data available to read.
     if (client.available() > 0) 
     {
       // Read a byte and write it to all clients.
       uint8_t code = client.read();
       Serial.print(F("Client sent \"")); Serial.print(code,DEC); Serial.println(F("\""));
       
       SendIRCommand( code );       
       
       //client.close();
     }
  }
}

void SendIRCommand(uint8_t code)
{
  prog_uint16_t* cmd_array_ptr;
  switch(code)
  {
  case 0:
   Serial.println(F("Sending IR OFF command"));
   cmd_array_ptr = power_off;
   break;
  case 1:
   Serial.println(F("Sending IR ON command"));
   cmd_array_ptr = power_on;
   break;
  case 2:
   Serial.println(F("Sending IR Brightness UP command"));
   cmd_array_ptr = brightness_up;
   break;
  case 3:
   Serial.println(F("Sending IR Brightness DOWN command"));
   cmd_array_ptr = brightness_down;
   break;
  case 4:
   Serial.println(F("Sending IR FLASH command"));
   cmd_array_ptr = flash;
   break;
  case 5:
   Serial.println(F("Sending IR STROBE Command"));
   cmd_array_ptr = strobe;
   break;
  case 6:
   Serial.println(F("Sending IR FADE Command"));
   cmd_array_ptr = fade;
   break;
  case 7:
   Serial.println(F("Sending IR SMOOTH Command"));
   cmd_array_ptr = smooth;
   break;
  case 8:
   Serial.println(F("Sending COLOR WHITE Command"));
   cmd_array_ptr =  color_white;
   break;
  case 9:
   Serial.println(F("Sending COLOR RED Command"));
   cmd_array_ptr = color_red;
   break;
  case 10:
   Serial.println(F("Sending COLOR RED/ORANGE Command"));
   cmd_array_ptr = color_red_orange;
   break;
  case 11:
   Serial.println(F("Sending COLOR ORANGE Command"));
   cmd_array_ptr = color_orange;
   break;
  case 12:
   Serial.println(F("Sending COLOR ORANGE/YELLOW Command"));
   cmd_array_ptr = color_orange_yellow;
   break;
  case 13:
   Serial.println(F("Sending COLOR YELLOW Command"));
   cmd_array_ptr = color_yellow;
   break;
  case 14:
   Serial.println(F("Sending COLOR GREAN Command"));
   cmd_array_ptr = color_green;
   break;
  case 15:
   Serial.println(F("Sending COLOR LIGHT GREEN Command"));
   cmd_array_ptr = color_light_green;
   break;
  case 16:
   Serial.println(F("Sending COLOR GREEN/BLUE Command"));
   cmd_array_ptr =  color_green_blue;
   break;
  case 17:
   Serial.println(F("Sending COLOR LIGHT BLUE Command"));
   cmd_array_ptr = color_light_blue;
   break;
  case 18:
   Serial.println(F("Sending COLOR MEDIUM BLUE Command"));
   cmd_array_ptr = color_medium_blue;
   break;
  case 19:
   Serial.println(F("Sending COLOR BLUE Command"));
   cmd_array_ptr = color_blue;
   break;
  case 20:
   Serial.println(F("Sending COLOR DARK BLUE Command"));
   cmd_array_ptr = color_dark_blue;
   break;
  case 21:
   Serial.println(F("Sending COLOR DARK PURPLE Command"));
   cmd_array_ptr = color_dark_purple;
   break;
  case 22:
   Serial.println(F("Sending COLOR MEDIUM PURPLE Command"));
   cmd_array_ptr = color_medium_purple;
   break;
  case 23:
   Serial.println(F("Sending COLOR LIGHT PURPLE Command"));
   cmd_array_ptr = color_light_purple;
   break;
   
  }
   
  //convert the PROGMEM item into an array
  unsigned int tx_array[71];
  for(int i = 0; i < 71; i ++)
  {
   tx_array[i] =  pgm_read_word_near( cmd_array_ptr + i);
  }
  
  //send the ir code
  for (int i = 0; i < 3; i++) 
  {
   irsend.sendRaw(tx_array, 71, 38);
   delay(100);
  }     
}

