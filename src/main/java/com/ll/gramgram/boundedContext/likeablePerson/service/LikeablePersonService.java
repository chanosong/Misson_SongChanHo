package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.baseEntity.appConfig.AppConfig;
import com.ll.gramgram.base.event.EventAfterLike;
import com.ll.gramgram.base.event.EventAfterModifyAttractiveType;
import com.ll.gramgram.base.event.EventBeforeCancelLike;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.AttractiveTypeCode;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher publisher;

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
                .modifyUnlockDate(AppConfig.genLikeablePersonModifyUnlockDate()) // 쿨타임 생성
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        // 양방향 호감표시 적용
        fromInstaMember.addFromLikeablePerson(likeablePerson);
        toInstaMember.addToLikeablePerson(likeablePerson);

        // 호감표시 횟수 증가
        publisher.publishEvent(new EventAfterLike(this, likeablePerson));

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

        publisher.publishEvent(new EventBeforeCancelLike(this, likeablePerson));

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

        // 쿨타임 확인 후 지나지 않은 경우 reject
        if (!likeablePerson.isModifyUnlocked()) return RsData.of("F-4", "호감사유 변경 가능 시간이 %s 남았습니다".formatted(likeablePerson.getModifyUnlockDateRemainStrHuman()));

        // 기존 호감 타입 코드
        int beforeTypeCode = likeablePerson.getAttractiveTypeCode();

        // 만일 현재 입력한 코드와 다른 경우
        if (beforeTypeCode != attractiveTypeCode) {
            likeablePerson.updateAttractiveTypeCode(attractiveTypeCode);

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

        modifyAttractiveTypeCode(likeablePerson, attractiveTypeCode);

        return RsData.of("S-1", "호감사유를 수정하였습니다.");
    }

    @Transactional
    public RsData<LikeablePerson> modifyAttractive(Member actor, LikeablePerson likeablePerson, int attractiveTypeCode) {
        RsData canModifyRsData = canModifyLike(actor, likeablePerson);

        if (canModifyRsData.isFail()) return canModifyRsData;

        String oldAttractiveTypeDisplayName = likeablePerson.getAttractiveTypeDisplayName();
        String username = likeablePerson.getToInstaMember().getUsername();

        modifyAttractiveTypeCode(likeablePerson, attractiveTypeCode);

        String newAttractiveTypeDisplayName = likeablePerson.getAttractiveTypeDisplayName();

        return RsData.of("S-3", "%s님에 대한 호감사유를 %s에서 %s(으)로 변경합니다.".formatted(username, oldAttractiveTypeDisplayName, newAttractiveTypeDisplayName), likeablePerson);
    }

    private RsData<LikeablePerson> modifyAttractive(Member actor, String username, int attractiveTypeCode) {
        // actor가 생성한 좋아요 가져오기
        List<LikeablePerson> formLikeablePeople = actor.getInstaMember().getFromLikeablePeople();

        LikeablePerson fromLikeablePerson = formLikeablePeople
                .stream()
                .filter(e -> e.getToInstaMember().getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (fromLikeablePerson == null) return RsData.of("F-7", "호감표시를 하지 않았습니다.");

        return modifyAttractive(actor, fromLikeablePerson, attractiveTypeCode);
    }

    private void modifyAttractiveTypeCode(LikeablePerson likeablePerson, int attractiveTypeCode) {
        int oldAttractiveTypeCode = likeablePerson.getAttractiveTypeCode();

        RsData rsData = likeablePerson.updateAttractiveTypeCode(attractiveTypeCode);

        if (rsData.isSuccess()) {
            publisher.publishEvent(new EventAfterModifyAttractiveType(this, likeablePerson, oldAttractiveTypeCode, attractiveTypeCode));
        }
    }

    public RsData canModifyLike(Member actor, LikeablePerson likeablePerson) {
        if (!actor.hasConnectedInstaMember()) {
            return RsData.of("F-1", "먼저 본인의 인스타그램 아이디를 입력해주세요.");
        }

        InstaMember fromInstaMember = actor.getInstaMember();

        if (!Objects.equals(likeablePerson.getFromInstaMember().getId(), fromInstaMember.getId())) {
            return RsData.of("F-2", "해당 호감표시를 취소할 권한이 없습니다.");
        }

        // 권한이 있는 경우 쿨타임이 지났는지 확인
        if (!likeablePerson.isModifyUnlocked()) return RsData.of("F-4", "호감사유 변경 가능 시간이 %s 남았습니다".formatted(likeablePerson.getModifyUnlockDateRemainStrHuman()));

        return RsData.of("S-1", "호감표시취소가 가능합니다.");
    }

    public RsData getReceivedLikeByGender(InstaMember instaMember, String gender) {
        
        // toInstaMember의 성별을 기준으로 호감표시 기록 로드
        List<LikeablePerson> likeablePersonList = likeablePersonRepository.findByToInstaMemberIdAndFromInstaMemberGender(instaMember.getId(), gender);

        return RsData.of("S-1", "%s에게서 받은 호감 표시입니다.".formatted(gender), likeablePersonList);
    }
}
