package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.baseEntity.appConfig.AppConfig;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
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
}
