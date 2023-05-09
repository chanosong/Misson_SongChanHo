package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.baseEntity.appConfig.AppConfig;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import com.ll.gramgram.standard.util.UtTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class LikeablePersonServiceTests {
    @Autowired
    private LikeablePersonService likeablePersonService;
    @Autowired
    private LikeablePersonRepository likeablePersonRepository;
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("설정파일에서 호감표시에 대한 수정쿨타임 가져오기")
    void t001() throws Exception {
        System.out.println("likeablePersonModifyCoolTime : " + AppConfig.getLikeablePersonModifyCoolTime());
        assertThat(AppConfig.getLikeablePersonModifyCoolTime()).isGreaterThan(0);
    }

    @Test
    @DisplayName("호감표시를 하면 쿨타임이 지정된다.")
    void t002() throws Exception {
        LocalDateTime coolTime = AppConfig.genLikeablePersonModifyUnlockDate();

        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();
        LikeablePerson likeablePersonToBts = likeablePersonService.like(memberUser3, "bts", 3).getData();

        assertThat(
                likeablePersonToBts.getModifyUnlockDate().isAfter(coolTime)
        ).isTrue();
    }

    @Test
    @DisplayName("호감사유를 변경하면 쿨타임이 갱신된다.")
    void t003() throws Exception {
        // 현재시점 기준에서 쿨타임이 다 차는 시간을 구한다.(미래)
        LocalDateTime coolTime = AppConfig.genLikeablePersonModifyUnlockDate();

        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();
        // 호감표시를 생성한다.
        LikeablePerson likeablePersonToBts = likeablePersonService.like(memberUser3, "bts", 3).getData();

        // 호감표시를 생성하면 쿨타임이 지정되기 때문에, 그래서 바로 수정이 안된다.
        // 그래서 강제로 쿨타임이 지난것으로 만든다.
        // 테스트를 위해서 억지로 값을 넣는다.
        UtTest.setFieldValue(likeablePersonToBts, "modifyUnlockDate", LocalDateTime.now().minusSeconds(1));

        // 수정을 하면 쿨타임이 갱신된다.
        likeablePersonService.modifyAttractive(memberUser3, likeablePersonToBts, 1);

        // 갱신 되었는지 확인
        assertThat(
                likeablePersonToBts.getModifyUnlockDate().isAfter(coolTime)
        ).isTrue();
    }

    @Test
    @DisplayName("쿨타임이 다 지나지 않은 경우 호감표시를 통해 사유 변경 불가")
    void t004() throws Exception {

        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();
        // 호감표시 생성
        likeablePersonService.like(memberUser3, "bts", 3).getData();

        // 다시 한번 호감표시를 다른 사유로 생성한다.
        RsData<LikeablePerson> likeablePersonRsData = likeablePersonService.like(memberUser3, "bts", 1);

        // 실패했는지 확인
        assertThat(
                likeablePersonRsData.isFail()
        ).isTrue();
    }

    @Test
    @DisplayName("쿨타임이 다 지나지 않은 경우 호감사유변경에서 사유 변경 불가")
    void t005() throws Exception {
        
        Member memberUser3 = memberService.findByUsername("user3").orElseThrow();
        // 호감표시 생성
        LikeablePerson likeablePersonToBts = likeablePersonService.like(memberUser3, "bts", 3).getData();

        // 호감사유 변경 시도
        RsData<LikeablePerson> likeablePersonRsData = likeablePersonService.modifyLike(memberUser3, likeablePersonToBts.getId(), 1);

        // 갱신 실패 확인
        assertThat(
                likeablePersonRsData.isFail()
        ).isTrue();
    }
}
