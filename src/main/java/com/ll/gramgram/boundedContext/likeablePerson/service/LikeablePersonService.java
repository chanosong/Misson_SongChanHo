package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.AttractiveTypeCode;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    @Value("${likeablePerson.from.max}")
    private Long maxFrom;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if ( member.hasConnectedInstaMember() == false ) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();
        InstaMember fromInstaMember = member.getInstaMember();

        // Optional로 from - to 관계 반환
        Optional<LikeablePerson> opLikeablePerson = findByfromIdAndToId(fromInstaMember, toInstaMember);

        // 동일 인물 호감표시 예외처리
        if (opLikeablePerson.isPresent()) {
            return handleDuplicateLikeablePerson(opLikeablePerson.get(), username, attractiveTypeCode);
        }

        // 10명 초과로 호감표시 예외처리
        if (this.findByFromInstaMemberId(fromInstaMember.getId()).size() >= maxFrom) {
            return RsData.of("F-4", "이미 10명의 좋은 인연으로 가득찼습니다.");
        }

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=성격, 3=능력
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        // 양방향 호감표시 적용
        fromInstaMember.addFromLikeablePerson(likeablePerson);
        toInstaMember.addToLikeablePerson(likeablePerson);

        // 호감표시 횟수 증가
        toInstaMember.increaseLikesCount(fromInstaMember.getGender(), attractiveTypeCode);

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    // 호감상대 취소
    @Transactional
    public RsData<LikeablePerson> unlike(Member member, Long id) {

        // 비정상적인 접근 차단
        if (member.hasConnectedInstaMember() == false) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        Optional<LikeablePerson> opLikeablePerson = findById(id);

        // 이미 삭제된 호감 상대일 경우
        if (!opLikeablePerson.isPresent()) {
            return RsData.of("F-3", "이미 삭제된 호감상대입니다.");
        }

        // 권한 체킹
        if (!Objects.equals(member.getInstaMember().getId(), opLikeablePerson.get().getFromInstaMember().getId())) {
            return RsData.of("F-4", "권한이 없습니다.");
        }

        // 삭제 가능한 상태인 경우 삭제
        LikeablePerson likeablePerson = opLikeablePerson.get();

        likeablePerson.getToInstaMember().decreaseLikesCount(likeablePerson.getFromInstaMember().getGender(), likeablePerson.getAttractiveTypeCode());

        likeablePersonRepository.delete(likeablePerson);

        return RsData.of("S-2", "해당 인스타유저(%s)를 호감상대에서 삭제하였습니다.".formatted(likeablePerson.getToInstaMemberUsername()));
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    public Optional<LikeablePerson> findById(Long id) {
        return likeablePersonRepository.findById(id);
    }

    // 해당 관계가 이미 적용되어있는지 확인
    public Optional<LikeablePerson> findByfromIdAndToId(InstaMember fromInstaMember, InstaMember toInstaMember) {
        return likeablePersonRepository
                .findByFromInstaMemberIdAndToInstaMemberId(fromInstaMember.getId(), toInstaMember.getId());
    }

    // 중복인 경우 호감 타입 코드 비교하여 처리
    public RsData<LikeablePerson> handleDuplicateLikeablePerson(LikeablePerson likeablePerson, String username, int attractiveTypeCode) {
        // 기존 호감 타입 코드
        int beforeTypeCode = likeablePerson.getAttractiveTypeCode();

        // 만일 현재 입력한 코드와 다른 경우
        if (beforeTypeCode != attractiveTypeCode) {
            likeablePerson.updateAttractionTypeCode(attractiveTypeCode);

            return RsData.of("S-2", "%s에 대한 호감사유를 %s에서 %s으로 변경합니다."
                    .formatted(username, AttractiveTypeCode.of(beforeTypeCode),AttractiveTypeCode.of(attractiveTypeCode)), likeablePerson);
        }

        // 현재 입력한 코드와 같은 경우 반려
        return RsData.of("F-3", "해당 사용자에게 이미 호감표시를 하였습니다.");
    }

    @Transactional
    public RsData<LikeablePerson> modifyLike(Member actor, Long id, int attractiveTypeCode) {
        LikeablePerson likeablePerson = findById(id).orElseThrow();

        RsData canModifyRsData = canModifyLike(actor, likeablePerson);

        if (canModifyRsData.isFail()) return canModifyRsData;

        likeablePerson.updateAttractionTypeCode(attractiveTypeCode);

        return RsData.of("S-1", "호감사유를 수정하였습니다.");
    }

    public RsData canModifyLike(Member actor, LikeablePerson likeablePerson) {
        if (!actor.hasConnectedInstaMember()) {
            return RsData.of("F-1", "먼저 본인의 인스타그램 아이디를 입력해주세요.");
        }

        InstaMember fromInstaMember = actor.getInstaMember();

        if (!Objects.equals(likeablePerson.getFromInstaMember().getId(), fromInstaMember.getId())) {
            return RsData.of("F-2", "해당 호감표시를 취소할 권한이 없습니다.");
        }

        return RsData.of("S-1", "호감표시취소가 가능합니다.");
    }
}
