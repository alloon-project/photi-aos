<img src="https://i.postimg.cc/CKk6Z32g/photi.png" alt="photi.png" width="600" />


<h1>
<img src="https://github.com/user-attachments/assets/7e77a0ab-30ef-4e2e-a918-5b061bad157f" alt="photi logo" width="28">
  &nbsp;포티 - photi (Android)
</h1>

### 포토 챌린지로 일상 속 새로운 즐거움을 발견해 보세요!

오늘도 “내일은 꼭!” 다짐했나요?

**Photi**는 사진 한 장으로 하루 목표를 인증하고,  
같은 목표를 가진 사람들과 서로 응원하며 성장할 수 있는 챌린지 기반 커뮤니티 앱입니다.

<br>

## Team-Photi 
- **서비스 소개:** 🍀[Team-Photi](https://octagonal-caboc-47d.notion.site/team-photi)
- **다운로드 링크:** 📲 [Google Play에서 다운로드](https://play.google.com/store/apps/details?id=com.photi.aos&hl=ko)
- **인스타그램:** 📸 [@photi_official](https://www.instagram.com/photi_official/)

<br>

## 기능 소개 
|챌린지 생성|챌린지 인증|챌린지 공유|파티원과 함께 도전 |
|:---:|:---:|:---:|:---:|
|<img src = "https://github.com/user-attachments/assets/82697c93-4045-44ba-a098-3c2408cfff5f" width="200"/>|<img src = "https://github.com/user-attachments/assets/99b9244a-303b-4c1a-afa2-ec22c7f9b7a5" width="200"/>|<img src = "https://github.com/user-attachments/assets/eb34d5a3-9771-476a-a114-4da5f7e4db6e" width="200"/>|<img src = "https://github.com/user-attachments/assets/d40d3e63-66b7-4c19-8d2c-a9ed959668c0" width="200"/>|
<details>
<summary>기능 상세 소개</summary>
<div markdown="1">

### 1일 1인증 📷

하루 한 번, 정해진 시간 안에 즉석 사진으로 인증해요. <br>
매일 쌓이는 기록이 다음 도전을 만드는 동력이 됩니다.

### 챌린지 만들기 🧩

목표·인증 시간·간단한 규칙만 정하면 바로 시작!
초대 코드를 활용해 친구들과 프라이빗 챌린지도 즐길 수 있어요.

### 다양한 챌린지 탐색  🔎

해시태그/검색으로 취향에 맞는 챌린지를 발견하세요.
인기 챌린지로 요즘 유행 중인 도전도 한눈에 확인!

### 파티원과 함께 도전하기 👯‍♀️

파티원과 사진을 공유하고 좋아요/댓글로 응원해요.
나만의 목표 메모를 남겨 서로 동기부여를 높여보세요.

### 인증 사진 공유하기 📲

인스타그램 등 소셜로 나의 챌린지 기록을 손쉽게 공유하고,
더 많은 사람들과 도전의 즐거움을 나눠보세요.
</div>
</details>

<br>

## 프로젝트 구조

<div style="border: 2px solid #4CAF50; padding: 16px; border-radius: 8px; background: #f9f9f9;">

<pre>
com/photi/aos/

├── data/                      # Data 계층
│   ├── enum/                  # 코드 내에서 사용되는 상수 값 정의
│   ├── model/                 # API 응답, DB, UI 데이터 모델 클래스
│   ├── paging/                # 데이터 페이징 처리 로직
│   ├── remote/                # API 통신 (Retrofit, OkHttp)
│   ├── repository/            # 데이터 소스 통합 및 비즈니스 로직 처리
│   └── storage/               # 로컬 데이터 저장 및 관리 (SharedPreference)

├── view/                      # UI 계층
│   ├── activity/              # 액티비티
│   ├── adapter/               # RecyclerView 어댑터
│   ├── fragment/              # 프래그먼트
│   └── ui/                    # UI 구성 요소와 유틸리티 모음
│   │   ├── component/         # 재사용 가능한 UI 컴포넌트 모음
│   │   └── util/              # UI 관련 유틸리티 기능

└── viewmodel/                 # ViewModel 계층  
</pre>

</div>


<br>

## Tech Stack

| Category     | **Tools & Technologies**                   |
|--------------|--------------------------------------------|
| **Languages**| Kotlin, Java                               |
| Architecture | MVVM                                      |
| **Frameworks**| Jetpack (ViewModel, LiveData, Navigation)|
| **Networking**| Retrofit, OkHttp, JWT                      |
| **UI / UX**  | ViewPager, RecyclerView                    |
| **Tools**    | Android Studio, GitHub                      |
| **Collab**   | Figma                                      |
