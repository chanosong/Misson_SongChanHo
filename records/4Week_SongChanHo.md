# 4Week_SongChanHo.md

## Title: [4Week] 송찬호

### 미션 요구사항 분석 & 체크리스트

---

- [x] 네이버 클라우드 플랫폼을 통한 배포, 도메인, HTTPS 적용
- [x] 호감리스트 성별 필터링 기능
  - [x] 성별에 따른 호감리스트 반환 기능 구현
  - [x] instaMemberId와 gender 기준 결과 JPQL 쿼리 작성
  - [x] toList 페이지 구현
  - [x] 테스팅
- [ ] 젠킨스 사용하여 main 브랜치에서 커밋 발생 시 자동 배포 (선택)
- [ ] 내가 받은 호감리스트에서 호감사유 필터링 기능 구현
- [ ] 내가 받은 호감리스트 정렬 기능 구현

### 4주차 미션 요약

---

**[접근 방법]**

- 호감리스트 페이지에서 성별 필터링하여 호감표시 기록 제공
  - 최초에 생각한 것은 toList에 처음 들어갈 시 여성들에게 온 호감표시를 보여주고 남성 체크시 남성 기록 또한 볼 수 있게 하는 것으로 이해하고 설계
  - PathVariable을 통해 toList/{gender}의 형태로 검색할 성별을 설정
  - PathVariable이 없는 경우를 감안하여 Optional<String>으로 gender를 받음
  - @Query 를 사용하여 JPQL을 직접 작성, fetch join 실행
  - 테스트 코드 작성

**[특이사항]**

- 나중에 정렬 관련한 기능을 생각하면 PathVariable이 아닌 RequestParam으로 받는 것이 알맞은 것 같다.
- 모든 성별에게 받은 호감표시 기능을 생각하지 않아 나중에 Optional<gender>가 없는 경우 "ALL"로 설정
- 모든 성별에게 받은 호감표시를 받기위해 repository에서 함수 따로 지정

- **[Refactoring]**

    [진행 중]