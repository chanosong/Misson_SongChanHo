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

- 대상에 대한 호감 수정이 가능한지 확인
  - 호감표시로 다른 호감사유를 등록을 하거나 호감사유 변경에서 수정을 시도하는 경우 체크
  - 쿨타임과 비교하여 지정된 시간이 지나지 않았다면 수정 불가
  - 남은 시간의 경우 until() 함수를 이용
- 호감표시/사유수정 시 알람 생성
  - 강 상황 별 EventListener를 생성하여 Event가 발생했는지 확인
  - 각 Listener들은 Event 발생 시 그에 맞는 notification을 생성
  - 어떤 상황에서 발생한 notification인지에 따라 typeCode 지정
  - 알림 리스트를 확인할 때 notification의 typeCode를 확인하고 다른 내용을 제공
  - 알림 리스트를 확인할 때 notification의 readDate를 확인하고 null인 경우만 필터링하여 제공
  - 알림 리스트를 확인했을 때 각 notification들의 readDate 값을 현재 시각으로 수정
  - 알림 리스트에서 제공할 때 사용자에 맞게 데이터를 수정하여 제공 (ex 'M' -> '남자')

**[특이사항]**

- 기존 NotProd에서 초기 데이터값을 생성하려고 했을 때 개인 인스타 아이디와 관련된 아이디를 미리 생성할 때 오류 발생
  - connect와 관련된 문제가 발생한 것이라고 예상
- notification/list에서 알람을 제공할 때 raw한 데이터말고 가공된 것을 보여줘야 하는데 이를 Notification 내부 메소드로 구현을 하는 것이 나은지 고민 중
- 이외 진도가 안나가서 시간 부족으로 진행 하지 못함...

- **[Refactoring]**

    [진행 중]