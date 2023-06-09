# 1Week_SongChanHo.md

## Title: [1Week] 송찬호

### 미션 요구사항 분석 & 체크리스트

---

- [x] 호감상대 삭제 기능 (필수)

  - [x] 호감상대 삭제 URL 설정
  - [x] ~~삭제 폼 설정~~
  - [x] 삭제 버튼 누를 시 대상 삭제
  - [x] 삭제를 할 때 회원이 아닌 호감대상 정보가 DB 상에서 유지
  - [x] 호감상대 삭제 후 호감목록으로 redirect
  - [x] ~~만일 남아있는 호감상대가 없는 경우 메인으로 redirect~~

- [x] 구글 로그인 기능 (선택)

  - [x] GCP 프로젝트 생성
  - [x] OAuth 동의 화면 설정
  - [x] 클라이언트 ID 설정
  - [x] ~~로그인 API 구현~~
  - [x] API key 숨기기

### 1주차 미션 요약

---

**[접근 방법]**

- 호감상대 삭제의 경우 "/likeablePerson/delete/{id}" 의 형식으로 PathVariable로 id를 받는 기존의 list.html의 형식을 유지하였다.
- 기존 호감상대 추가 때와 같이 DeleteForm을 사용하려 하였으나 불필요하다 생각하여 취소하였다.
- 삭제 요청을 할 때 DB상의 likeable_person에서만 삭제하고 insta_member에서는 유지한다.
- 삭제 요청을 완료한 후에 기존엔 호감상대가 없는 경우 메인으로 리다이렉션을 하려했으나 사용자 입장에서 자신의 호감상대 리스트가 비어있는 것을 확인하는 것이 더 낫다고 판단하여 호감상대 목록 페이지로 리다이렉션을 걸었다.


**[특이사항]**

- 구글 로그인 API 설정 후 400:redirect_uri_matching 발생
  - 리다이렉션 URI은 OAuth 동의 화면이 아닌 사용자 인증 정보에서 설정해야하는데 착각하였음
- Google 계정으로 로그인까지는 되었으나 실제로 로그인이 되지 않고 "~/login?error" 로 리다이렉션
  - SQL 로그 조차 뜨지 않는 것을 보아 구글 로그인 API 설정 관련 문제라고 판단
  - yml에서 google의 registration scope를 따로 설정해주니 해결



  **[Refactoring]**

    - API Secret Key들을 은닉한 것처럼 DB의 아이디/패스워드 또한 은닉
    - LikeablePersonService에서 사용하는 findById를 메소드화하여 재사용성 증가
    - /delete/{id}의 PathVariable의 타입을 String에서 DB와 같은 int로 변경