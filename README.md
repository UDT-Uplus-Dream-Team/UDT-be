<div align="center">
  <h1> Firefly, OTT 콘텐츠 추천 서비스
</div>

    
<div align="center">
<table>
  
## 👥 백엔드 팀원 소개
  
<tr>
<td align="center">
<a href="https://github.com/dnjstjt1297">
<img src="https://avatars.githubusercontent.com/dnjstjt1297?s=100" width="100px;" alt="dnjstjt1297"/><br/>
<b>dnjstjt1297</b><br/>
김원석
</a>
</td>
<td align="center">
<a href="https://github.com/dudxo">
<img src="https://avatars.githubusercontent.com/dudxo?s=100" width="100px;" alt="dudxo"/><br/>
<b>dudxo</b><br/>
권영태
</a>
</td>
<td align="center">
<a href="https://github.com/hjg727">
<img src="https://avatars.githubusercontent.com/hjg727?s=100" width="100px;" alt="hjg727"/><br/>
<b>hjg727</b><br/>
홍정기
</a>
</td>
<td align="center">
<a href="https://github.com/LGAIN">
<img src="https://avatars.githubusercontent.com/LGAIN?s=100" width="100px;" alt="LGAIN"/><br/>
<b>LGAIN</b><br/>
이가인
</a>
</td>
</tr>
</table>
</div>

## 프로젝트 배경

### 문제 인식

> 1. **OTT 콘텐츠 과잉**
>    - 국내 OTT 시장 규모: 2023년 약 1.4조원.
>    - 20~30대가 전체 이용자의 80% 이상, 구독 플랫폼 평균 2.8개 보유.
>    - 방대한 선택지로 ‘무엇을 볼지’ 고민에 평균 10~20분 이상 소모.
> 2. **통합 추천 서비스 부재**
>    - 플랫폼별 앱을 오가며 콘텐츠를 검색해야 하는 불편.
>    - 기존 서비스(예: 키노라이츠)는 최소 10편 평가 후 추천, 실시간 개인화 한계.
>    - 능동적 피드백 수집의 어려움
> 3. **‘좋아요/싫어요’ 클릭률 저조, 피드백 데이터 부족.**

### 프로젝트 목표 및 기대 효과

### 스트리밍 성향 파악 및 직관적 UI(릴스)를 통해 OTT 콘텐츠를 추천한다. 이를 통해, 탐색 시간을 30초 내로 단축하여 빠르게 유입과 퇴장을 목표로 한다.

### 세부 목표

> 1. **초기 성향 설정**
>    - 회원가입 시 OTT 구독 플랫폼·선호 장르 설문.
> 2. **스와이프 기반 실시간 피드백**
>    - 릴스형 숏폼 UI, 오른쪽 스와이프 좋아요, 왼쪽 스와이프 싫어요, 아래 관심없음.
> 3. **구독 플랫폼 통합 추천**
>    - 구독 중인 OTT 콘텐츠 우선 추천, 미구독 콘텐츠 병행 노출.
> 4. **마이페이지 피드백 수정**
>    - 이전 피드백 히스토리 수정 기능 제공, 추천 알고리즘에 반영.
> 5. **OTT 콘텐츠 필터링해서 보기**

## ERD
<img width="928" height="520" alt="udt_erd" src="https://github.com/user-attachments/assets/e51a8a65-b8a8-4600-9e92-4d72c066c945" />

## 서비스 요청 흐름도


## 시스템 아키텍처
<img width="3536" height="1825" alt="UDT_아키텍처" src="https://github.com/user-attachments/assets/9592939a-7234-48e5-ae22-88b4c4099f45" />


## 기능 소개

### 1️⃣ 로그인

👉 카카오 로그인을 이용해 회원가입 및 로그인을 진행할 수 있다.
<br>
<img width="200" height="260" alt="스크린샷 2025-07-31 오전 3 21 21" src="https://github.com/user-attachments/assets/5fa7e079-494a-4801-a637-1bd6a76fbc2c" />
<br>

### 2️⃣ 설문

👉 처음 가입하는 사용자는 자신이 구독하고 있는 OTT 플랫폼과 선호하는 장르를 추천받을 수 있다.
<br>

### 3️⃣ OTT 콘텐츠 추천

👉 사용자는 자신이 선호하는 장르에 기반한 콘텐츠들의 카드를 넘겨 OTT 콘텐츠들을 추천받을 수 있다.<br>
👉 이때, 추천받은 콘텐츠에 대해 좋아요/싫어요/관심없음 의 피드백을 남길 수 있으며, 이 피드백으로 다음 추천 콘텐츠들이 결정된다.

<br>
<img alt="recommend" src="https://github.com/user-attachments/assets/38567240-aedb-42fe-9cfc-b2cde25bb7fc" width="200" height="260"/>
<br>

### 4️⃣ 엄선된 콘텐츠 추천

👉 20개의 피드백을 남기면 3개의 엄선된 추천 콘텐츠를 확인할 수 있다. <br>
👉 각 콘텐츠 카드 우상단의 새로운 추천 콘텐츠를 확인할 수 있다.

<br>
<img width="200" height="260" alt="curetedcontent" src="https://github.com/user-attachments/assets/25d4d2bb-e1ab-4681-8e0e-9a0738a6717c" />
<br>

### 5️⃣ 찾아보기

👉 찾아보기 탭에서 OTT 콘텐츠들을 확인할 수 있다. <br>
👉 필터링 기능을 통해 OTT / 카테고리 / 장르 / 국가 등으로 콘텐츠들을 필터링할 수 있다. <br>
👉 요일별 추천 콘텐츠를 확인할 수 있다. <br>
👉 인기 콘텐츠를 확인할 수 있다. <br>
<br>
<img width="200" height="260" alt="explore" src="https://github.com/user-attachments/assets/858e7a67-b0a3-4cb8-86fe-44bf8ee16d74" />

### 6️⃣ 마이페이지

👉 마이페이지에서는 사용자의 정보를 확인할 수 있다. <br>
👉 사용자는 회원가입 시 작성한 설문의 내용(구독하고 있는 OTT 플랫폼, 선호 장르)를 확인하고, 수정할 수 있다. <br>
👉 사용자는 자신의 피드백 히스토리를 확인하고, 이전에 남긴 피드백을 수정할 수 있다. <br>

<br>
<img width="200" height="260" alt="profile_edit" src="https://github.com/user-attachments/assets/5e67c7f3-1724-419c-8ef7-5709169f45d8" />
<img width="200" height="260" alt="feedback_delete" src="https://github.com/user-attachments/assets/32232f2f-3ab5-432f-b1d0-85c0c9dc3f8b" />
<img width="200" height="260" alt="feedback_view" src="https://github.com/user-attachments/assets/3e722ec5-b061-450b-bdb3-c4b83d644955" />
<img width="200" height="260" alt="saved" src="https://github.com/user-attachments/assets/a23eae93-a498-4c10-8503-eddf93c5582c" />

### 7️⃣ 관리자 모드

👉 관리자는 ADMIN 페이지에서 콘텐츠를 등록/수정/삭제할 수 있다.
<br>
<br>

### (개발 진행 중) 관리자 기능 고도화

👉 관리자는 삭제/수정한 콘텐츠의 목록을 확인할 수 있다. <br>
👉 관리자는 유저의 피드백 현황을 확인할 수 있다. <br>
👉 관리자는 탐색하기 페이지의 추천 콘텐츠 리스트를 관리할 수 있다. <br>


# Team Rules

## 🌿 브랜치 관리

### 브랜치 구조

- **develop**: 개발용 메인 브랜치
- **prod**: 배포용 브랜치
- **feature**: 기능 개발 브랜치

### 브랜치 네이밍 규칙

```
feat/UDT-22-구현할-기능-기반-네이밍
fix/UDT-22-고쳐야될-부분-네이밍
refactor/UDT-22-리팩토링-부분-네이밍
```

**예시**:

- `feat/UDT-22-회원가입`
- `feat/UDT-22-1차-추천알고리즘`
- `fix/UDT-22-회원가입-검증오류`
- `refactor/UDT-22-dto-record로-변경`

### 개발 플로우

1. develop에서 feature 브랜치 생성
2. 기능 개발 및 커밋
3. PR 생성 및 코드 리뷰
4. develop 브랜치로 머지
5. 배포 시 develop → prod

## 📝 커밋 컨벤션

### 기본 형식

```
UDT-[티켓번호] [타입]: [간단한 설명]
```

### 타입 분류

| 타입       | 설명                      |
| ---------- | ------------------------- |
| `feat`     | 새로운 기능 추가          |
| `fix`      | 버그 수정                 |
| `refactor` | 리팩토링 (기능 변화 없음) |
| `hotfix`   | 빠르게 버그 수정          |
| `docs`     | 문서 수정                 |
| `test`     | 테스트 코드               |
| `chore`    | 빌드, 배포 관련           |

**예시**: `UDT-XXX feat: 사용자 인증 기능 구현`

## 🏗️ 패키지 구조

```
com.example.udtbe/
├── domain/
│   ├── admin/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── exception/
│   │   └── service/
│   ├── auth/
│   ├── content/
│   ├── file/
│   ├── member/
│   └── survey/
├── global/
│   ├── config/
│   ├── dto/
│   ├── entity/
│   ├── exception/
│   ├── log/
│   ├── security/
│   ├── token/
│   └── util/
└── UdtBeApplication
```

## 💻 코딩 컨벤션

### API 네이밍

- **엔드포인트**: `/api/users` 형식
- **컨트롤러 메서드**:
  - 리스트: `getUsers()`
  - 단일: `getUser()`
- **서비스 메서드**: `getUsers()`

### 변수 네이밍

- `findUser` ← `findByUserById()`
- `savedUser` ← `.save()`

### DTO 네이밍 규칙

- **Record 사용**: `Entity(User)~~Request`, `Entity(User)~~Response`
- **생성자**: 정적메서드 사용 (`of`, `fromXX`)
- **참조형 클래스**: `~~DTO` (예: `PlatformDTO`)
- **커서 페이지네이션**: `CursorPageResponse<~~GetResponse>`

### 인증 처리

```java
@GetMapping
public ResponseEntity<?> getUser(@AuthenticationPrincipal Long userId) {
    // 구현
}
```

## ⚙️ 개발 환경 설정

### IntelliJ 설정

#### 1. Google 코드 스타일 적용

- 다운로드: [Google Java Style Guide](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)
- Settings → Editor → Code Style → Java → 구성표 가져오기
- 들여쓰기: 4 4 8 0 고정

#### 2. 자동 포맷팅 설정

- Settings → Tools → Actions on Save
- ✅ Reformat code
- ✅ Optimize imports

## 🚀 CI/CD

### 파이프라인 구조

- **develop**: CI/CD (빌드 및 테스트, 배포)
- **prod**: CD (배포)

## ✅ 개발 원칙

### 레이어 구조

- **Controller** → **Service** → **Repository** 계층 구조 준수
- 각 레이어의 역할과 책임 명확히 분리

### 의존성 주입

- `@RequiredArgsConstructor` 활용한 생성자 주입 권장

### 테스트

- 비즈니스 로직에 대한 단위 테스트 필수
- 통합 테스트를 통한 전체 플로우 검증

---

# 4. 기술적 고민
  ## 4-1. GraphQL vs RestAPI
  * GraphQL 장점: 클라이언트가 필요한 데이터만 정확히 요청 가능해 over-fetch/under-fetch 문제 해소
  * GraphQL 단점: 초기 학습 비용 및 서버·스키마 복잡도 증가, 캐싱 전략이 REST만큼 성숙하지 않음

  * REST API 장점: 표준 HTTP 메서드와 URI 사용, CDN·로드밸런서 등 인프라 캐싱 활용 우수, 구현·디버깅 용이
  * REST API 단점: 고정된 엔드포인트 응답 스펙으로 클라이언트 요구사항 변화 시 over-fetch 발생 가능

  **RESTAPI 방식을 선택했다.**
  * 팀원 모두 GraphQL보다 REST API에 능숙하여 구축·테스트·디버깅에 효율적이라 생각
  * 특히, 로드밸런서등 인프라 캐싱 활용으로 초기 MVP 단계에서 빠른 개발 및 안정적인 성능 확보가 가능하다고 판단해 REST API를 사용

---
