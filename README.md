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

## 🛠 적용 내용

- 메인 화면의 기존 Activity(10개) 구조를 MainActivity + Fragment(3개) 구조로 재설계해 화면 구조 단순화
- 전체훈련 / 상세훈련 Fragment 내부를 Jetpack Compose + MVVM으로 구성
- 훈련 실행(12개), 기록 보기(13개) 화면에 MVVM + Repository를 적용해 UI와 데이터 처리 책임 분

---

## ➕ 추가 적용

- 구조 개선 과정에서 의존성 관리 필요성을 느껴 Hilt를 도입, ViewModel·Repository 간 의존성 주입 자동화
- 반복되는 수동 배포 과정을 개선하기 위해 GitHub Actions + Firebase App Distribution 기반 CI/CD 파이프라인 구축

---

## 💡 기대 효과

- 단일 Activity에 혼재되어 있던 UI, Firebase 접근, 상태 관리 로직을 분리해 역할이 명확해짐
- Firebase 접근 로직이 Repository로 분리되면서 UI 로직과 결합도를 낮춰 유지보수성 개선
- 이후 화면 추가나 Compose 전환 시 ViewModel/Repository 재사용이 가능한 구조로 확장성 확보
- Hilt 적용으로 ViewModel/Repository 간 의존성이 명시적으로 관리되어 모듈 교체 및 확장이 용이
- CI/CD 구축으로 빌드·배포 자동화, 개발 흐름의 일관성 확보 
