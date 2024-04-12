#include <LiquidCrystal.h>


#define BLACK 0
#define WHITE 1

#define READY -1
#define RUNNING 0
#define PAUSE 1
#define END 2


const int ledPins[] = { 3, 12 };
const int btnPins[] = { 2, 13 };
const int lcdPins[] = { 4, 5, 6, 7, 8, 9 };

LiquidCrystal lcd(lcdPins[0], lcdPins[1], lcdPins[2], lcdPins[3], lcdPins[4], lcdPins[5]);

int turn = BLACK;
int ledStatus[] = { LOW, LOW };
long int times[] = { 3000, 3000 };
char timeString[17]="0000000000000000";
int status= READY;
String cmd;
long int startTime = -1;                                                                                                                                                                                                                
long int passedTime = 0;                                                                                                                                                                                                                   
                                                                                                                                                                                                                                           

void handleStatusTransitions()
{
  switch(status)
  {
    case READY:
      if(Serial.available()) {
        Serial.readString();
      }
      if(digitalRead(btnPins[turn]) == HIGH){
        status=RUNNING;
      }
    break;

    case RUNNING:
      if(times[WHITE]<=0 || times[BLACK]<=0)
      {
        status=END;
      } else if(Serial.available()){
        cmd = Serial.readString();
        cmd.trim();
        if(cmd.equals("pause"))
        {
          startTime=-1;
          status=PAUSE;
        }
      }
      
    break;

    case PAUSE:
      if(Serial.available())
      {
        cmd = Serial.readString();
        cmd.trim();
        if(cmd.equals("restart"))
        {
          status=RUNNING;
        }
      }
    break;

    default:
    break;
  }


}

void changeTurn() {                                                                                                                                                                                                                                                                                                             
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
  if (startTime==-1) {                                                                                                                                                                                                                     
    startTime = (millis() / 100);                                                                         
  }else{
    passedTime = (millis() / 100) - startTime;
    times[turn] -= passedTime;
    startTime += passedTime;
  }
}

void handleDisplay()
{
  switch (status) {
  case READY:
    for (int i = 0; i < 2; i++) {
      snprintf(timeString, sizeof(timeString), "%03ld.%ld\s %c", times[i] / 10, times[i] % 10, (i == WHITE) ? 'W' : 'B');
      lcd.setCursor(0, i);
      lcd.print(timeString);
      
    }
    break;

  case RUNNING:
    for (int i = 0; i < 2; i++) {
      snprintf(timeString, sizeof(timeString), "%03ld.%ld\s %c", times[i] / 10, times[i] % 10, (i == WHITE) ? 'W' : 'B');
      lcd.setCursor(0, i);
      lcd.print(timeString);
      
    }
    break;

  case PAUSE:
    lcd.setCursor(0, 0);
    lcd.print("PAUSE           ");
    lcd.setCursor(0, 1);
    lcd.print("                ");
    break;
  case END:
    lcd.setCursor(0, 0);
    lcd.print("END             ");
    lcd.setCursor(0, 1);
    lcd.print("                ");
    break;

  default:
    lcd.setCursor(0, 0);
    lcd.print("ERROR           ");
    lcd.setCursor(0, 1);
    lcd.print("                ");
    break;
    break;
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
  handleStatusTransitions();
  if(status==READY)
  {
    handleLeds();
  } else if(status==RUNNING){
    handleButtons();
    handleLeds();
    handleTimes();
  }
  handleDisplay();
}
