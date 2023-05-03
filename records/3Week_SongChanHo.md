# 3Week_SongChanHo.md

## Title: [3Week] 송찬호

### 미션 요구사항 분석 & 체크리스트

---

- [ ] 네이버 클라우드 플랫폼을 통한 배포
- [x] 호감표시/호감사유변경 후 개별 호감표시 건에 대해 3시간 쿨타임 적용
    - [x] 호감표시/사유변경 직전 modify_unlock_date 확인
    - [x] modify_unlock_date와 현재 시각 비교 후 적용
    - [x] modify_unlock_date 관련 테스트 코드 작성
    - [x] 남은 쿨타임 반환 함수 구현
    - [x] 남은 쿨타임 호감 목록에서 표시
- [ ] 알림기능 구현 (선택)
    - [x] 호감표시/사유변경 시 Event 생성
    - [x] 이벤트를 처리할 EventListener 생성
    - [x] EventListener에서 notification 생성 후 DB에 저장 (readDate = null)
    - [x] notification/list에서 자신에게 온 모든 알림 표시
    - [ ] 알림을 읽은 경우 readDate 업데이트
    - [ ] notification/list에서 자신에게 온 알림 중 이미 읽은 것을 제외하고 표시
    - [ ] 테스트 코드 작성

### 3주차 미션 요약

---

**[접근 방법]**


**[특이사항]**

**[Refactoring]**

    [진행 중]