package com.ll.gramgram.base.eventListener;

import com.ll.gramgram.base.event.EventAfterLike;
import com.ll.gramgram.boundedContext.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

// 새로운 호감 등록이 일어난 경우 Listener
@RequiredArgsConstructor
@Component
public class EventAfterLikeListener implements ApplicationListener<EventAfterLike> {

    private final NotificationService notificationService;

    @Override
    public void onApplicationEvent(EventAfterLike eventAfterLike) {
        notificationService.afterLikeNotify(eventAfterLike.getLikeablePerson());
    }
}
