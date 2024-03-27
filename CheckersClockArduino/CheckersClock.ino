#include <LiquidCrystal.h>


#define BLACK 0
#define WHITE 1


const int ledPins[] = { 3, 12 };
const int btnPins[] = { 2, 13 };
const int lcdPins[] = { 4, 5, 6, 7, 8, 9 };

LiquidCrystal lcd(lcdPins[0], lcdPins[1], lcdPins[2], lcdPins[3], lcdPins[4], lcdPins[5]);

int turn = BLACK;
int ledStatus[] = { LOW, LOW };
long int times[] = { 3000, 3000 };
char timeString[17]="0000000000000000";
long int startTime = -1;                                                                                                                                                                                                                   
long int passedTime = 0;                                                                                                                                                                                                                   
                                                                                                                                                                                                                                           
                                                                                                                                                                                                                                           
void changeTurn() {                                                                                                                                                                                                                        
  if (startTime==-1) {                                                                                                                                                                                                                     
    startTime = (millis() / 100);                                                                                   
  }                                                                                                                 
  turn = (turn + 1) % 2;                                                                                            
  Serial.println("shift");                                                                                          
}                                                                                                                   
                                                                                                                    
void handleButtons() {                                                                                              
  if (digitalRead(btnPins[turn]) == HIGH) changeTurn();                                                             
                                                                                                                    
}                                                                                                                   
                                                                                                                    
void handleLeds() {                                                                                                 
  if (ledStatus[turn] != HIGH) {                                                                                    
    ledStatus[turn] = HIGH;                                                                                         
    digitalWrite(ledPins[turn], HIGH);                                                                              
  }
  if (ledStatus[(turn + 1) % 2] != LOW) {
    ledStatus[(turn + 1) % 2] = LOW;
    digitalWrite(ledPins[(turn + 1) % 2], LOW);
  }
}

void handleTimes() {
  if (startTime != -1) {
    passedTime = (millis() / 100) - startTime;
    times[turn] -= passedTime;
    startTime += passedTime;
  }


  for (int i = 0; i < 2; i++) {
    
    snprintf(timeString, sizeof(timeString), "%03ld.%ld\s %c", times[i] / 10, times[i] % 10, (i == WHITE) ? 'W' : 'B');
    lcd.setCursor(0, i);
    lcd.print(timeString);
  }
}

void setup() {

  Serial.begin(9600);

  // leds and buttons init
  for (int i = 0; i < 2; i++) {
    pinMode(ledPins[i], OUTPUT);
    pinMode(btnPins[i], INPUT);
  }

  // lcd init
  lcd.begin(16, 2);
}

void loop() {
  handleButtons();
  handleLeds();
  handleTimes();
}
