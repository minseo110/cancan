# ✨ 캡스톤 디자인: 점자 학습 앱 (CanCan)

## 목차

1. [프로젝트 소개](#프로젝트-소개)
2. [주요 기능](#주요-기능)
3. [아키텍처 & 기술 스택](#아키텍처--기술-스택)
4. [화면 흐름 & UI 설계](#화면-흐름--ui-설계)
5. [기능 상세](#기능-상세)

   1. [점자 사전 (카메라 → 음성)](#1-점자-사전-카메라--음성)
   2. [음성인식 → 점자 변환 & 출력](#2-음성인식--점자-변환--출력)
   3. [점자 퀴즈](#3-점자-퀴즈)
   4. [BLE 통신 & 솔레노이드 제어](#4-ble-통신--솔레노이드-제어)
6. [프로젝트 구조](#프로젝트-구조)
7. [팀원](#팀원)
8. [라이선스](#라이선스)
9. [참고 자료](#참고-자료)

---

## 프로젝트 소개

시각 장애 학습자를 위한 점자 학습 보조 앱으로, 카메라·음성 입출력·BLE 통신을 연계하여 직관적인 학습 환경을 제공한다. 

* **목적**: 시각 장애 사용자의 독립적 점자 학습 지원
* **주요 특징**:

  * 점자 사진 인식 후 한글 음성 안내
  * 음성 입력 기반 점자 변환 및 솔레노이드 출력
  * 점자 퀴즈

---

## 주요 기능

| 기능        | 설명                                               |
| --------- | ------------------------------------------------ |
| 📷 점자 인식  | 카메라 촬영 → 점자 인식 및 변환 → TTS(텍스트 → 음성) 출력 |
| 🎙️ 음성 인식 | STT(음성 → 텍스트) → 점자 변환 → BLE 전송      |
| 📝 점자 퀴즈  | Room DB 기반 문제 출제(5문제), 정답·오답 점수 반영 → 음성·점자 출력      |
| 🔗 BLE 통신 | HM-10 BLE 모듈을 이용한 아두이노 솔레노이드 제어                  |

---

## 아키텍처 & 기술 스택

* **플랫폼**: Android (Kotlin)
* **통신**: BLE (HM-10)
* **주요 라이브러리**:
  * Google Speech-to-Text & Text-to-Speech
  * Room Database
  * BLEManager + ForegroundService
* **UI 설계**: Figma
* **개발 환경**: Android Studio Arctic Fox (Kotlin 1.5+)

---

## 화면 흐름 & UI 설계

1. **시작 화면** (`MainActivity`) – 앱 소개 및 시작 버튼
2. **홈 화면** (`HomeActivity`) – 기능 선택 버튼
3. **점자 사전** (`CameraActivity`) – 이미지 촬영 및 점자 음성 안내
4. **음성 인식** (`VtoBActivity`) – 음성 입력 후 점자 변환·출력
5. **퀴즈** (`QuizActivity`) – 점자 퀴즈 진행 및 결과 안내


---


## 기능 상세

### 1. 점자 사전 (카메라 → 음성)

* **점자 인식 및 변환**: YOLO + CNN 기반 인공지능 + 변환 알고리즘
* **음성 안내**: `TextToSpeech` API

### 2. 음성 인식 → 점자 변환 & 출력

* **STT**: Android `SpeechRecognizer`
* **점자 변환**: 변환 알고리즘
* **BLE 전송**: 변환된 벡터를 HM-10 모듈로 전송

### 3. 점자 퀴즈

* **DB 구조**: 글자, 점자, 답
* **출제 로직**: 랜덤 선택 (5문제)
* **응답 처리**: 정·오답 상관없이 음성·점자 출력

### 4. BLE 통신 & 솔레노이드 제어

* **BleManager**: HM-10 초기화 및 연결
* **BleForegroundService**: 백그라운드 연결 유지
* **제어 흐름**: RESET → 현재 점자 재출력, NEXT → 다음 점자 출력, ENTER → 점자 입력 완료

---

## 프로젝트 구조

```
cancan/
├─ android/
│  ├─ app/
|  |  ├─ assets/
│  │  ├─ src/main/java/com/example/cancan/
│  │  └─ res/
|  |     ├─ drawable/
|  |     └─ layout/
│  └─ build.gradle
├─ arduino/
│  └─ cancan_module.ino
├─ .gitignore
└─ README.md
```

---

## 팀원

| 역할                | GitHub                                    |
| ----------------- | ----------------------------------------- |
| 아두이노 모듈 및 머신러닝 모델 개발       | [minseo110](https://github.com/minseo110) |
| 아두이노 모듈 및 머신러닝 모델 개발       | [Choi-Bogyeong](https://github.com/Choi-Bogyeong) |
| 아두이노 모듈 및 Android 앱 개발 및 설계  | [imdohyang](https://github.com/imdohyang) |

---



## 라이선스

MIT © 2025 minseo110


---

## 참고 자료

* **Jumjaro 한글-점자 변환 알고리즘** - Kotlin 포팅: [https://github.com/teamdotsix/jumjaro](https://github.com/teamdotsix/jumjaro)
* **BrailleToKor Python 모듈** - 원본 로직: [https://github.com/Bridge-NOONGIL/BrailleToKor\_Python](https://github.com/Bridge-NOONGIL/BrailleToKor_Python)
* **YOLOv5** - 점자 박스 검출: [https://github.com/ultralytics/yolov5](https://github.com/ultralytics/yolov5)
* **DotNeuralNet 점자 인식 모델** - 학습/테스트 프레임워크: [https://github.com/snoop2head/DotNeuralNet](https://github.com/snoop2head/DotNeuralNet)




