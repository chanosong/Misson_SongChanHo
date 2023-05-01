package com.ll.gramgram.boundedContext.likeablePerson.controller;


import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class LikeablePersonControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private LikeablePersonService likeablePersonService;

    @Test
    @DisplayName("등록 폼(인스타 인증을 안해서 폼 대신 메세지)")
    @WithUserDetails("user1")
    void t001() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/likeablePerson/like"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showLike"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        먼저 본인의 인스타그램 아이디를 입력해주세요.
                        """.stripIndent().trim())))
        ;
    }

    @Test
    @DisplayName("등록 폼")
    @WithUserDetails("user2")
    void t002() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/likeablePerson/like"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showLike"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        <input type="text" name="username"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="1"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="2"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="3"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="submit" value="추가"
                        """.stripIndent().trim())));
        ;
    }

    @Test
    @DisplayName("등록 폼 처리(user2가 user3에게 호감표시(외모))")
    @WithUserDetails("user2")
    void t003() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user3")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());
        ;
    }

    @Test
    @DisplayName("등록 폼 처리(user2가 abcd에게 호감표시(외모), abcd는 아직 우리 서비스에 가입하지 않은상태)")
    @WithUserDetails("user2")
    void t004() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "abcd")
                        .param("attractiveTypeCode", "2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());
        ;
    }

    @Test
    @DisplayName("호감목록")
    @WithUserDetails("user3")
    void t005() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/likeablePerson/list"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showList"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        <span class="toInstaMember_username">insta_user4</span>
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <span class="toInstaMember_attractiveTypeDisplayName">외모</span>
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <span class="toInstaMember_username">insta_user100</span>
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <span class="toInstaMember_attractiveTypeDisplayName">성격</span>
                        """.stripIndent().trim())));
        ;
    }

    @Test
    @DisplayName("호감상대 삭제 (user3이 user4에게 보낸 호감 삭제)")
    @WithUserDetails("user3")
    void t006() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(
                        delete("/likeablePerson/1").with(csrf())
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("호감 삭제 (존재하지 않는 호감 상태 삭제 -> 오류)")
    @WithUserDetails("user3")
    void t007() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        delete("/likeablePerson/100")
                                .with(csrf())
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("호감삭제 (권한 없음 -> 오류)")
    @WithUserDetails("user2")
    void t008() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        delete("/likeablePerson/1")
                                .with(csrf())
                )
                .andDo(print());

        // Then
        resultActions.andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is4xxClientError());

        assertThat(likeablePersonService.findById(1L).isPresent()).isEqualTo(true);
    }

    @Test
    @DisplayName("중복 호감등록 (user3 -> user4)")
    @WithUserDetails("user3")
    void t009() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/likeablePerson/like")
                                .with(csrf())
                                .param("username", "insta_user4")
                                .param("attractiveCodeType", "2")
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    @DisplayName("호감등록 10회 초과 오류 확인 (user2 -> user01 ~ user11")
    @WithUserDetails("user2")
    void t010() throws Exception {
        ResultActions resultActions;
        // 11회 진행
        for (int i = 0; i < 11; i++) {
            // When
            resultActions = mvc
                    .perform(post("/likeablePerson/like")
                            .with(csrf()) // CSRF 키 생성
                            .param("username", "insta_user0" + i)
                            .param("attractiveTypeCode", "" + i % 3)
                    )
                    .andDo(print());
            // Then
            // 1 ~ 10 회까지는
            if (i != 10) {
                resultActions
                        .andExpect(handler().handlerType(LikeablePersonController.class))
                        .andExpect(handler().methodName("like"))
                        .andExpect(status().is3xxRedirection());
            }
            // 11회인 경우 오류
            else {
                resultActions
                        .andExpect(handler().handlerType(LikeablePersonController.class))
                        .andExpect(handler().methodName("like"))
                        .andExpect(status().is4xxClientError());
            }

        }
    }

    @Test
    @DisplayName("user3 -> user4의 attrativeTypeCode 3 -> 2 -> 1 테스팅")
    @WithUserDetails("user3")
    void t011() throws Exception {
        ResultActions resultActions;

        // 기존의 코드 1에서 1 -> 3 -> 2 -> 1 순으로 변경
        for (int i = 3; i > 0; i--) {
            // When
            resultActions = mvc
                    .perform(
                            post("/likeablePerson/like")
                                    .with(csrf())
                                    .param("username","insta_user4")
                                    .param("attractiveTypeCode", "" + i)
                    )
                    .andDo(print());

            // Then
            resultActions
                    .andExpect(handler().handlerType(LikeablePersonController.class))
                    .andExpect(handler().methodName("like"))
                    .andExpect(status().is3xxRedirection());
        }

    }
}
