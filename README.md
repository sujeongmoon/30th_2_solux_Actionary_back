<a href="https://actionary.site/" target="_blank">
<img width="3520" height="3520" alt="actionary_main" src="https://github.com/user-attachments/assets/e14f3537-fc79-4fb9-8169-2a1e0c4e1bb8" alt="배너" width="100%"/>
</a>


<br/>
<br/>

---

[BE 개발 정리 노션 링크](https://delicate-dish-b60.notion.site/Back-End-259a4c0c427280ab93fdd108790f4b75?source=copy_link)

---

[ERD 링크](https://www.erdcloud.com/d/knbderZkaaRFLL2Rb)
* ERD
  <img width="1686" height="724" alt="image" src="https://github.com/user-attachments/assets/89deb4af-5984-482b-a3d3-6412ad2742ee" />

---

[API 명세서 노션 링크](https://delicate-dish-b60.notion.site/API-291a4c0c42728018b399fd9b1fd5638c?source=copy_link)
* API 명세서 (일부)
  <img width="1856" height="930" alt="image" src="https://github.com/user-attachments/assets/1c96c52a-5980-439f-8139-1500b8a17b97" />

---

# 0. Getting Started (시작하기)

* 개발환경

```bash
docker compose up -d --build
```

* 운영환경

```bash
docker compose -f docker-compose.yml -f docker-compose-prod.yml down -v
docker compose -f docker-compose.yml -f docker-compose-prod.yml pull
docker compose -f docker-compose.yml -f docker-compose-prod.yml up -d
docker image prune -f
```

[서비스 링크](https://actionary.site/)

<br/>
<br/>

# 1. Project Overview (프로젝트 개요)

- 프로젝트 이름: Actionary
- 프로젝트 설명: 모든 갓생 습관을 한 곳에 담은 올인원 생산성 플랫폼

<br/>
<br/>

# 2. Team Members (팀원 및 팀 소개)

|                                           문수정                                           |                                           백다현                                           |                                           강나현                                           |  
|:---------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------:|
| <img src="https://avatars.githubusercontent.com/u/163665929?v=4" alt="문수정" width="150"> | <img src="https://avatars.githubusercontent.com/u/110024521?v=4" alt="백다현" width="150"> | <img src="https://avatars.githubusercontent.com/u/163988792?v=4" alt="강나현" width="150"> | 
|                                        BE Leader                                        |                                           BE                                            |                                           BE                                            |  
|                        [GitHub](https://github.com/sujeongmoon)                         |                        [GitHub](https://github.com/dahyeon1216)                         |                         [GitHub](https://github.com/NaHyeon647)                         |  

<br/>
<br/>

# 3. Key Features (주요 기능)

- **회원가입**:
    - 회원가입 시 DB에 유저정보가 등록됩니다.

- **로그인**:
    - 사용자 인증[build](build) 정보를 통해 로그인합니다.

- **스터디**:
    - 사용자가 스터디를 생성하고 참여할 수 있는 실시간 화상 스터디 기능입니다.
    - 스터디 내에서 카메라와 마이크 기능을 제공하며 스터디 내 채팅을 제공합니다.
    - 스터디 내에서 뽀모도로 기능을 실행할 수 있으며 공부 시간과 쉬는 시간을 관리할 수 있습니다.

- **투두 리스트**:
    - 오늘 할 일들에 대한 카테고리와 투두리스트 생성을 제공합니다.
    - 캘린더에서 투두리스트 달성 개수와 달성률을 아이콘으로 한눈에 확인할 수 있습니다.

- **게시판**:
    - 말머리를 선택하여 자유롭게 게시글을 생성, 수정, 삭제할 수 있습니다.
    - 일반 댓글과 비밀 댓글 기능을 제공하여 사용자들과 소통할 수 있습니다.

- **Ai 요약 서비스**:
    - 파일을 제공하면 AI가 핵심 내용을 요약합니다.

- **사이드바**:
    - 사이드바로 오늘의 투두리스트, 공부시간과 누적 포인트를 간편하게 조회할 수 있습니다.
    - 사이드바 내 알림창을 통하여 본인에게 온 알림들을 확인할 수 있습니다.

- **마이페이지**:
    - 본인의 개인 정보 수정이 가능합니다.
    - 누적 포인트와 포인트에 따른 배지 조회가 가능합니다
    - 자주 사용하는 사이트는 북마크 등록을 통해 바로 이동 가능합니다.
      <br/>
      <br/>

# 4. Tasks & Responsibilities (작업 및 역할 분담)

|     |                                                                                         |                                                                          |
|-----|-----------------------------------------------------------------------------------------|--------------------------------------------------------------------------|
| 문수정 | <img src="https://avatars.githubusercontent.com/u/163665929?v=4" alt="문수정" width="100"> | <ul><li>스터디</li><li>스터디 참여</li><li>북마크</li><li>공부량</li><li>인프라</li></ul> |
| 백다현 | <img src="https://avatars.githubusercontent.com/u/110024521?v=4" alt="백다현" width="100"> | <ul><li>포인트</li><li>투두리스트</li><li>알림</li><li>검색/탐색</li><li>GPT</li></ul> |
| 강나현 | <img src="https://avatars.githubusercontent.com/u/163988792?v=4" alt="강나현" width="100"> | <ul><li>회원가입/로그인</li><li>게시글</li><li>개인</li></ul>                        |

<br/>
<br/>

# 5. Technology Stack (기술 스택)

<img width="1920" height="1080" alt="Slide 16_9 - 10" src="https://github.com/user-attachments/assets/52865987-be3b-426d-ae2c-7970ef864815" />

<br/>
<br/>

# 6. Project Structure (프로젝트 구조)

```plaintext
.
├── .github/
│   ├── ISSUE_TEMPLATE/                                           # github Issue Template
│   ├── workflows/                                                # github actions CI / CD workflows
│   └── PULL_REQUEST_TEMPLATE.md                                  # github PR Template
├── src/main/java/com/req2res/actionarybe/
│   ├── domain/                                                   # DDD 구조
│   │   ├── aisummary/                                            # AI 요약
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── worker/                                           # 비동기 작업 처리
│   │   ├── auth/                                                 # 인증, 인가
│   │   ├── bookmark/                                             # 북마크
│   │   ├── comment/                                              # 댓글
│   │   ├── image/                                                # 이미지
│   │   ├── member/                                               # 회원
│   │   ├── notification/                                         # 알림
│   │   ├── point/                                                # 포인트
│   │   ├── post/                                                 # 게시글
│   │   ├── search/                                               # 검색
│   │   ├── study/                                                # 스터디, 스터디 상호작용, 스터디 참여
│   │   ├── studyTime/                                            # 공부량
│   │   └── todo/                                                 # 투두리스트
│   ├── global/                                                   # 전역
│   │   ├── config/                                               # 설정 (Janus, OpenAI 등)
│   │   ├── event/                                                # 이벤트 기반 통신 관련
│   │   ├── exception/                                            # 전역 예외 처리
│   │   ├── security/                                             # 보안 설정
│   │   └── ...
│   ├── resources/
│   │   ├── application.yml                                       # 개발 환경 설정
│   │   ├── application-prod.yml                                  # 운영 환경 설정
│   │   └── ...
│   └── test/                                                     # http 테스트
├── build.gradle                                                  # 의존성 관리
├── docker-compose.yml                                            # 개발 환경 인프라 (DB, Redis 등)
├── docker-compose-prod.yml                                       # 운영 환경 인프라 (SpringBoot, NGINX등)
├── Dockerfile                                                    # 앱 이미지 빌드 설정
└── ...
```

<br/>
<br/>

# 7. Development Workflow (개발 워크플로우)

## 브랜치 전략 (Branch Strategy)

우리의 브랜치 전략은 Git Flow를 기반으로 하며, 다음과 같은 브랜치를 사용합니다.

- Main Branch
    - 배포 가능한 상태의 코드를 유지합니다.


- Develop Branch
    - main 브랜치에 머지 전, develop 브랜치 기준으로 개발합니다.
    - 배포 가능한 상태의 코드를 유지합니다.


- Issue Branch
    - ```{이슈타입}/{#이슈번호}-이슈이름-영문-형식```으로 작성합니다.
    - 모든 기능 개발은 이 브랜치에서 이루어집니다.
    - 이슈당 하나의 브랜치로 개발합니다.
    - 원격 브랜치는 PR / 머지 후 삭제를 원칙으로 합니다.
    - Issue Branch 예시
        ```
        == ex1
        feat/#1-project-initialization
        
        == ex2
        fix/#40-oauth-fix
        ```

<br/>
<br/>

# 8. Coding Convention

* 네이버 코딩 컨벤션을 사용합니다.

## 명명 규칙

* 클래스명 : 파스칼 케이스
* 메서드 & 변수명 : 카멜 케이스
* 상수명 : 스네이크 케이스

```
public class SummaryService {

    public static final int MAX_LIMIT = 10;

    public void processSummary(String fileName) { ... }
}
```

<br/>

## 폴더 네이밍

폴더 & 패키지명 : 소문자

```
// 소문자
aisummary
```

<br/>
<br/>

# 9. 커밋 컨벤션

## 기본 구조

* ```{타입}: {#이슈번호} - 커밋내용```으로 작성합니다.

<br/>

## type 종류

```
feat : 새로운 기능 추가
fix : 버그 수정
docs : 문서 수정
style : 코드 포맷팅, 세미콜론 누락, 코드 변경이 없는 경우
refactor : 코드 리펙토링
test : 테스트 코드, 리펙토링 테스트 코드 추가
chore : 빌드 업무 수정, 패키지 매니저 수정
```

<br/>

## 커밋 예시

```
== ex1
feat: #27 - 포인트 사용 내역 조회  

== ex2
chore: #1 - application 및 docker-compose.yml 설정
```

<br/>
<br/>
