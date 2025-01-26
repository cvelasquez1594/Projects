 //*****SENIOR DESIGN 1 GROUP 23*******
 //*****A.T.L.A.S*****

TaskHandle_t Task1;
TaskHandle_t Task2;

#include "HUSKYLENS.h"
#include "ESP32Servo.h"
#include <LiquidCrystal_I2C.h>

LiquidCrystal_I2C lcd(0x27, 16, 2);

const int minPixelHor = 10;
const int lowPixelHor = 125;
const int highPixelHor = 195;
const int startAngleHor = 90;
const int maxServoHor = 130;
const float deltaHor = 1.5;
const byte servoPinHor = 13;

const int minPixelVert = 10;
const int lowPixelVert = 80;
const int highPixelVert = 140;
const int startAngleVert = 90;
const int maxServoVert = 120;
const float deltaVert = 1.0;
const byte servoPinVert = 12;

const int trackID = 0;

struct servoType {
  Servo servo;
  int angle;
  float fAngle;
  float delta;
  int maxServoAngle;

  void init(byte aPin, int startAngle, int aDelta, int MaxServoAngle) {
    servo.attach(aPin);
    angle = startAngle;
    fAngle = startAngle;
    delta = aDelta;
    maxServoAngle = MaxServoAngle;
    servo.write(angle);
  }
  void movePlus() {
    move(1);
  }
  void moveMinus() {
    move(-1);
  }
  void move(int dir) {
    fAngle += dir * delta;
    angle = constrain(fAngle, 0, maxServoAngle);
    servo.write(fAngle);
    if (fAngle < 0.0) { fAngle = 0.0; }
    if (fAngle > maxServoAngle) { fAngle = maxServoAngle; }
  }
};

HUSKYLENS huskylens;
servoType horizontal;
servoType vertical;
int flyWheel = 26;
int trigger = 25;
int sensor = 35;
int ammoCount = 100;
#define SDA_2 33
#define SCL_2 32

void setup() {
  Serial.begin(115200);
  Wire.begin();
  Wire1.begin(SDA_2, SCL_2);
  horizontal.init(servoPinHor, startAngleHor, deltaHor, maxServoHor);    // Connect the servo to pin , set start angle, set Delta, set max Servo Angle, move servo to start angle
  vertical.init(servoPinVert, startAngleVert, deltaVert, maxServoVert);  // Connect the servo to pin, set start angle, set Delta,set max Servo Angle, move servo to start angle
  pinMode(flyWheel, OUTPUT);
  pinMode(trigger, OUTPUT);
  pinMode(sensor, INPUT);

  while (!huskylens.begin(Wire1)) {
    Serial.println(F("Begin failed!"));
    Serial.println(F("1.Please recheck the \"Protocol Type\" in HUSKYLENS (General Settings>>Protocol Type>>Serial 9600)"));
    Serial.println(F("2.Please recheck the connection."));
    delay(100);

    //huskylens.writeAlgorithm(ALGORITHM_OBJECT_TRACKING);
    huskylens.writeAlgorithm(ALGORITHM_FACE_RECOGNITION);
    //huskylens.writeAlgorithm(ALGORITHM_OBJECT_RECOGNITION);
  }

  //create a task that will be executed in the Task2code() function, with priority 1 and executed on core 1
  xTaskCreatePinnedToCore(
    Task2code,      /* Task function. */
    "Shoot_Target", /* name of task. */
    10000,          /* Stack size of task */
    NULL,           /* parameter of the task */
    1,              /* priority of the task */
    &Task2,         /* Task handle to keep track of created task */
    1);             /* pin task to core 1 */

  //LCD Setup
  lcd.begin();
  lcd.backlight();
  lcd.setCursor(2, 0);
  lcd.print("PROJECT ATLAS");
  lcd.setCursor(4, 1);
  lcd.print("GROUP 23");
  delay(2000);
}

//Core 0 -- Tracking of the Target
void loop() {

  if (!huskylens.request()) return;
  else {

    for (int i = 0; i < huskylens.countBlocks(); i++) {
      HUSKYLENSResult result = huskylens.getBlock(i);
      printResult(result);

      if (result.ID == trackID) {
        if (handlePan(result.xCenter) || handleTilt(result.yCenter)) {
          delay(15);  // Add a delay to allow the servo to move to the new position
        }
      }
    }
  }
}

//Core 1: Shoot to the target while still tracking
void Task2code(void* pvParameters) {

  for (;;) {

    if (!huskylens.request()) return;

    for (int i = 0; i < huskylens.countBlocks(); i++) {
      HUSKYLENSResult result = huskylens.getBlock(i);
      if (result.ID == trackID) {
        shootTarget(result.xCenter, result.yCenter);
      }
    }
    digitalWrite(flyWheel, LOW);
    int sensorSignal = digitalRead(sensor);
    lcd.clear();  // clear previous values from screen
    lcd.setCursor(5, 0);
    lcd.print("ATLAS");
    
    if (sensorSignal == 0) {
      ammoCount--;
    }

    if (ammoCount <= 0) {
      lcd.setCursor(0, 1);
      lcd.print("Mag-Empty-Reload");
    } else {
      lcd.setCursor(0, 1);
      lcd.print("Ammo Left: ");
      lcd.setCursor(12, 1);
      lcd.print(ammoCount);
    }
    delay(1);
  }
}

void shootTarget(int x, int y) {
  if (x >= 150 && x <= 170) {
    digitalWrite(flyWheel, HIGH);
    delay(2000);
    digitalWrite(trigger, HIGH);
    delay(250);
    digitalWrite(trigger, LOW);
  } else {
    digitalWrite(trigger, LOW);
    digitalWrite(flyWheel, LOW);
  }
}

boolean handlePan(int xCenter) {
  byte mode = 0;
  if (xCenter > minPixelHor && xCenter < lowPixelHor) { mode = 1; }
  if (xCenter > highPixelHor) { mode = 2; }
  switch (mode) {
    case 0:  // No change with x_center below minPixelHor or between lowPixelHor and highPixelHor
      break;
    case 1:  // Increase servo angle
      horizontal.movePlus();
      break;
    case 2:  // Decrease servo angle
      horizontal.moveMinus();
      break;
  }
  return mode;
}

boolean handleTilt(int yCenter) {
  byte mode = 0;
  if (yCenter > minPixelVert && yCenter < lowPixelVert) { mode = 1; }
  if (yCenter > highPixelVert) { mode = 2; }
  switch (mode) {
    case 0:  // No change with yCenter below 10 or between 90 and 150
      break;
    case 1:  // Decrease servo angle
      vertical.moveMinus();
      break;
    case 2:  // Increase servo angle
      vertical.movePlus();
      break;
  }
  return mode;
}

void printResult(HUSKYLENSResult Result) {
  Serial.print("Object tracked at X: ");
  Serial.print(Result.xCenter);
  Serial.print(", Y: ");
  Serial.print(Result.yCenter);
  Serial.print(", Width: ");
  Serial.print(Result.width);
  Serial.print(", Height: ");
  Serial.print(Result.height);
  Serial.print(", Tracked ID: ");
  Serial.println(Result.ID);
}
