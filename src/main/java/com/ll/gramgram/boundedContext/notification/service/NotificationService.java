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
        Notification notification = Notification
                .builder()
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .toInstaMember(likeablePerson.getToInstaMember())
                .typeCode("Like")
                .build();

        notificationRepository.save(notification);

        return RsData.of("S-1", "알람 생성 완료", notification);
    }

    // 호감사유 변경이 일어난 경우 새로운 Noti 생성
    @Transactional
    public RsData<Notification> afterModifyAttractiveTypeNotify(LikeablePerson likeablePerson, int oldAttractiveType) {
        Notification notification = Notification
                .builder()
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .toInstaMember(likeablePerson.getToInstaMember())
                .typeCode("ModifyAttractiveType")
                .oldAttributeTypeCode(oldAttractiveType)
                .newAttractiveTypeCode(likeablePerson.getAttractiveTypeCode())
                .build();

        notificationRepository.save(notification);

        return RsData.of("S-1", "알람 생성 완료", notification);
    }

    public List<Notification> findByToInstaMember(InstaMember toInstaMember){
        return notificationRepository.findByToInstaMember(toInstaMember);
    }
}
