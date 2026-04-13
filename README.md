# 🧠 Emotion Regulation Training App(Refactoring)

🔗 Original Repository
https://github.com/JinsuPark01/emotionalApp

🔗 README Repository
https://github.com/JinsuPark01/Emotion-Regulation-Training-App

---

## 📌 리팩토링 목적

- 기존 Activity 상속 기반 구조에서 화면 책임이 커지고 재사용과 확장이 어렵다고 판단
- Compose, Fragment, MVVM 구조를 실제 프로젝트에 적용해보며 유지보수 가능한 구조로 개선하는 것을 목표로 함

---

## 🛠 현재 적용 범위

- MainActivity + Fragment 기반 메인 화면 전환 구조 적용
- 전체훈련 / 상세훈련 화면 Compose + MVVM 적용
- 일부 훈련 실행 화면 Activity 유지 상태에서 MVVM + Repository 적용

---

## 💡 기대 효과

- UI / 상태 / 데이터 처리 책임 분리
- Firebase 접근 로직의 분리로 테스트 및 수정 범위 축소
- 이후 기록보기 화면, 상세 연결, Compose 전환 확장 용이

