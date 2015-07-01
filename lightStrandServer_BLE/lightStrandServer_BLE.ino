#include <IRremote.h>
#include <IRremoteInt.h>
#include "Adafruit_BLE_UART.h"
#include <SPI.h>
#include <avr/pgmspace.h>

// Connect CLK/MISO/MOSI to hardware SPI
// e.g. On UNO & compatible: CLK = 13, MISO = 12, MOSI = 11
#define ADAFRUITBLE_REQ 10
#define ADAFRUITBLE_RDY 2     // This should be an interrupt pin, on Uno thats #2 or #3
#define ADAFRUITBLE_RST 9

Adafruit_BLE_UART BTLEserial = Adafruit_BLE_UART(ADAFRUITBLE_REQ, ADAFRUITBLE_RDY, ADAFRUITBLE_RST);

unsigned long lastupdate;
aci_evt_opcode_t laststatus = ACI_EVT_DISCONNECTED;
     
IRsend irsend;
uint16_t  const power_off[] PROGMEM            = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };                                         
uint16_t  const power_on[]  PROGMEM            = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };                            
uint16_t  const brightness_up[] PROGMEM        = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
uint16_t  const brightness_down[] PROGMEM      = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
uint16_t  const flash[] PROGMEM                = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 39996, 8967, 2261, 552, };
uint16_t  const strobe[] PROGMEM               = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
uint16_t  const fade[] PROGMEM                 = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
uint16_t  const smooth[] PROGMEM               = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
uint16_t  const color_white[] PROGMEM          = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
uint16_t  const color_red[] PROGMEM            = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
uint16_t  const color_red_orange[] PROGMEM     = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
uint16_t  const color_orange[] PROGMEM         = { 8993, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 40101, 8993, 2261, 552 };
uint16_t  const color_orange_yellow[] PROGMEM  = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683,	552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
uint16_t  const color_yellow[] PROGMEM         = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552} ;
uint16_t  const color_green[] PROGMEM          = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
uint16_t  const color_light_green[] PROGMEM    = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
uint16_t  const color_green_blue[] PROGMEM     = { 8967, 4523, 552, 552, 552, 552, 552, 552 ,552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552,	552, 552, 552, 1683, 552, 1683,	552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683,	552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683,	552, 39996, 8967, 2261, 552 };
uint16_t  const color_light_blue[] PROGMEM     = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
uint16_t  const color_medium_blue[] PROGMEM    = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
uint16_t  const color_blue[] PROGMEM           = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
uint16_t  const color_dark_blue[] PROGMEM      = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
uint16_t  const color_dark_purple[] PROGMEM    = { 8967, 4523, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };
uint16_t  const color_medium_purple[] PROGMEM  = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 39996, 8967, 2261, 552 };
uint16_t  const color_light_purple[] PROGMEM   = { 8967, 4497, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 552, 1683, 552, 1683, 552, 1683, 552, 1683, 552, 40022, 8967, 2261, 552 };

void setup(void)
{
  Serial.begin(9600);
  Serial.println(F("Hello, CC3000!\n")); 

  //Serial.print(F("Free RAM: ")); Serial.println(getFreeRam(), DEC);
  
  BTLEserial.setDeviceName("LIGHTS"); /* 7 characters max! */

  BTLEserial.begin();
  
  Serial.println(F("Resetting Light Strand"));
  SendIRCommand( 0 );
}

void loop(void)
{

 // Tell the nRF8001 to do whatever it should be working on.
  BTLEserial.pollACI();

  // Ask what is our current status
  aci_evt_opcode_t status = BTLEserial.getState();
  // If the status changed....
  if (status != laststatus) {
    // print it out!
    if (status == ACI_EVT_DEVICE_STARTED) {
        Serial.println(F("* Advertising started"));
    }
    if (status == ACI_EVT_CONNECTED) {
        Serial.println(F("* Connected!"));
    }
    if (status == ACI_EVT_DISCONNECTED) {
        Serial.println(F("* Disconnected or advertising timed out"));
    }
    // OK set the last status change to this one
    laststatus = status;
  }

  if (status == ACI_EVT_CONNECTED) 
  {
    // Lets see if there's any data for us!
    if (BTLEserial.available()) 
    {
      Serial.print("* "); Serial.print(BTLEserial.available()); Serial.println(F(" bytes available from BTLE"));
    }

    if (BTLEserial.available())
    { 
      uint8_t tmp = BTLEserial.read();
      int code = ((int)tmp) - 48;
      Serial.print(F("Client sent \"")); Serial.print(code,DEC); Serial.println(F("\""));
      //Serial.print(F("Client sent \"")); Serial.print(code); Serial.println(F("\""));
       
      SendIRCommand( code );
    } 
  }
}


void SendIRCommand(uint8_t code)
{
  uint16_t const * cmd_array_ptr;
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

