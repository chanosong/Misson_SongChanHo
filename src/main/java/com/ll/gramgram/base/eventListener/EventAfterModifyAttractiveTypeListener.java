package com.ll.gramgram.base.eventListener;

import com.ll.gramgram.base.event.EventAfterModifyAttractiveType;
import com.ll.gramgram.boundedContext.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// 호감 사유 변경이 일어날 경우 Listener
@RequiredArgsConstructor
@Component
@Transactional
public class EventAfterModifyAttractiveTypeListener implements ApplicationListener<EventAfterModifyAttractiveType> {
    private final NotificationService notificationService;

    @Override
    public void onApplicationEvent(EventAfterModifyAttractiveType eventAfterModifyAttractiveType) {
        notificationService.afterModifyAttractiveTypeNotify(eventAfterModifyAttractiveType.getLikeablePerson(), eventAfterModifyAttractiveType.getOldAttractiveTypeCode());
    }
}
