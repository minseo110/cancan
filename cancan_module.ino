#include <SoftwareSerial.h>
SoftwareSerial BTSerial(12, 11); // RX, TX

int relayPins[6] = {2, 3, 4, 10, 9, 8};
int inputSwitchPins[6] = {A3, A4, A5, A2, A1, A0};
int controlSwitchPins[3] = {5, 7, 6}; // RESET, NEXT, ENTER

int currentState[6] = {0};
bool isBrailleActive = false;
unsigned long lastOutputTime = 0;
const unsigned long outputDuration = 5000;

int lastReceivedDots[6] = {0};
int mode = 0; // 0: 출력 모드, 1: 입력 모드

void flushBTSerial() {
  while (BTSerial.available()) {
    BTSerial.read();
  }
}

void setup() {
  Serial.begin(9600);
  BTSerial.begin(9600);

  for (int i = 0; i < 6; i++) {
    pinMode(inputSwitchPins[i], INPUT);
    pinMode(relayPins[i], OUTPUT);
    digitalWrite(relayPins[i], LOW);
  }
  for (int i = 0; i < 3; i++) {
    pinMode(controlSwitchPins[i], INPUT);
  }

  BTSerial.println("READY: Arduino BLE 점자 출력/입력 모드 분리 지원");
  Serial.println("----시작----");

  // BLE 시리얼 버퍼 비우기
  flushBTSerial();
}

void loop() {
  handleModeChange();
  if (mode == 0) {
    handleOutputControl();
  } else if (mode == 1) {
    handleBrailleInput();
  }
}

void handleModeChange() {
  if (BTSerial.available()) {
    String line = BTSerial.readStringUntil('\n');
    line.trim();

    if (line.startsWith("mode:")) {
      if (line.endsWith("output")) {
        mode = 0;
        Serial.println("🟢 모드 전환: 출력 모드");
        BTSerial.println("mode:output:ok");
        flushBTSerial(); // 모드 전환 직후 버퍼 비우기
      } else if (line.endsWith("input")) {
        mode = 1;
        Serial.println("🔵 모드 전환: 입력 모드");
        BTSerial.println("mode:input:ok");
        flushBTSerial();
      }
      return;
    }

    if (mode == 0) {
      handleBrailleOutput(line);
    }
  }
}

void handleBrailleOutput(String line) {
  if (line == "done") {
    Serial.println("📴 done 명령 수신 - 릴레이 OFF");
    for (int i = 0; i < 6; i++) digitalWrite(relayPins[i], LOW);
    isBrailleActive = false;
    return;
  }

  // 유효성 검사: 6개의 숫자(0 또는 1), 콤마 5개
  int commaCount = 0;
  for (int i = 0; i < line.length(); i++) {
    if (line.charAt(i) == ',') commaCount++;
  }
  if (commaCount != 5) {
    Serial.print("⚠️ 잘못된 점자 명령 무시: "); Serial.println(line);
    return;
  }

  int dots[6] = {0};
  int idx = 0;
  while (line.length() > 0 && idx < 6) {
    int sep = line.indexOf(',');
    String num = (sep == -1) ? line : line.substring(0, sep);
    dots[idx++] = num.toInt();
    if (sep == -1) break;
    line = line.substring(sep + 1);
  }

  // dots 배열이 0 또는 1로만 구성되어 있는지 확인
  bool valid = true;
  for (int i = 0; i < 6; i++) {
    if (!(dots[i] == 0 || dots[i] == 1)) valid = false;
  }
  if (!valid) {
    Serial.println("⚠️ 점자값 오류, 명령 무시");
    return;
  }

  // 기존 점자 OFF
  for (int i = 0; i < 6; i++) {
    digitalWrite(relayPins[i], LOW);
  }
  delay(100); // OFF 후 잠깐 대기

  // 새로운 점자를 순차적으로 ON
  for (int i = 0; i < 6; i++) {
    if (dots[i] == 1) {
      digitalWrite(relayPins[i], HIGH);
      delay(500); // 점 간 출력 간격
    }
    lastReceivedDots[i] = dots[i];
  }

  isBrailleActive = true;
  lastOutputTime = millis(); // 자동 OFF 타이머 시작
}

void handleOutputControl() {
  if (digitalRead(controlSwitchPins[0]) == HIGH) { // RESET
    Serial.println("[출력] RESET 눌림 - 현재 점자 다시 출력");
    for (int i = 0; i < 6; i++) {
      if (lastReceivedDots[i] == 1) {
        digitalWrite(relayPins[i], HIGH);
        delay(500);
      }
    }
    isBrailleActive = true;
    lastOutputTime = millis();
    delay(300);
  }

  if (digitalRead(controlSwitchPins[1]) == HIGH) { // NEXT
    Serial.println("[출력] NEXT 눌림 - 다음 점자 요청");
    BTSerial.println("next");
    delay(300);
  }

  if (isBrailleActive && millis() - lastOutputTime > outputDuration) {
    for (int i = 0; i < 6; i++) digitalWrite(relayPins[i], LOW);
    isBrailleActive = false;
    Serial.println("🕒 릴레이 자동 OFF 완료");
  }
}

void handleBrailleInput() {
  for (int i = 0; i < 6; i++) {
    if (digitalRead(inputSwitchPins[i]) == HIGH) {
      currentState[i] = 1;
      delay(200);
    }
  }

  if (digitalRead(controlSwitchPins[0]) == HIGH) {
    for (int i = 0; i < 6; i++) currentState[i] = 0;
    Serial.println("[입력] 초기화");
    delay(300);
  }

  if (digitalRead(controlSwitchPins[1]) == HIGH) { // NEXT
    String msg = makeDotMessage();
    BTSerial.println(msg);
    Serial.print("[입력] 전송 (next 전): "); Serial.println(msg);
    BTSerial.println("next");
    Serial.println("[입력] 명령 전송: next");
    for (int i = 0; i < 6; i++) currentState[i] = 0;
    delay(300);
  }

  if (digitalRead(controlSwitchPins[2]) == HIGH) { // ENTER
    String msg = makeDotMessage();
    BTSerial.println(msg);
    Serial.print("[입력] 전송 (enter 전): "); Serial.println(msg);
    BTSerial.println("enter");
    Serial.println("[입력] 명령 전송: enter");
    for (int i = 0; i < 6; i++) currentState[i] = 0;
    delay(300);
  }
}

String makeDotMessage() {
  String msg = "";
  for (int i = 0; i < 6; i++) {
    msg += String(currentState[i]);
    if (i < 5) msg += ",";
  }
  return msg;
}
