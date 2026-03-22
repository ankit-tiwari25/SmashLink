package com.project.smashlink.notification.publisher;

import com.project.smashlink.notification.event.UrlEvent;
import com.project.smashlink.notification.observer.UrlEventObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UrlEventPublisher {

    private final List<UrlEventObserver> observers;

    public  void publish(UrlEvent event) {
        log.info("Publishing UrlEvent: {} for shortCode: {}", event.getEventType(), event.getShortCode());
        observers.forEach(observer -> observer.onEvent(event));
    }
}
