package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    // 호감표시가 일어난 경우 새로운 Noti 생성
    @Transactional
    public RsData<Notification> afterLikeNotify(LikeablePerson likeablePerson) {
        return make(likeablePerson, "LIKE", 0, null);
    }

    // 호감사유 변경이 일어난 경우 새로운 Noti 생성
    @Transactional
    public RsData<Notification> afterModifyAttractiveTypeNotify(LikeablePerson likeablePerson, int oldAttractiveType) {
        return make(likeablePerson, "ModifyAttractiveType", oldAttractiveType, likeablePerson.getFromInstaMember().getGender());
    }

    private RsData<Notification> make(LikeablePerson likeablePerson, String typeCode, int oldAttractiveTypeCode, String oldGender) {
        Notification notification = Notification.builder()
                .typeCode(typeCode)
                .toInstaMember(likeablePerson.getToInstaMember())
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .oldAttractiveTypeCode(oldAttractiveTypeCode)
                .oldGender(oldGender)
                .newAttractiveTypeCode(likeablePerson.getAttractiveTypeCode())
                .newGender(likeablePerson.getFromInstaMember().getGender())
                .build();

        notificationRepository.save(notification);

        return RsData.of("S-1", "알람 메시지가 생성되었습니다.", notification);
    }

    public List<Notification> findByToInstaMember(InstaMember toInstaMember){
        return notificationRepository.findByToInstaMember(toInstaMember);
    }
}
