#include <Arduino.h>
#include <U8x8lib.h>

#define WL A1
#define MOSFET 2
#define dryLevel 400 //below 200 is dry
#define wetLevel 650 //above 600 is too wet
#define LED 4
#define buzzer 5
#define button 6

int WLval = 0;
int received_data = 0;
int buzzer_volume = 0;
int previous_data = 100;
auto display = U8X8_SSD1306_128X64_NONAME_HW_I2C(U8X8_PIN_NONE);

int enbAuto = 254;
int disAuto = 255;
int mode = 0; //0: Auto | 1: Manual

void setup() {
   pinMode(WL, INPUT);
   pinMode(MOSFET, OUTPUT);
   pinMode(LED, OUTPUT);
   pinMode(buzzer, OUTPUT);
   pinMode(button, INPUT);

   analogWrite(buzzer, 0);
   Serial.begin(9600);

   digitalWrite(MOSFET, LOW);
   digitalWrite(LED, LOW);
   
   display.begin(); 
   display.setFlipMode(1); 
   display.clearDisplay();                     
   display.setFont(u8x8_font_artossans8_r);

}

void sendData(int value)
{
  const byte data[] = {0, 0, highByte(value), lowByte(value)};
  Serial.write(data, 4); 
  Serial.println();
}

void autoPump()
{
  WLval = analogRead(WL);
  display.setCursor(0,0);
  display.print("Water Level: " + String(WLval) );
  display.setCursor(0, 3);
  display.print("Automatic Mode");
  
  if(WLval > dryLevel)
  {
    digitalWrite(MOSFET, HIGH);
  }
  else
  {
    digitalWrite(MOSFET, LOW);
  }
  
  Serial.println(WLval);
}

void manualPump(int received_data)
{
  WLval = analogRead(WL);
  display.setCursor(0,0);
  display.print("Water Level: " + String(WLval) );
  display.setCursor(0, 3);
  display.print("Manual Mode");

  if(received_data >= 100 && received_data <= 1000)
  {
    display.setCursor(0, 1);
    display.print("Buzzer volume: " + String(received_data));
    buzzer_volume = received_data;
  }
  
  if(WLval > wetLevel) //turn on LED to alert owner that there is flooding about to start
  {
    display.setCursor(0, 2);
    digitalWrite(LED, HIGH);
    tone(buzzer, buzzer_volume);
    
  }
  else if(WLval > dryLevel)
  {
    digitalWrite(LED, HIGH);
  }
  else
  {
    digitalWrite(LED, LOW);
    noTone(buzzer);
  }

  display.setCursor(0, 1);
  
  if(digitalRead(button) == HIGH) //turn on the pump if the button is pressed
  {
    digitalWrite(MOSFET, HIGH);
  }
  else
  {
    digitalWrite(MOSFET, LOW);
  }
  
  
}

void loop() {
  if(Serial.available() > 0)
  {
    
    received_data = Serial.read();
    
    if(received_data == enbAuto)
    {
      mode = 0;
    }
    else if(received_data == disAuto)
    {
      mode = 1;
    }
  }
  
    if (mode == 1)
    {
      manualPump(received_data);
    }
    else
    {
      autoPump();
    }
    
  
  
  
}
